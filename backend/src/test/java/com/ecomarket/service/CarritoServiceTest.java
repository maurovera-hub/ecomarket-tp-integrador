package com.ecomarket.service;

import com.ecomarket.dto.ActualizarCantidadRequest;
import com.ecomarket.dto.AgregarItemRequest;
import com.ecomarket.dto.CarritoDTO;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.model.Carrito;
import com.ecomarket.model.ItemCarrito;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.CarritoRepository;
import com.ecomarket.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CarritoService carritoService;

    private Producto producto(Long id, String precio) {
        Producto p = new Producto("Prod" + id, "d", new BigDecimal(precio), "Cocina", 50, "img");
        p.setId(id);
        return p;
    }

    private ItemCarrito item(Long id, Producto producto, int cantidad) {
        ItemCarrito i = new ItemCarrito(producto, cantidad);
        i.setId(id);
        return i;
    }

    @Test
    void obtenerCarrito_cuandoNoExiste_creaUnoVacio() {
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> {
            Carrito c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CarritoDTO dto = carritoService.obtenerCarrito();

        assertThat(dto.items()).isEmpty();
        assertThat(dto.total()).isEqualByComparingTo("0");
    }

    @Test
    void agregarItem_productoNuevo_loAgregaAlCarrito() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(5L)).thenReturn(Optional.of(producto(5L, "100.00")));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoDTO dto = carritoService.agregarItem(new AgregarItemRequest(5L, 2));

        assertThat(dto.items()).hasSize(1);
        assertThat(dto.items().get(0).cantidad()).isEqualTo(2);
        assertThat(dto.total()).isEqualByComparingTo("200.00");
    }

    @Test
    void agregarItem_productoYaEnCarrito_incrementaCantidad() {
        Producto p = producto(5L, "100.00");
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.agregarItem(item(10L, p, 1));
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(5L)).thenReturn(Optional.of(p));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoDTO dto = carritoService.agregarItem(new AgregarItemRequest(5L, 3));

        assertThat(dto.items()).hasSize(1);
        assertThat(dto.items().get(0).cantidad()).isEqualTo(4);
        assertThat(dto.total()).isEqualByComparingTo("400.00");
    }

    @Test
    void agregarItem_productoInexistente_lanzaExcepcion() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carritoService.agregarItem(new AgregarItemRequest(99L, 1)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizarCantidad_modificaLaLinea() {
        Producto p = producto(5L, "100.00");
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.agregarItem(item(10L, p, 1));
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoDTO dto = carritoService.actualizarCantidad(10L, new ActualizarCantidadRequest(7));

        assertThat(dto.items().get(0).cantidad()).isEqualTo(7);
        assertThat(dto.total()).isEqualByComparingTo("700.00");
    }

    @Test
    void actualizarCantidad_itemInexistente_lanzaExcepcion() {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));

        assertThatThrownBy(() -> carritoService.actualizarCantidad(999L, new ActualizarCantidadRequest(2)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminarItem_quitaLaLineaDelCarrito() {
        Producto p = producto(5L, "100.00");
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.agregarItem(item(10L, p, 2));
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoDTO dto = carritoService.eliminarItem(10L);

        assertThat(dto.items()).isEmpty();
        assertThat(dto.total()).isEqualByComparingTo("0");
    }

    @Test
    void vaciar_dejaElCarritoSinItems() {
        Producto p = producto(5L, "100.00");
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.agregarItem(item(10L, p, 2));
        when(carritoRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoDTO dto = carritoService.vaciar();

        assertThat(dto.items()).isEmpty();
    }
}
