package com.ecomarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Datos para modificar la cantidad de una linea del carrito. */
public record ActualizarCantidadRequest(
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
) {
}
