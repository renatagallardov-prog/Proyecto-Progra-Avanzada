package com.parquetematico.gestion;

import com.parquetematico.excepciones.CapacidadExcedidaException;
import com.parquetematico.excepciones.ElementoNoEncontradoException;
import com.parquetematico.modelo.Atraccion;
import com.parquetematico.modelo.EstadoReserva;
import com.parquetematico.modelo.Reserva;
import com.parquetematico.modelo.TipoAtraccion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de negocio principal del sistema.
 *
 * SIA-4: contiene la 1ª colección del sistema: un Map (JCF) que asocia el
 * código de cada atracción con su objeto Atraccion. La 2ª colección
 * (lista de reservas) vive anidada dentro de cada Atraccion.
 *
 * SIA-5: sobrecarga de métodos -> buscarAtraccion(String) / buscarAtraccion(TipoAtraccion)
 * y buscarReservasPorFecha(LocalDate) / buscarReservasPorFecha(String, LocalDate).
 */
public class GestorParque {

    // 1ª colección del sistema (Map, JCF).
    private final Map<String, Atraccion> atracciones;

    public GestorParque() {
        this.atracciones = new LinkedHashMap<>();
    }

    public Map<String, Atraccion> getAtracciones() {
        return atracciones;
    }

    // ---------------------- CRUD ATRACCIONES ----------------------

    public void agregarAtraccion(Atraccion a) {
        atracciones.put(a.getCodigo(), a);
    }

    public List<Atraccion> listarAtracciones() {
        return new ArrayList<>(atracciones.values());
    }

    /**
     * SIA-5 (sobrecarga 1/2): busca una atracción por su código exacto.
     *
     * @param codigo codigo unico de la atraccion (ej: "A01")
     * @return la atraccion encontrada
     * @throws ElementoNoEncontradoException si no existe una atraccion con ese codigo
     */
    public Atraccion buscarAtraccion(String codigo) throws ElementoNoEncontradoException {
        Atraccion a = atracciones.get(codigo);
        if (a == null) {
            throw new ElementoNoEncontradoException("No existe una atracción con código: " + codigo);
        }
        return a;
    }

