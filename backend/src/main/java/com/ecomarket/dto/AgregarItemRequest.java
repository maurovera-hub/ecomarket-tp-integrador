package com.ecomarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Datos para agregar un producto al carrito. */
public record AgregarItemRequest(
        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad
) {
}
