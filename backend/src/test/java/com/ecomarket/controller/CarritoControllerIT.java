package com.ecomarket.controller;

import com.ecomarket.dto.ActualizarCantidadRequest;
import com.ecomarket.dto.AgregarItemRequest;
import com.ecomarket.dto.ProductoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CarritoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    private Long crearProducto(String nombre, String precio) throws Exception {
        ProductoRequest req = new ProductoRequest(nombre, "desc", new BigDecimal(precio),
                "Cocina", 100, "img");
        String body = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    private Long agregarYDevolverItemId(Long productoId, int cantidad) throws Exception {
        String body = mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(productoId, cantidad))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("items").get(0).get("id").asLong();
    }

    @Test
    void obtenerCarrito_devuelve200() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.total").exists());
    }

    @Test
    void agregarItem_devuelve201YCalculaTotal() throws Exception {
        Long productoId = crearProducto("Cafe organico", "1500.00");

        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(productoId, 3))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].cantidad", is(3)))
                .andExpect(jsonPath("$.total", is(4500.00)));
    }

    @Test
    void agregarItem_productoInexistente_devuelve404() throws Exception {
        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(999999L, 1))))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarItem_cantidadInvalida_devuelve400() throws Exception {
        Long productoId = crearProducto("Te verde", "1000.00");

        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(productoId, 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agregarMismoProductoDosVeces_acumulaCantidad() throws Exception {
        Long productoId = crearProducto("Miel", "2000.00");

        mockMvc.perform(post("/api/carrito/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new AgregarItemRequest(productoId, 1))));
        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(productoId, 2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].cantidad", is(3)));
    }

    @Test
    void actualizarCantidad_modificaLinea() throws Exception {
        Long productoId = crearProducto("Avena", "800.00");
        Long itemId = agregarYDevolverItemId(productoId, 2);

        mockMvc.perform(put("/api/carrito/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ActualizarCantidadRequest(5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].cantidad", is(5)))
                .andExpect(jsonPath("$.total", is(4000.00)));
    }

    @Test
    void actualizarCantidad_itemInexistente_devuelve404() throws Exception {
        mockMvc.perform(put("/api/carrito/items/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ActualizarCantidadRequest(2))))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarItem_quitaLineaDelCarrito() throws Exception {
        Long productoId = crearProducto("Quinoa", "3000.00");
        Long itemId = agregarYDevolverItemId(productoId, 1);

        mockMvc.perform(delete("/api/carrito/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void vaciarCarrito_dejaItemsVacios() throws Exception {
        Long productoId = crearProducto("Lentejas", "1200.00");
        agregarYDevolverItemId(productoId, 4);

        mockMvc.perform(delete("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}
