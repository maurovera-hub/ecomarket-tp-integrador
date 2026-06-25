package com.ecomarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Orden de compra confirmada, expuesta por la API. */
public record OrdenDTO(
        Long id,
        LocalDateTime fechaHora,
        String mensaje,
        BigDecimal total,
        List<ItemOrdenDTO> items
) {
}
