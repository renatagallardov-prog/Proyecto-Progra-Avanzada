package com.parquetematico.ui;

import com.parquetematico.controlador.ParqueController;
import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Grupo;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SIA-10: modalidad de ventana (Swing).
 *
 * SIA-O4 (MVC): esta clase es la Vista. Solo le habla al ParqueController,
 * nunca a GestorParque o PersistenciaCSV directamente.
 *
 * La ventana quedo con tablas (mas facil de leer que una lista de texto),
 * una barra de estado abajo que va explicando que hacer, y una pestaña de
 * estadisticas con un grafico de barras (SIA-O1) y el boton para exportar
 * la planilla (SIA-O2).
 */
public class VentanaPrincipal extends JFrame {

    private final ParqueController controlador;

    // Guardo las listas "reales" en paralelo a las tablas, para saber que
    // Atraccion/Reserva corresponde a la fila que el usuario selecciono.
    private List<Atraccion> atraccionesEnTabla = new ArrayList<>();
    private List<Reserva> reservasEnTabla = new ArrayList<>();

    private final DefaultTableModel modeloAtracciones = new DefaultTableModel(
            new Object[]{"Codigo", "Nombre", "Tipo", "Capacidad", "Horario", "N° Reservas"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la tabla es solo para mostrar, se edita con los botones
        }
    };

