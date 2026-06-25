package com.ecomarket.service;

import com.ecomarket.dto.ProductoDTO;
import com.ecomarket.dto.ProductoRequest;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto(Long id, String nombre, String precio) {
        Producto p = new Producto(nombre, "desc", new BigDecimal(precio), "Cocina", 10, "img");
        p.setId(id);
        return p;
    }

    @Test
    void listar_devuelveTodosLosProductosMapeados() {
        when(productoRepository.findAll()).thenReturn(List.of(
                producto(1L, "A", "100.00"),
                producto(2L, "B", "200.00")));

        List<ProductoDTO> resultado = productoService.listar();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nombre()).isEqualTo("A");
        assertThat(resultado.get(1).precio()).isEqualByComparingTo("200.00");
    }

    @Test
    void obtener_cuandoExiste_devuelveDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1L, "A", "100.00")));

        ProductoDTO dto = productoService.obtener(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nombre()).isEqualTo("A");
    }

    @Test
    void obtener_cuandoNoExiste_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtener(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crear_guardaYDevuelveDTO() {
        ProductoRequest request = new ProductoRequest("Nuevo", "d", new BigDecimal("500.00"),
                "Hogar", 5, "img");
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProductoDTO dto = productoService.crear(request);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.nombre()).isEqualTo("Nuevo");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizar_cuandoExiste_modificaCampos() {
        Producto existente = producto(1L, "Viejo", "100.00");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest request = new ProductoRequest("Editado", "d", new BigDecimal("999.00"),
                "Jardin", 3, "img2");
        ProductoDTO dto = productoService.actualizar(1L, request);

        assertThat(dto.nombre()).isEqualTo("Editado");
        assertThat(dto.precio()).isEqualByComparingTo("999.00");
        assertThat(dto.stock()).isEqualTo(3);
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        ProductoRequest request = new ProductoRequest("X", "d", new BigDecimal("1.00"), "c", 1, "i");

        assertThatThrownBy(() -> productoService.actualizar(99L, request))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_borra() {
        Producto existente = producto(1L, "A", "100.00");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));

        productoService.eliminar(1L);

        verify(productoRepository).delete(existente);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(productoRepository, never()).delete(any());
    }
}
