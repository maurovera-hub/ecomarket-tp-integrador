package com.ecomarket.service;

import com.ecomarket.dto.ActualizarCantidadRequest;
import com.ecomarket.dto.AgregarItemRequest;
import com.ecomarket.dto.CarritoDTO;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.mapper.EcoMapper;
import com.ecomarket.model.Carrito;
import com.ecomarket.model.ItemCarrito;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.CarritoRepository;
import com.ecomarket.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica del carrito de compras. La aplicacion mantiene un unico carrito
 * activo que se crea on-demand la primera vez que se lo consulta o se le
 * agrega un producto.
 */
@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarrito() {
        Carrito carrito = carritoRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::crearCarritoVacio);
        return EcoMapper.toCarritoDTO(carrito);
    }

    public CarritoDTO agregarItem(AgregarItemRequest request) {
        Carrito carrito = obtenerOCrearEntidad();
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el producto con id " + request.productoId()));

        ItemCarrito existente = buscarItemPorProducto(carrito, producto.getId());
        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + request.cantidad());
        } else {
            carrito.agregarItem(new ItemCarrito(producto, request.cantidad()));
        }

        Carrito guardado = carritoRepository.save(carrito);
        return EcoMapper.toCarritoDTO(guardado);
    }

    public CarritoDTO actualizarCantidad(Long itemId, ActualizarCantidadRequest request) {
        Carrito carrito = obtenerOCrearEntidad();
        ItemCarrito item = buscarItemPorId(carrito, itemId);
        item.setCantidad(request.cantidad());
        Carrito guardado = carritoRepository.save(carrito);
        return EcoMapper.toCarritoDTO(guardado);
    }

    public CarritoDTO eliminarItem(Long itemId) {
        Carrito carrito = obtenerOCrearEntidad();
        ItemCarrito item = buscarItemPorId(carrito, itemId);
        carrito.eliminarItem(item);
        Carrito guardado = carritoRepository.save(carrito);
        return EcoMapper.toCarritoDTO(guardado);
    }

    public CarritoDTO vaciar() {
        Carrito carrito = obtenerOCrearEntidad();
        carrito.vaciar();
        Carrito guardado = carritoRepository.save(carrito);
        return EcoMapper.toCarritoDTO(guardado);
    }

    /** Devuelve la entidad carrito activa (para uso interno, ej. confirmar orden). */
    public Carrito obtenerOCrearEntidad() {
        return carritoRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::crearCarritoVacio);
    }

    private Carrito crearCarritoVacio() {
        return carritoRepository.save(new Carrito());
    }

    private ItemCarrito buscarItemPorProducto(Carrito carrito, Long productoId) {
        return carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    private ItemCarrito buscarItemPorId(Carrito carrito, Long itemId) {
        return carrito.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el item con id " + itemId + " en el carrito"));
    }
}
