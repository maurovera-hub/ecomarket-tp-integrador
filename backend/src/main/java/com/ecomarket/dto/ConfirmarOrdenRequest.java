package com.ecomarket.dto;

import jakarta.validation.constraints.Size;

/** Datos para confirmar el carrito como una orden. El mensaje es opcional. */
public record ConfirmarOrdenRequest(
        @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
        String mensaje
) {
}