    private final DefaultTableModel modeloReservas = new DefaultTableModel(
            new Object[]{"ID", "Responsable", "Personas", "Fecha", "Hora", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private final JTable tablaAtracciones = new JTable(modeloAtracciones);
    private final JTable tablaReservas = new JTable(modeloReservas);
    private final JLabel etiquetaAtraccionSeleccionada = new JLabel("Selecciona una atraccion en la pestaña 'Atracciones' para ver sus reservas aqui.");
    private final JLabel barraEstado = new JLabel("Listo. Elige una pestaña para empezar.");
    private final PanelGraficoBarras panelGrafico = new PanelGraficoBarras();
    private final JTextArea areaReporte = new JTextArea(8, 40);

    public VentanaPrincipal(ParqueController controlador) {
        super("Sistema Parque Tematico");
        this.controlador = controlador;

        construirMenu();
        construirUI();
        refrescarTablaAtracciones();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);
    }

    private void construirMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemGuardar = new JMenuItem("Guardar ahora");
        itemGuardar.addActionListener(e -> {
            controlador.guardarTodo();
            mostrarEstado("Datos guardados en la carpeta 'data'.");
        });

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> {
            controlador.guardarTodo();
            dispose();
        });

        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void construirUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1. Atracciones", panelAtracciones());
        tabs.addTab("2. Reservas", panelReservas());
        tabs.addTab("3. Estadisticas y ocupacion", panelEstadisticas());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        barraEstado.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        barraEstado.setOpaque(true);
        barraEstado.setBackground(new Color(230, 230, 230));
        add(barraEstado, BorderLayout.SOUTH);
    }

    private void mostrarEstado(String mensaje) {
        barraEstado.setText(mensaje);
    }

    // ==================== PESTAÑA 1: ATRACCIONES ====================

    private JPanel panelAtracciones() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel titulo = new JLabel("Estas son todas las atracciones del parque. Haz clic en una fila para ver sus reservas en la pestaña 2.");
        panel.add(titulo, BorderLayout.NORTH);

        tablaAtracciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAtracciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refrescarTablaReservas();
            }
        });
        panel.add(new JScrollPane(tablaAtracciones), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar atraccion");
        JButton btnModificar = new JButton("Modificar seleccionada");
        JButton btnEliminar = new JButton("Eliminar seleccionada");
        botones.add(btnAgregar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> dialogoAgregarAtraccion());
        btnModificar.addActionListener(e -> dialogoModificarAtraccion());
        btnEliminar.addActionListener(e -> {
            Atraccion seleccionada = obtenerAtraccionSeleccionada();
            if (seleccionada == null) {
                mostrarEstado("Primero selecciona una atraccion en la tabla.");
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que quieres eliminar '" + seleccionada.getNombre() + "'?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    controlador.eliminarAtraccion(seleccionada.getCodigo());
                    refrescarTablaAtracciones();
                    mostrarEstado("Atraccion eliminada.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private Atraccion obtenerAtraccionSeleccionada() {
        int fila = tablaAtracciones.getSelectedRow();
        if (fila < 0 || fila >= atraccionesEnTabla.size()) {
            return null;
        }
        return atraccionesEnTabla.get(fila);
    }

    private void dialogoAgregarAtraccion() {
        JTextField codigo = new JTextField();
        JTextField nombre = new JTextField();
        JComboBox<TipoAtraccion> tipo = new JComboBox<>(TipoAtraccion.values());
        JTextField capacidad = new JTextField();
        JTextField apertura = new JTextField("10:00");
        JTextField cierre = new JTextField("18:00");

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Codigo (unico):")); form.add(codigo);
        form.add(new JLabel("Nombre:")); form.add(nombre);
        form.add(new JLabel("Tipo:")); form.add(tipo);
        form.add(new JLabel("Capacidad maxima (personas):")); form.add(capacidad);
        form.add(new JLabel("Hora apertura (HH:mm):")); form.add(apertura);
        form.add(new JLabel("Hora cierre (HH:mm):")); form.add(cierre);

        int res = JOptionPane.showConfirmDialog(this, form, "Nueva atraccion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                Atraccion a = new Atraccion(codigo.getText().trim(), nombre.getText().trim(),
                        (TipoAtraccion) tipo.getSelectedItem(), Integer.parseInt(capacidad.getText().trim()),
                        LocalTime.parse(apertura.getText().trim()), LocalTime.parse(cierre.getText().trim()), true);
                controlador.agregarAtraccion(a);
                refrescarTablaAtracciones();
                mostrarEstado("Atraccion '" + a.getNombre() + "' agregada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Revisa los datos ingresados: " + ex.getMessage(),
                        "Datos invalidos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogoModificarAtraccion() {
        Atraccion seleccionada = obtenerAtraccionSeleccionada();
        if (seleccionada == null) {
            mostrarEstado("Primero selecciona una atraccion en la tabla.");
            return;
        }
        JTextField nombre = new JTextField(seleccionada.getNombre());
        JComboBox<TipoAtraccion> tipo = new JComboBox<>(TipoAtraccion.values());
        tipo.setSelectedItem(seleccionada.getTipo());
        JTextField capacidad = new JTextField(String.valueOf(seleccionada.getCapacidadMaxima()));

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Nombre:")); form.add(nombre);
        form.add(new JLabel("Tipo:")); form.add(tipo);
        form.add(new JLabel("Capacidad maxima:")); form.add(capacidad);

        int res = JOptionPane.showConfirmDialog(this, form, "Modificar " + seleccionada.getCodigo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                controlador.modificarAtraccion(seleccionada.getCodigo(), nombre.getText().trim(),
                        (TipoAtraccion) tipo.getSelectedItem(), Integer.parseInt(capacidad.getText().trim()));
                refrescarTablaAtracciones();
                mostrarEstado("Atraccion modificada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Revisa los datos: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refrescarTablaAtracciones() {
        atraccionesEnTabla = controlador.listarAtracciones();
        modeloAtracciones.setRowCount(0);
        for (Atraccion a : atraccionesEnTabla) {
            modeloAtracciones.addRow(new Object[]{
                    a.getCodigo(), a.getNombre(), a.getTipo(), a.getCapacidadMaxima(),
                    a.getHoraApertura() + " - " + a.getHoraCierre(), a.getReservas().size()
            });
        }
        refrescarTablaReservas();
    }

    // ==================== PESTAÑA 2: RESERVAS ====================

    private JPanel panelReservas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(etiquetaAtraccionSeleccionada, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar reserva");
        JButton btnCambiarEstado = new JButton("Cambiar estado");
        JButton btnEliminar = new JButton("Eliminar reserva");
        botones.add(btnAgregar);
        botones.add(btnCambiarEstado);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> dialogoAgregarReserva());
        btnCambiarEstado.addActionListener(e -> dialogoCambiarEstadoReserva());
        btnEliminar.addActionListener(e -> {
            Atraccion a = obtenerAtraccionSeleccionada();
            Reserva r = obtenerReservaSeleccionada();
            if (a == null || r == null) {
                mostrarEstado("Selecciona primero una atraccion (pestaña 1) y una reserva (arriba).");
                return;
            }
            try {
                controlador.eliminarReserva(a.getCodigo(), r.getIdReserva());
                refrescarTablaAtracciones();
                mostrarEstado("Reserva eliminada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private Reserva obtenerReservaSeleccionada() {
        int fila = tablaReservas.getSelectedRow();
        if (fila < 0 || fila >= reservasEnTabla.size()) {
            return null;
        }
        return reservasEnTabla.get(fila);
    }

    private void dialogoAgregarReserva() {
        Atraccion a = obtenerAtraccionSeleccionada();
        if (a == null) {
            mostrarEstado("Primero ve a la pestaña 'Atracciones' y selecciona una.");
            return;
        }
        JTextField id = new JTextField();
        JTextField rut = new JTextField();
        JTextField nombreResp = new JTextField();
        JTextField contacto = new JTextField();
        JTextField personas = new JTextField();
        JTextField fecha = new JTextField(LocalDate.now().plusDays(1).toString());
        JTextField hora = new JTextField("11:00");

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Id reserva:")); form.add(id);
        form.add(new JLabel("RUT responsable:")); form.add(rut);
        form.add(new JLabel("Nombre responsable:")); form.add(nombreResp);
        form.add(new JLabel("Contacto:")); form.add(contacto);
        form.add(new JLabel("Cantidad de personas:")); form.add(personas);
        form.add(new JLabel("Fecha (yyyy-MM-dd):")); form.add(fecha);
        form.add(new JLabel("Hora (HH:mm):")); form.add(hora);

        int res = JOptionPane.showConfirmDialog(this, form, "Nueva reserva para " + a.getNombre(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int cantidad = Integer.parseInt(personas.getText().trim());
                Grupo g = new Grupo(rut.getText().trim(), nombreResp.getText().trim(), contacto.getText().trim(), cantidad);
                Reserva r = new Reserva(id.getText().trim(), a.getCodigo(), g, LocalDate.parse(fecha.getText().trim()),
                        LocalTime.parse(hora.getText().trim()), cantidad, EstadoReserva.PENDIENTE);
                controlador.agregarReserva(a.getCodigo(), r);
                refrescarTablaAtracciones();
                mostrarEstado("Reserva agregada para " + a.getNombre() + ".");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo agregar la reserva: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogoCambiarEstadoReserva() {
        Atraccion a = obtenerAtraccionSeleccionada();
        Reserva r = obtenerReservaSeleccionada();
        if (a == null || r == null) {
            mostrarEstado("Selecciona una atraccion y una reserva primero.");
            return;
        }
        JComboBox<EstadoReserva> combo = new JComboBox<>(EstadoReserva.values());
        combo.setSelectedItem(r.getEstado());
        int res = JOptionPane.showConfirmDialog(this, combo, "Nuevo estado para " + r.getIdReserva(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                controlador.modificarReserva(a.getCodigo(), r.getIdReserva(), r.getCantidadPersonas(),
                        (EstadoReserva) combo.getSelectedItem());
                refrescarTablaReservas();
                mostrarEstado("Estado de la reserva actualizado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refrescarTablaReservas() {
        Atraccion seleccionada = obtenerAtraccionSeleccionada();
        modeloReservas.setRowCount(0);
        reservasEnTabla = new ArrayList<>();

        if (seleccionada == null) {
            etiquetaAtraccionSeleccionada.setText("Selecciona una atraccion en la pestaña 'Atracciones' para ver sus reservas aqui.");
            return;
        }

        etiquetaAtraccionSeleccionada.setText("Reservas de: " + seleccionada.getNombre()
                + "  (capacidad maxima: " + seleccionada.getCapacidadMaxima() + " personas)");

        reservasEnTabla = seleccionada.getReservas();
        for (Reserva r : reservasEnTabla) {
            modeloReservas.addRow(new Object[]{
                    r.getIdReserva(), r.getGrupo().getNombreResponsable(), r.getCantidadPersonas(),
                    r.getFecha(), r.getHora(), r.getEstado()
            });
        }
    }

    // ==================== PESTAÑA 3: ESTADISTICAS (SIA-9, SIA-O1, SIA-O2) ====================

    private JPanel panelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTextField campoDesde = new JTextField(LocalDate.now().toString(), 10);
        JTextField campoHasta = new JTextField(LocalDate.now().plusDays(7).toString(), 10);
        JButton btnGenerar = new JButton("Generar reporte y grafico");
        JButton btnExportar = new JButton("Exportar planilla (Excel/CSV)");

        JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaSuperior.add(new JLabel("Desde:"));
        filaSuperior.add(campoDesde);
        filaSuperior.add(new JLabel("Hasta:"));
        filaSuperior.add(campoHasta);
        filaSuperior.add(btnGenerar);
        filaSuperior.add(btnExportar);
        panel.add(filaSuperior, BorderLayout.NORTH);

        panelGrafico.setPreferredSize(new Dimension(400, 300));
        panelGrafico.setBorder(BorderFactory.createTitledBorder("Ocupacion por atraccion (SIA-O1)"));

        areaReporte.setEditable(false);
        areaReporte.setBorder(BorderFactory.createTitledBorder("Detalle (SIA-9)"));

        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.add(panelGrafico, BorderLayout.CENTER);
        centro.add(new JScrollPane(areaReporte), BorderLayout.SOUTH);
        panel.add(centro, BorderLayout.CENTER);

        btnGenerar.addActionListener(e -> {
            try {
                LocalDate desde = LocalDate.parse(campoDesde.getText().trim());
                LocalDate hasta = LocalDate.parse(campoHasta.getText().trim());
                areaReporte.setText(controlador.generarReporteOcupacion(desde, hasta));
                Map<String, Double> datos = controlador.calcularOcupacionPorAtraccion(desde, hasta);
                panelGrafico.actualizarDatos(datos);
                mostrarEstado("Reporte generado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Revisa el formato de las fechas (yyyy-MM-dd).",
                        "Fechas invalidas", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExportar.addActionListener(e -> {
            try {
                LocalDate desde = LocalDate.parse(campoDesde.getText().trim());
                LocalDate hasta = LocalDate.parse(campoHasta.getText().trim());
                String ruta = controlador.exportarPlanillaOcupacion(desde, hasta);
                JOptionPane.showMessageDialog(this, "Planilla generada en:\n" + ruta
                        + "\n\nSe abre directo con Excel o LibreOffice Calc.");
                mostrarEstado("Planilla exportada a " + ruta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }
}
