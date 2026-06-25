package com.ecomarket.controller;

import com.ecomarket.dto.ConfirmarOrdenRequest;
import com.ecomarket.dto.OrdenDTO;
import com.ecomarket.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de ordenes de compra.
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public List<OrdenDTO> listar() {
        return ordenService.listar();
    }

    @GetMapping("/{id}")
    public OrdenDTO obtener(@PathVariable Long id) {
        return ordenService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<OrdenDTO> confirmar(@Valid @RequestBody(required = false) ConfirmarOrdenRequest request) {
        OrdenDTO orden = ordenService.confirmar(request);
        return ResponseEntity
                .created(URI.create("/api/ordenes/" + orden.id()))
                .body(orden);
    }
}
