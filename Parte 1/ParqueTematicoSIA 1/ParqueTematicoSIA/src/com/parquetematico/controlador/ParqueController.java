package com.parquetematico.controlador;

import com.parquetematico.excepciones.CapacidadExcedidaException;
import com.parquetematico.excepciones.ElementoNoEncontradoException;
import com.parquetematico.gestion.DatosIniciales;
import com.parquetematico.gestion.GestorParque;
import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;
import com.parquetematico.persistencia.PersistenciaCSV;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SIA-O4: Este proyecto sigue el patrón Modelo-Vista-Controlador (MVC).
 *
 * - Modelo: las clases del paquete "modelo" (Atraccion, Grupo, Reserva) y la
 *   clase GestorParque, que guarda los datos y las reglas del negocio.
 * - Vista: las clases del paquete "ui" (MenuConsola y VentanaPrincipal).
 *   Estas clases NO conocen a GestorParque ni a PersistenciaCSV directamente,
 *   solo saben pedirle cosas a este controlador.
 * - Controlador: esta misma clase. Recibe las peticiones de la vista, las
 *   traduce en operaciones sobre el modelo, y decide cuándo guardar/cargar
 *   los datos desde el archivo.
 *
 * La idea de separar en capas es que si el día de mañana cambio la consola
 * por la ventana (o agrego una tercera forma de interfaz), no tengo que
 * tocar nada de la lógica de negocio ni de la persistencia.
 */
public class ParqueController {

    private final GestorParque gestor;
    private final PersistenciaCSV persistencia;

    public ParqueController() {
        this.gestor = new GestorParque();
        this.persistencia = new PersistenciaCSV();
    }

    /**
     * Se llama una sola vez, al partir el programa. Si ya existen datos
     * guardados de una ejecución anterior los carga, y si no, deja unos
     * datos de ejemplo para poder probar el sistema altiro.
     */
    public void iniciar() {
        File archivo = new File("data/atracciones.csv");
        if (archivo.exists()) {
            persistencia.cargarDatos(gestor);
        } else {
            DatosIniciales.cargar(gestor);
        }
    }

    /**
     * Se llama al cerrar el programa (desde la consola o desde la ventana)
     * para dejar todo guardado en disco.
     */
    public void guardarTodo() {
        persistencia.guardarDatos(gestor);
    }

    // ---------- operaciones sobre Atracciones (la Vista llama estos metodos) ----------

    public void agregarAtraccion(Atraccion a) {
        gestor.agregarAtraccion(a);
    }

    public List<Atraccion> listarAtracciones() {
        return gestor.listarAtracciones();
    }

    public Atraccion buscarAtraccion(String codigo) throws ElementoNoEncontradoException {
        return gestor.buscarAtraccion(codigo);
    }

    public List<Atraccion> buscarAtraccionPorTipo(TipoAtraccion tipo) {
        return gestor.buscarAtraccion(tipo);
    }

    public void modificarAtraccion(String codigo, String nombre, TipoAtraccion tipo, int capacidad)
            throws ElementoNoEncontradoException {
        gestor.modificarAtraccion(codigo, nombre, tipo, capacidad);
    }

    public void eliminarAtraccion(String codigo) throws ElementoNoEncontradoException {
        gestor.eliminarAtraccion(codigo);
    }

    // ---------- operaciones sobre Reservas ----------

    public void agregarReserva(String codigoAtraccion, Reserva reserva)
            throws ElementoNoEncontradoException, CapacidadExcedidaException {
        gestor.agregarReserva(codigoAtraccion, reserva);
    }

    public List<Reserva> listarReservas(String codigoAtraccion) throws ElementoNoEncontradoException {
        return gestor.listarReservas(codigoAtraccion);
    }

    public Reserva buscarReserva(String codigoAtraccion, String idReserva)
            throws ElementoNoEncontradoException {
        return gestor.buscarReserva(codigoAtraccion, idReserva);
    }

    public List<Reserva> buscarReservasPorFecha(LocalDate fecha) {
        return gestor.buscarReservasPorFecha(fecha);
    }

    public void modificarReserva(String codigoAtraccion, String idReserva, int cantidadPersonas,
                                  EstadoReserva estado) throws ElementoNoEncontradoException {
        gestor.modificarReserva(codigoAtraccion, idReserva, cantidadPersonas, estado);
    }

    public void eliminarReserva(String codigoAtraccion, String idReserva)
            throws ElementoNoEncontradoException {
        gestor.eliminarReserva(codigoAtraccion, idReserva);
    }

    // ---------- SIA-9: reporte de ocupacion ----------

    public String generarReporteOcupacion(LocalDate desde, LocalDate hasta) {
        return gestor.generarReporteOcupacion(desde, hasta);
    }

    /**
     * Devuelve, para cada atracción, el porcentaje de ocupación en el rango
     * de fechas pedido. Lo uso para dibujar el gráfico de barras (SIA-O1) y
     * para armar la planilla de cálculo (SIA-O2), así no repito el cálculo
     * en dos lados distintos.
     */
    public Map<String, Double> calcularOcupacionPorAtraccion(LocalDate desde, LocalDate hasta) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        for (Atraccion a : gestor.listarAtracciones()) {
            int personas = 0;
            for (Reserva r : a.getReservas()) {
                if (r.getEstado() == EstadoReserva.CANCELADA) continue;
                if (r.getFecha() == null) continue;
                if (!r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta)) {
                    personas = personas + r.getCantidadPersonas();
                }
            }
            double porcentaje = 0;
            if (a.getCapacidadMaxima() > 0) {
                porcentaje = (personas * 100.0) / a.getCapacidadMaxima();
            }
            resultado.put(a.getNombre(), porcentaje);
        }
        return resultado;
    }

    /**
     * SIA-O2: genera un archivo de planilla de cálculo (formato CSV, que
     * Excel/LibreOffice Calc abren directo como una tabla) con el reporte
     * de ocupación del rango de fechas pedido.
     *
     * @return la ruta del archivo generado, para poder mostrarla en un aviso.
     */
    public String exportarPlanillaOcupacion(LocalDate desde, LocalDate hasta) throws IOException {
        File carpeta = new File("reportes");
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        String rutaArchivo = "reportes/planilla_ocupacion.csv";

        BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo));
        try {
            // Uso ";" como separador porque es lo que Excel en español reconoce
            // automáticamente al abrir un CSV con doble clic.
            bw.write("Atraccion;Personas reservadas;Ocupacion (%)");
            bw.newLine();

            Map<String, Double> ocupacion = calcularOcupacionPorAtraccion(desde, hasta);
            for (Atraccion a : gestor.listarAtracciones()) {
                int personas = 0;
                for (Reserva r : a.getReservas()) {
                    if (r.getEstado() == EstadoReserva.CANCELADA) continue;
                    if (r.getFecha() == null) continue;
                    if (!r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta)) {
                        personas = personas + r.getCantidadPersonas();
                    }
                }
                double porcentaje = ocupacion.get(a.getNombre());
                bw.write(a.getNombre() + ";" + personas + ";" + String.format("%.1f", porcentaje));
                bw.newLine();
            }
        } finally {
            bw.close();
        }
        return rutaArchivo;
    }
}
