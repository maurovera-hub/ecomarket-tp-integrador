package com.ecomarket.exception;

/** Se lanza cuando no existe el recurso solicitado. Mapea a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
