package com.ecomarket.controller;

import com.ecomarket.dto.AgregarItemRequest;
import com.ecomarket.dto.ConfirmarOrdenRequest;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrdenControllerIT {

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

    private void agregarAlCarrito(Long productoId, int cantidad) throws Exception {
        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AgregarItemRequest(productoId, cantidad))))
                .andExpect(status().isCreated());
    }

    @Test
    void confirmar_conCarritoConItems_devuelve201YRegistraOrden() throws Exception {
        Long p1 = crearProducto("Producto A", "100.00");
        Long p2 = crearProducto("Producto B", "250.00");
        agregarAlCarrito(p1, 2); // 200
        agregarAlCarrito(p2, 1); // 250

        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ConfirmarOrdenRequest("Entregar por la tarde"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.mensaje", is("Entregar por la tarde")))
                .andExpect(jsonPath("$.fechaHora").exists())
                .andExpect(jsonPath("$.total", is(450.00)))
                .andExpect(jsonPath("$.items", hasSize(2)));
    }

    @Test
    void confirmar_vaciaElCarrito() throws Exception {
        Long p1 = crearProducto("Producto C", "500.00");
        agregarAlCarrito(p1, 1);

        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ConfirmarOrdenRequest("gracias"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void confirmar_carritoVacio_devuelve400() throws Exception {
        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ConfirmarOrdenRequest("sin nada"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void confirmar_sinBody_conItems_funciona() throws Exception {
        Long p1 = crearProducto("Producto D", "300.00");
        agregarAlCarrito(p1, 2);

        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total", is(600.00)));
    }

    @Test
    void historial_devuelveOrdenesConfirmadas() throws Exception {
        Long p1 = crearProducto("Producto E", "100.00");
        agregarAlCarrito(p1, 1);
        mockMvc.perform(post("/api/ordenes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ConfirmarOrdenRequest("orden de prueba"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/ordenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void obtenerOrden_inexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/ordenes/999999"))
                .andExpect(status().isNotFound());
    }
}
