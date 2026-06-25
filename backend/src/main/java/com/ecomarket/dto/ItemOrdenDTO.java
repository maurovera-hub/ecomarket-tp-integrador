package com.ecomarket.dto;

import java.math.BigDecimal;

/** Linea de una orden expuesta por la API. */
public record ItemOrdenDTO(
        Long id,
        Long productoId,
        String nombreProducto,
        BigDecimal precioUnitario,
        Integer cantidad,
        BigDecimal subtotal
) {
}
