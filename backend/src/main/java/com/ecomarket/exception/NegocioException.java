package com.ecomarket.exception;

/** Se lanza ante una regla de negocio invalida. Mapea a HTTP 400. */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
