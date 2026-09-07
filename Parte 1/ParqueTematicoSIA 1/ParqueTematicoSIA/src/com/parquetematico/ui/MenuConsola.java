package com.parquetematico.ui;

import com.parquetematico.controlador.ParqueController;
import com.parquetematico.excepciones.CapacidadExcedidaException;
import com.parquetematico.excepciones.ElementoNoEncontradoException;
import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Grupo;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de consola del sistema (SIA-7, SIA-8, SIA-10).
 *
 * SIA-O4 (MVC): esta clase es la Vista. No conoce a GestorParque ni a
 * PersistenciaCSV, todas las operaciones se piden a través del
 * ParqueController.
 */
public class MenuConsola {

    private final ParqueController controlador;
    private final Scanner sc;

    public MenuConsola(ParqueController controlador, Scanner sc) {
        this.controlador = controlador;
        this.sc = sc;
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n===== SISTEMA PARQUE TEMATICO =====");
            System.out.println("--- Atracciones ---");
            System.out.println(" 1) Agregar atraccion");
            System.out.println(" 2) Listar atracciones");
            System.out.println(" 3) Modificar atraccion");
            System.out.println(" 4) Eliminar atraccion");
            System.out.println(" 5) Buscar atraccion");
            System.out.println("--- Reservas ---");
            System.out.println(" 6) Agregar reserva");
            System.out.println(" 7) Listar reservas de una atraccion");
            System.out.println(" 8) Modificar reserva");
            System.out.println(" 9) Eliminar reserva");
            System.out.println("10) Buscar reserva");
            System.out.println("--- Extra ---");
            System.out.println("11) Reporte de ocupacion (SIA-9)");
            System.out.println("12) Exportar planilla de ocupacion a Excel/CSV (SIA-O2)");
            System.out.println(" 0) Salir");
            System.out.print("Opcion: ");
            opcion = leerEntero();

            try {
                switch (opcion) {
                    case 1:
                        agregarAtraccion();
                        break;
                    case 2:
                        listarAtracciones();
                        break;
                    case 3:
                        modificarAtraccion();
                        break;
                    case 4:
                        eliminarAtraccion();
                        break;
                    case 5:
                        buscarAtraccion();
                        break;
                    case 6:
                        agregarReserva();
                        break;
                    case 7:
                        listarReservas();
                        break;
                    case 8:
                        modificarReserva();
                        break;
                    case 9:
                        eliminarReserva();
                        break;
                    case 10:
                        buscarReserva();
                        break;
                    case 11:
                        reporteOcupacion();
                        break;
                    case 12:
                        exportarPlanilla();
                        break;
                    case 0:
                        System.out.println("Guardando datos y saliendo...");
                        break;
                    default:
                        System.out.println("Opcion invalida.");
                }
            } catch (ElementoNoEncontradoException e) {
                // SIA-12: manejo con try-catch de las excepciones propias
                System.out.println("No se encontro lo que buscabas: " + e.getMessage());
            } catch (CapacidadExcedidaException e) {
                System.out.println("No se pudo hacer la reserva: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Algo salio mal: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    // ---------------------- ATRACCIONES ----------------------

    private void agregarAtraccion() {
        System.out.print("Codigo: ");
        String codigo = sc.nextLine();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Tipo (MECANICA, ACUATICA, INFANTIL, EXTREMA, ESPECTACULO): ");
        TipoAtraccion tipo = TipoAtraccion.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Capacidad maxima: ");
        int capacidad = leerEntero();
        System.out.print("Hora apertura (HH:mm): ");
        LocalTime apertura = LocalTime.parse(sc.nextLine());
        System.out.print("Hora cierre (HH:mm): ");
        LocalTime cierre = LocalTime.parse(sc.nextLine());

        Atraccion a = new Atraccion(codigo, nombre, tipo, capacidad, apertura, cierre, true);
        controlador.agregarAtraccion(a);
        System.out.println("Atraccion agregada correctamente.");
    }

    private void listarAtracciones() {
        List<Atraccion> lista = controlador.listarAtracciones();
        if (lista.isEmpty()) {
            System.out.println("No hay atracciones registradas.");
            return;
        }
        for (Atraccion a : lista) {
            System.out.println(a);
        }
    }

    private void modificarAtraccion() throws ElementoNoEncontradoException {
        System.out.print("Codigo de la atraccion a modificar: ");
        String codigo = sc.nextLine();
        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Nuevo tipo: ");
        TipoAtraccion tipo = TipoAtraccion.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Nueva capacidad maxima: ");
        int capacidad = leerEntero();
        controlador.modificarAtraccion(codigo, nombre, tipo, capacidad);
        System.out.println("Atraccion modificada correctamente.");
    }

    private void eliminarAtraccion() throws ElementoNoEncontradoException {
        System.out.print("Codigo de la atraccion a eliminar: ");
        String codigo = sc.nextLine();
        controlador.eliminarAtraccion(codigo);
        System.out.println("Atraccion eliminada correctamente.");
    }

    private void buscarAtraccion() throws ElementoNoEncontradoException {
        System.out.print("Buscar por (1) codigo o (2) tipo: ");
        int modo = leerEntero();
        if (modo == 1) {
            System.out.print("Codigo: ");
            String codigo = sc.nextLine();
            System.out.println(controlador.buscarAtraccion(codigo));
        } else {
            System.out.print("Tipo: ");
            TipoAtraccion tipo = TipoAtraccion.valueOf(sc.nextLine().trim().toUpperCase());
            List<Atraccion> encontradas = controlador.buscarAtraccionPorTipo(tipo);
            for (Atraccion a : encontradas) {
                System.out.println(a);
            }
        }
    }

    // ---------------------- RESERVAS ----------------------

    private void agregarReserva() throws ElementoNoEncontradoException, CapacidadExcedidaException {
        System.out.print("Codigo de atraccion: ");
        String codigoAtraccion = sc.nextLine();
        System.out.print("Id reserva: ");
        String idReserva = sc.nextLine();
        System.out.print("RUT responsable del grupo: ");
        String rut = sc.nextLine();
        System.out.print("Nombre responsable: ");
        String nombreResp = sc.nextLine();
        System.out.print("Contacto: ");
        String contacto = sc.nextLine();
        System.out.print("Cantidad de personas: ");
        int personas = leerEntero();
        System.out.print("Fecha (yyyy-MM-dd): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine());
        System.out.print("Hora (HH:mm): ");
        LocalTime hora = LocalTime.parse(sc.nextLine());

        Grupo grupo = new Grupo(rut, nombreResp, contacto, personas);
        Reserva reserva = new Reserva(idReserva, codigoAtraccion, grupo, fecha, hora, personas,
                EstadoReserva.PENDIENTE);

        controlador.agregarReserva(codigoAtraccion, reserva);
        System.out.println("Reserva agregada correctamente.");
    }

    private void listarReservas() throws ElementoNoEncontradoException {
        System.out.print("Codigo de atraccion: ");
        String codigo = sc.nextLine();
        List<Reserva> reservas = controlador.listarReservas(codigo);
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas para esta atraccion.");
        } else {
            for (Reserva r : reservas) {
                System.out.println(r);
            }
        }
    }

    private void modificarReserva() throws ElementoNoEncontradoException {
        System.out.print("Codigo de atraccion: ");
        String codigoAtraccion = sc.nextLine();
        System.out.print("Id de la reserva: ");
        String idReserva = sc.nextLine();
        System.out.print("Nueva cantidad de personas: ");
        int personas = leerEntero();
        System.out.print("Nuevo estado (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA): ");
        EstadoReserva estado = EstadoReserva.valueOf(sc.nextLine().trim().toUpperCase());
        controlador.modificarReserva(codigoAtraccion, idReserva, personas, estado);
        System.out.println("Reserva modificada correctamente.");
    }

    private void eliminarReserva() throws ElementoNoEncontradoException {
        System.out.print("Codigo de atraccion: ");
        String codigoAtraccion = sc.nextLine();
        System.out.print("Id de la reserva: ");
        String idReserva = sc.nextLine();
        controlador.eliminarReserva(codigoAtraccion, idReserva);
        System.out.println("Reserva eliminada correctamente.");
    }

    private void buscarReserva() throws ElementoNoEncontradoException {
        System.out.print("Buscar por (1) codigo de atraccion + id, o (2) fecha: ");
        int modo = leerEntero();
        if (modo == 1) {
            System.out.print("Codigo de atraccion: ");
            String codigoAtraccion = sc.nextLine();
            System.out.print("Id reserva: ");
            String idReserva = sc.nextLine();
            System.out.println(controlador.buscarReserva(codigoAtraccion, idReserva));
        } else {
            System.out.print("Fecha (yyyy-MM-dd): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine());
            List<Reserva> resultado = controlador.buscarReservasPorFecha(fecha);
            if (resultado.isEmpty()) {
                System.out.println("No hay reservas para esa fecha.");
            } else {
                for (Reserva r : resultado) {
                    System.out.println(r);
                }
            }
        }
    }

    // ---------------------- SIA-9 y SIA-O2 ----------------------

    private void reporteOcupacion() {
        System.out.print("Fecha desde (yyyy-MM-dd): ");
        LocalDate desde = LocalDate.parse(sc.nextLine());
        System.out.print("Fecha hasta (yyyy-MM-dd): ");
        LocalDate hasta = LocalDate.parse(sc.nextLine());
        System.out.println(controlador.generarReporteOcupacion(desde, hasta));
    }

    private void exportarPlanilla() {
        System.out.print("Fecha desde (yyyy-MM-dd): ");
        LocalDate desde = LocalDate.parse(sc.nextLine());
        System.out.print("Fecha hasta (yyyy-MM-dd): ");
        LocalDate hasta = LocalDate.parse(sc.nextLine());
        try {
            String ruta = controlador.exportarPlanillaOcupacion(desde, hasta);
            System.out.println("Listo! Se genero la planilla en: " + ruta);
            System.out.println("Se puede abrir directo con Excel o LibreOffice Calc.");
        } catch (Exception e) {
            System.out.println("No se pudo generar la planilla: " + e.getMessage());
        }
    }

    // ---------------------- UTIL ----------------------

    private int leerEntero() {
        while (true) {
            try {
                String linea = sc.nextLine();
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido: ");
            }
        }
    }
}
