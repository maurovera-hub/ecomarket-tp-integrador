package com.ecomarket.controller;

import com.ecomarket.dto.ActualizarCantidadRequest;
import com.ecomarket.dto.AgregarItemRequest;
import com.ecomarket.dto.CarritoDTO;
import com.ecomarket.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST del carrito de compras.
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public CarritoDTO obtener() {
        return carritoService.obtenerCarrito();
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTO> agregarItem(@Valid @RequestBody AgregarItemRequest request) {
        CarritoDTO carrito = carritoService.agregarItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrito);
    }

    @PutMapping("/items/{itemId}")
    public CarritoDTO actualizarCantidad(@PathVariable Long itemId,
                                         @Valid @RequestBody ActualizarCantidadRequest request) {
        return carritoService.actualizarCantidad(itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CarritoDTO eliminarItem(@PathVariable Long itemId) {
        return carritoService.eliminarItem(itemId);
    }

    @DeleteMapping
    public CarritoDTO vaciar() {
        return carritoService.vaciar();
    }
}
