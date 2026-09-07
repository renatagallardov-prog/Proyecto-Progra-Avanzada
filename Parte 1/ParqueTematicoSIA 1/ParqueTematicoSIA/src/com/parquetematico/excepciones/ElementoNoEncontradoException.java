package com.parquetematico.excepciones;

/**
 * Se lanza cuando no se encuentra una atracción o una reserva
 * a partir del criterio de búsqueda entregado.
 */
public class ElementoNoEncontradoException extends Exception {

    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
