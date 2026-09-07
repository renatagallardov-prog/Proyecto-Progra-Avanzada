package com.parquetematico.persistencia;

import com.parquetematico.gestion.GestorParque;
import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Grupo;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * SIA-11: Persistencia de datos mediante archivos de texto (CSV), usando un
 * esquema batch: los datos se cargan completos al iniciar la aplicación
 * (cargarDatos) y se graban completos al salir (guardarDatos).
 */
public class PersistenciaCSV {

    private static final String ARCHIVO_ATRACCIONES = "data/atracciones.csv";
    private static final String ARCHIVO_RESERVAS = "data/reservas.csv";
    private static final String SEP = ";";

    /**
     * Carga todo lo que haya guardado en los archivos CSV (atracciones y
     * reservas) dentro del GestorParque recibido. Si los archivos no
     * existen, no hace nada (eso pasa la primera vez que se usa el sistema).
     *
     * @param gestor el gestor donde se van a dejar los datos cargados
     */
    public void cargarDatos(GestorParque gestor) {
        cargarAtracciones(gestor);
        cargarReservas(gestor);
    }

    /**
     * Escribe todo lo que hay en el GestorParque a los archivos CSV,
     * sobrescribiendo lo que hubiera antes (por eso es "batch": se guarda
     * todo junto, no de a poquito).
     *
     * @param gestor el gestor con los datos actuales del sistema
     */
    public void guardarDatos(GestorParque gestor) {
        guardarAtracciones(gestor);
        guardarReservas(gestor);
    }

    private void cargarAtracciones(GestorParque gestor) {
        File archivo = new File(ARCHIVO_ATRACCIONES);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEP);
                Atraccion a = new Atraccion(
                        campos[0],
                        campos[1],
                        TipoAtraccion.valueOf(campos[2]),
                        Integer.parseInt(campos[3]),
                        LocalTime.parse(campos[4]),
                        LocalTime.parse(campos[5]),
                        Boolean.parseBoolean(campos[6])
                );
                gestor.agregarAtraccion(a);
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar las atracciones: " + e.getMessage());
        }
    }

    private void cargarReservas(GestorParque gestor) {
        File archivo = new File(ARCHIVO_RESERVAS);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEP);
                String idReserva = campos[0];
                String codigoAtraccion = campos[1];
                Grupo grupo = new Grupo(campos[2], campos[3], campos[4], Integer.parseInt(campos[5]));
                LocalDate fecha = LocalDate.parse(campos[6]);
                LocalTime hora = LocalTime.parse(campos[7]);
                int cantidadPersonas = Integer.parseInt(campos[8]);
                EstadoReserva estado = EstadoReserva.valueOf(campos[9]);

                Reserva r = new Reserva(idReserva, codigoAtraccion, grupo, fecha, hora,
                        cantidadPersonas, estado);

                Atraccion a = gestor.getAtracciones().get(codigoAtraccion);
                if (a != null) {
                    a.getReservas().add(r);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar las reservas: " + e.getMessage());
        }
    }

    private void guardarAtracciones(GestorParque gestor) {
        File carpeta = new File("data");
        if (!carpeta.exists()) carpeta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ATRACCIONES))) {
            for (Atraccion a : gestor.listarAtracciones()) {
                bw.write(String.join(SEP,
                        a.getCodigo(),
                        a.getNombre(),
                        a.getTipo().name(),
                        String.valueOf(a.getCapacidadMaxima()),
                        a.getHoraApertura().toString(),
                        a.getHoraCierre().toString(),
                        String.valueOf(a.isActiva())
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudieron guardar las atracciones: " + e.getMessage());
        }
    }

    private void guardarReservas(GestorParque gestor) {
        File carpeta = new File("data");
        if (!carpeta.exists()) carpeta.mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_RESERVAS))) {
            for (Atraccion a : gestor.listarAtracciones()) {
                for (Reserva r : a.getReservas()) {
                    Grupo g = r.getGrupo();
                    bw.write(String.join(SEP,
                            r.getIdReserva(),
                            r.getCodigoAtraccion(),
                            g.getRutResponsable(),
                            g.getNombreResponsable(),
                            g.getContacto(),
                            String.valueOf(g.getCantidadPersonas()),
                            r.getFecha().toString(),
                            r.getHora().toString(),
                            String.valueOf(r.getCantidadPersonas()),
                            r.getEstado().name()
                    ));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron guardar las reservas: " + e.getMessage());
        }
    }
}
