package com.ecomarket.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cuerpo JSON uniforme para todos los errores de la API.
 *
 * @param timestamp momento del error
 * @param status    codigo HTTP
 * @param error     descripcion corta del codigo HTTP
 * @param message   mensaje legible para el cliente
 * @param errores   detalle por campo en errores de validacion (opcional)
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> errores
) {
    public ErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> errores) {
        this(LocalDateTime.now(), status, error, message, errores);
    }
}
