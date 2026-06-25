package com.ecomarket.dto;

import java.math.BigDecimal;

/** Linea del carrito expuesta por la API. */
public record ItemCarritoDTO(
        Long id,
        Long productoId,
        String nombreProducto,
        BigDecimal precioUnitario,
        String imagenUrl,
        Integer cantidad,
        BigDecimal subtotal
) {
}
