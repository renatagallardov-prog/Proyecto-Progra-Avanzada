package com.parquetematico.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una atracción del parque temático.
 *
 * SIA-4: Cada Atraccion contiene su propia colección de Reservas (ArrayList),
 * la cual constituye la 2ª colección del sistema, ANIDADA dentro de la 1ª
 * colección (el Map<String, Atraccion> que vive en GestorParque).
 *
 * SIA-5: Sobrecarga de métodos -> tieneCapacidadDisponible(int) y
 * tieneCapacidadDisponible(LocalDate, int).
 *
 * SIA-6: Sobreescritura de toString(), equals() y hashCode() de Object.
 */
public class Atraccion {

    private String codigo;
    private String nombre;
    private TipoAtraccion tipo;
    private int capacidadMaxima;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean activa;

    // 2ª colección del sistema (anidada dentro de cada Atraccion). JCF: ArrayList.
    private List<Reserva> reservas;

    public Atraccion(String codigo, String nombre, TipoAtraccion tipo, int capacidadMaxima,
                      LocalTime horaApertura, LocalTime horaCierre, boolean activa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadMaxima = capacidadMaxima;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.activa = activa;
        this.reservas = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoAtraccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtraccion tipo) {
        this.tipo = tipo;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    /**
     * SIA-5 (sobrecarga 1/2): verifica si hay cupo para una cantidad de personas,
     * considerando únicamente la capacidad máxima de la atracción.
     *
     * @param personasSolicitadas cuantas personas se quieren agregar
     * @return true si la cantidad no supera la capacidad maxima
     */
    public boolean tieneCapacidadDisponible(int personasSolicitadas) {
        return personasSolicitadas > 0 && personasSolicitadas <= this.capacidadMaxima;
    }

    /**
     * SIA-5 (sobrecarga 2/2): verifica si hay cupo para una cantidad de personas
     * en una fecha específica, sumando las reservas ya existentes ese día.
     *
     * @param fecha la fecha en la que se quiere reservar
     * @param personasSolicitadas cuantas personas se quieren agregar ese dia
     * @return true si sumando las reservas de ese dia no se pasa de la capacidad maxima
     */
    public boolean tieneCapacidadDisponible(LocalDate fecha, int personasSolicitadas) {
        int ocupadas = 0;
        for (Reserva r : reservas) {
            if (r.getFecha() != null && r.getFecha().equals(fecha)
                    && r.getEstado() != EstadoReserva.CANCELADA) {
                ocupadas += r.getCantidadPersonas();
            }
        }
        return (ocupadas + personasSolicitadas) <= this.capacidadMaxima;
    }

    @Override
    public String toString() {
        return "Atraccion{codigo=" + codigo + ", nombre=" + nombre + ", tipo=" + tipo
                + ", capacidadMaxima=" + capacidadMaxima + ", horario=" + horaApertura + "-"
                + horaCierre + ", activa=" + activa + ", reservas=" + reservas.size() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Atraccion)) return false;
        Atraccion atraccion = (Atraccion) o;
        return Objects.equals(codigo, atraccion.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
