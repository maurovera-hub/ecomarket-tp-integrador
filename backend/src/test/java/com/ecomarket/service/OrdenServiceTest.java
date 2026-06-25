package com.ecomarket.service;

import com.ecomarket.dto.ConfirmarOrdenRequest;
import com.ecomarket.dto.OrdenDTO;
import com.ecomarket.exception.NegocioException;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.model.Carrito;
import com.ecomarket.model.ItemCarrito;
import com.ecomarket.model.Orden;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.OrdenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private CarritoService carritoService;

    @InjectMocks
    private OrdenService ordenService;

    private Producto producto(Long id, String precio) {
        Producto p = new Producto("Prod" + id, "d", new BigDecimal(precio), "Cocina", 50, "img");
        p.setId(id);
        return p;
    }

    private Carrito carritoConItems() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        ItemCarrito i1 = new ItemCarrito(producto(1L, "100.00"), 2);
        i1.setId(10L);
        ItemCarrito i2 = new ItemCarrito(producto(2L, "50.00"), 1);
        i2.setId(11L);
        carrito.agregarItem(i1);
        carrito.agregarItem(i2);
        return carrito;
    }

    @Test
    void confirmar_conItems_creaOrdenYVaciaCarrito() {
        when(carritoService.obtenerOCrearEntidad()).thenReturn(carritoConItems());
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> {
            Orden o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrdenDTO dto = ordenService.confirmar(new ConfirmarOrdenRequest("Dejar en porteria"));

        assertThat(dto.id()).isEqualTo(100L);
        assertThat(dto.mensaje()).isEqualTo("Dejar en porteria");
        assertThat(dto.items()).hasSize(2);
        assertThat(dto.total()).isEqualByComparingTo("250.00");
        assertThat(dto.fechaHora()).isNotNull();
        assertThat(dto.fechaHora()).isBeforeOrEqualTo(LocalDateTime.now());
        // El historial congela nombre y precio del producto
        assertThat(dto.items().get(0).nombreProducto()).isEqualTo("Prod1");
        assertThat(dto.items().get(0).precioUnitario()).isEqualByComparingTo("100.00");
        verify(carritoService).vaciar();
    }

    @Test
    void confirmar_carritoVacio_lanzaNegocioException() {
        Carrito vacio = new Carrito();
        vacio.setId(1L);
        when(carritoService.obtenerOCrearEntidad()).thenReturn(vacio);

        assertThatThrownBy(() -> ordenService.confirmar(new ConfirmarOrdenRequest("hola")))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("vacio");
    }

    @Test
    void confirmar_sinMensaje_funcionaIgual() {
        when(carritoService.obtenerOCrearEntidad()).thenReturn(carritoConItems());
        when(ordenRepository.save(any(Orden.class))).thenAnswer(inv -> {
            Orden o = inv.getArgument(0);
            o.setId(101L);
            return o;
        });

        OrdenDTO dto = ordenService.confirmar(new ConfirmarOrdenRequest(null));

        assertThat(dto.mensaje()).isNull();
        assertThat(dto.total()).isEqualByComparingTo("250.00");
    }

    @Test
    void listar_devuelveHistorialMapeado() {
        Orden orden = new Orden();
        orden.setId(1L);
        orden.setFechaHora(LocalDateTime.now());
        orden.setTotal(new BigDecimal("250.00"));
        when(ordenRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(orden));

        List<OrdenDTO> resultado = ordenService.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).total()).isEqualByComparingTo("250.00");
    }

    @Test
    void obtener_cuandoExiste_devuelveDTO() {
        Orden orden = new Orden();
        orden.setId(7L);
        orden.setFechaHora(LocalDateTime.now());
        orden.setTotal(new BigDecimal("10.00"));
        when(ordenRepository.findById(7L)).thenReturn(Optional.of(orden));

        OrdenDTO dto = ordenService.obtener(7L);

        assertThat(dto.id()).isEqualTo(7L);
    }

    @Test
    void obtener_cuandoNoExiste_lanzaExcepcion() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordenService.obtener(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
