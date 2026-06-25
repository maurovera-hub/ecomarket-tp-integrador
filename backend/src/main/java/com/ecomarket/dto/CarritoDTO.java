package com.ecomarket.dto;

import java.math.BigDecimal;
import java.util.List;

/** Carrito completo con sus lineas y el total calculado. */
public record CarritoDTO(
        Long id,
        List<ItemCarritoDTO> items,
        BigDecimal total
) {
}
