package com.parquetematico.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa una reserva de un grupo para una atracción en una fecha/hora dada.
 * SIA-6: sobreescribe toString(), equals() y hashCode() de Object.
 */
public class Reserva {

    private String idReserva;
    private String codigoAtraccion;
    private Grupo grupo;
    private LocalDate fecha;
    private LocalTime hora;
    private int cantidadPersonas;
    private EstadoReserva estado;

    public Reserva(String idReserva, String codigoAtraccion, Grupo grupo, LocalDate fecha,
                    LocalTime hora, int cantidadPersonas, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.codigoAtraccion = codigoAtraccion;
        this.grupo = grupo;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidadPersonas = cantidadPersonas;
        this.estado = estado;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodigoAtraccion() {
        return codigoAtraccion;
    }

    public void setCodigoAtraccion(String codigoAtraccion) {
        this.codigoAtraccion = codigoAtraccion;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva{id=" + idReserva + ", atraccion=" + codigoAtraccion + ", grupo="
                + (grupo != null ? grupo.getNombreResponsable() : "N/A") + ", fecha=" + fecha
                + ", hora=" + hora + ", personas=" + cantidadPersonas + ", estado=" + estado + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva)) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(idReserva, reserva.idReserva);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva);
    }
}