    /**
     * SIA-5 (sobrecarga 2/2): busca todas las atracciones de un tipo determinado.
     *
     * @param tipo el tipo de atraccion a buscar (ej: TipoAtraccion.ACUATICA)
     * @return lista con las atracciones de ese tipo (puede quedar vacia)
     */
    public List<Atraccion> buscarAtraccion(TipoAtraccion tipo) {
        List<Atraccion> resultado = new ArrayList<>();
        for (Atraccion a : atracciones.values()) {
            if (a.getTipo() == tipo) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    public void modificarAtraccion(String codigo, String nombre, TipoAtraccion tipo,
                                    int capacidadMaxima) throws ElementoNoEncontradoException {
        Atraccion a = buscarAtraccion(codigo);
        a.setNombre(nombre);
        a.setTipo(tipo);
        a.setCapacidadMaxima(capacidadMaxima);
    }

    public void eliminarAtraccion(String codigo) throws ElementoNoEncontradoException {
        if (!atracciones.containsKey(codigo)) {
            throw new ElementoNoEncontradoException("No existe una atracción con código: " + codigo);
        }
        atracciones.remove(codigo);
    }

    // ---------------------- CRUD RESERVAS (2ª colección, anidada) ----------------------

    /**
     * Agrega una reserva a la atraccion indicada, revisando antes que haya
     * cupo disponible para esa fecha.
     *
     * @param codigoAtraccion codigo de la atraccion donde se quiere reservar
     * @param reserva la reserva a agregar
     * @throws ElementoNoEncontradoException si el codigo de atraccion no existe
     * @throws CapacidadExcedidaException si no hay cupo para esa cantidad de personas
     */
    public void agregarReserva(String codigoAtraccion, Reserva reserva)
            throws ElementoNoEncontradoException, CapacidadExcedidaException {
        Atraccion a = buscarAtraccion(codigoAtraccion);
        if (!a.tieneCapacidadDisponible(reserva.getFecha(), reserva.getCantidadPersonas())) {
            throw new CapacidadExcedidaException("La atracción '" + a.getNombre()
                    + "' no tiene capacidad disponible para " + reserva.getCantidadPersonas()
                    + " personas el día " + reserva.getFecha());
        }
        a.getReservas().add(reserva);
    }

    public List<Reserva> listarReservas(String codigoAtraccion) throws ElementoNoEncontradoException {
        return buscarAtraccion(codigoAtraccion).getReservas();
    }

    public Reserva buscarReserva(String codigoAtraccion, String idReserva)
            throws ElementoNoEncontradoException {
        Atraccion a = buscarAtraccion(codigoAtraccion);
        for (Reserva r : a.getReservas()) {
            if (r.getIdReserva().equals(idReserva)) {
                return r;
            }
        }
        throw new ElementoNoEncontradoException("No existe una reserva con id: " + idReserva);
    }

    /**
     * Búsqueda (sin sobrecarga aquí, ya usada en Atraccion/GestorParque) de todas
     * las reservas de una fecha específica, a través de todas las atracciones.
     */
    public List<Reserva> buscarReservasPorFecha(LocalDate fecha) {
        List<Reserva> resultado = new ArrayList<>();
        for (Atraccion a : atracciones.values()) {
            for (Reserva r : a.getReservas()) {
                if (r.getFecha() != null && r.getFecha().equals(fecha)) {
                    resultado.add(r);
                }
            }
        }
        return resultado;
    }

    public void modificarReserva(String codigoAtraccion, String idReserva, int nuevaCantidadPersonas,
                                  EstadoReserva nuevoEstado) throws ElementoNoEncontradoException {
        Reserva r = buscarReserva(codigoAtraccion, idReserva);
        r.setCantidadPersonas(nuevaCantidadPersonas);
        r.setEstado(nuevoEstado);
    }

    public void eliminarReserva(String codigoAtraccion, String idReserva)
            throws ElementoNoEncontradoException {
        Atraccion a = buscarAtraccion(codigoAtraccion);
        Reserva r = buscarReserva(codigoAtraccion, idReserva);
        a.getReservas().remove(r);
    }

    // ---------------------- SIA-9: FUNCIONALIDAD PROPIA ----------------------

    /**
     * Genera un reporte de ocupación/estadísticas del parque, filtrado por un
     * rango de fechas. Para cada atracción calcula el total de personas
     * reservadas (sin contar canceladas) y el % de ocupación respecto a su
     * capacidad máxima diaria.
     */
    /**
     * @param desde fecha inicial del rango (inclusiva)
     * @param hasta fecha final del rango (inclusiva)
     * @return texto con el reporte de ocupacion de todas las atracciones en ese rango
     */
    public String generarReporteOcupacion(LocalDate desde, LocalDate hasta) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE OCUPACIÓN (").append(desde).append(" a ").append(hasta).append(") ===\n");
        for (Atraccion a : atracciones.values()) {
            int totalPersonas = 0;
            int totalReservasValidas = 0;
            for (Reserva r : a.getReservas()) {
                if (r.getEstado() == EstadoReserva.CANCELADA) continue;
                if (r.getFecha() == null) continue;
                if (!r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta)) {
                    totalPersonas += r.getCantidadPersonas();
                    totalReservasValidas++;
                }
            }
            double porcentaje = a.getCapacidadMaxima() == 0 ? 0
                    : (totalPersonas * 100.0) / a.getCapacidadMaxima();
            sb.append(String.format("- %-20s | reservas: %3d | personas: %4d | ocupación prom.: %.1f%%\n",
                    a.getNombre(), totalReservasValidas, totalPersonas, porcentaje));
        }
        return sb.toString();
    }
}
