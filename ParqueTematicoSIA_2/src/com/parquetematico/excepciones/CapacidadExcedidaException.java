package com.parquetematico.excepciones;

/**
 * Se lanza cuando una reserva excede la capacidad disponible de una atracción
 * en la fecha solicitada.
 */
public class CapacidadExcedidaException extends Exception {

    public CapacidadExcedidaException(String mensaje) {
        super(mensaje);
    }
}
