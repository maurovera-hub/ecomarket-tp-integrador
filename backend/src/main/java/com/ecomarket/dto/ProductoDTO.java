package com.ecomarket.dto;

import java.math.BigDecimal;

/** Representacion de un producto expuesta por la API. */
public record ProductoDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String categoria,
        Integer stock,
        String imagenUrl
) {
}
