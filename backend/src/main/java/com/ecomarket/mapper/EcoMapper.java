package com.ecomarket.mapper;

import com.ecomarket.dto.CarritoDTO;
import com.ecomarket.dto.ItemCarritoDTO;
import com.ecomarket.dto.ItemOrdenDTO;
import com.ecomarket.dto.OrdenDTO;
import com.ecomarket.dto.ProductoDTO;
import com.ecomarket.model.Carrito;
import com.ecomarket.model.ItemCarrito;
import com.ecomarket.model.ItemOrden;
import com.ecomarket.model.Orden;
import com.ecomarket.model.Producto;

import java.util.List;

/**
 * Conversores de entidades JPA a DTOs. Centralizamos el mapeo aca para no
 * exponer entidades en la API ni repetir la logica en cada service.
 */
public final class EcoMapper {

    private EcoMapper() {
    }

    public static ProductoDTO toProductoDTO(Producto p) {
        return new ProductoDTO(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getCategoria(),
                p.getStock(),
                p.getImagenUrl());
    }

    public static ItemCarritoDTO toItemCarritoDTO(ItemCarrito item) {
        Producto p = item.getProducto();
        return new ItemCarritoDTO(
                item.getId(),
                p.getId(),
                p.getNombre(),
                p.getPrecio(),
                p.getImagenUrl(),
                item.getCantidad(),
                item.getSubtotal());
    }

    public static CarritoDTO toCarritoDTO(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItems().stream()
                .map(EcoMapper::toItemCarritoDTO)
                .toList();
        return new CarritoDTO(carrito.getId(), items, carrito.getTotal());
    }

    public static ItemOrdenDTO toItemOrdenDTO(ItemOrden item) {
        return new ItemOrdenDTO(
                item.getId(),
                item.getProductoId(),
                item.getNombreProducto(),
                item.getPrecioUnitario(),
                item.getCantidad(),
                item.getSubtotal());
    }

    public static OrdenDTO toOrdenDTO(Orden orden) {
        List<ItemOrdenDTO> items = orden.getItems().stream()
                .map(EcoMapper::toItemOrdenDTO)
                .toList();
        return new OrdenDTO(
                orden.getId(),
                orden.getFechaHora(),
                orden.getMensaje(),
                orden.getTotal(),
                items);
    }
}
