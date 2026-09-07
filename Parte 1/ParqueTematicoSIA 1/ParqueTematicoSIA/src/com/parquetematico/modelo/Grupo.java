package com.parquetematico.modelo;

import java.util.Objects;

/**
 * Representa un grupo de visitantes que realiza una reserva.
 * Todos los atributos son privados con sus respectivos getters y setters
 * (buenas prácticas - SIA-3).
 */
public class Grupo {

    private String rutResponsable;
    private String nombreResponsable;
    private String contacto;
    private int cantidadPersonas;

    public Grupo(String rutResponsable, String nombreResponsable, String contacto, int cantidadPersonas) {
        this.rutResponsable = rutResponsable;
        this.nombreResponsable = nombreResponsable;
        this.contacto = contacto;
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getRutResponsable() {
        return rutResponsable;
    }

    public void setRutResponsable(String rutResponsable) {
        this.rutResponsable = rutResponsable;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    @Override
    public String toString() {
        return "Grupo{rut=" + rutResponsable + ", responsable=" + nombreResponsable
                + ", contacto=" + contacto + ", personas=" + cantidadPersonas + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grupo)) return false;
        Grupo grupo = (Grupo) o;
        return Objects.equals(rutResponsable, grupo.rutResponsable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rutResponsable);
    }
}
