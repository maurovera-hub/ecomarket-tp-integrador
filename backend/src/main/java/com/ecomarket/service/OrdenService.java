package com.ecomarket.service;

import com.ecomarket.dto.ConfirmarOrdenRequest;
import com.ecomarket.dto.OrdenDTO;
import com.ecomarket.exception.NegocioException;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.mapper.EcoMapper;
import com.ecomarket.model.Carrito;
import com.ecomarket.model.ItemCarrito;
import com.ecomarket.model.ItemOrden;
import com.ecomarket.model.Orden;
import com.ecomarket.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Logica de confirmacion y consulta de ordenes de compra.
 */
@Service
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final CarritoService carritoService;

    public OrdenService(OrdenRepository ordenRepository, CarritoService carritoService) {
        this.ordenRepository = ordenRepository;
        this.carritoService = carritoService;
    }

    /**
     * Confirma el carrito activo como una orden de compra: congela los precios,
     * registra fecha y hora, guarda el mensaje del cliente y vacia el carrito.
     */
    public OrdenDTO confirmar(ConfirmarOrdenRequest request) {
        Carrito carrito = carritoService.obtenerOCrearEntidad();
        if (carrito.getItems().isEmpty()) {
            throw new NegocioException("No se puede confirmar una orden con el carrito vacio");
        }

        Orden orden = new Orden();
        orden.setFechaHora(LocalDateTime.now());
        orden.setMensaje(request != null ? request.mensaje() : null);

        for (ItemCarrito item : carrito.getItems()) {
            ItemOrden itemOrden = new ItemOrden(
                    item.getProducto().getId(),
                    item.getProducto().getNombre(),
                    item.getProducto().getPrecio(),
                    item.getCantidad());
            orden.agregarItem(itemOrden);
        }
        orden.setTotal(carrito.getTotal());

        Orden guardada = ordenRepository.save(orden);
        carritoService.vaciar();

        return EcoMapper.toOrdenDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<OrdenDTO> listar() {
        return ordenRepository.findAllByOrderByFechaHoraDesc().stream()
                .map(EcoMapper::toOrdenDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenDTO obtener(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la orden con id " + id));
        return EcoMapper.toOrdenDTO(orden);
    }
}
