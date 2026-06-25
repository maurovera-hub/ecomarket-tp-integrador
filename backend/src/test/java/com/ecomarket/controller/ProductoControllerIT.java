package com.ecomarket.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String json(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    private Long crearProducto(String nombre) throws Exception {
        ProductoRequest req = new ProductoRequest(nombre, "desc", new BigDecimal("1000.00"),
                "Cocina", 10, "img");
        String body = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void listar_devuelve200YArreglo() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void crear_devuelve201ConLocationYBody() throws Exception {
        ProductoRequest req = new ProductoRequest("Producto Test", "una descripcion",
                new BigDecimal("4500.50"), "Hogar", 25, "http://img");

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre", is("Producto Test")))
                .andExpect(jsonPath("$.precio", is(4500.50)));
    }

    @Test
    void crear_conDatosInvalidos_devuelve400() throws Exception {
        ProductoRequest req = new ProductoRequest("", null, new BigDecimal("-5"), "Hogar", -1, null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errores.nombre").exists());
    }

    @Test
    void obtener_existente_devuelve200() throws Exception {
        Long id = crearProducto("Para obtener");

        mockMvc.perform(get("/api/productos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.nombre", is("Para obtener")));
    }

    @Test
    void obtener_inexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/productos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void actualizar_existente_devuelve200ConDatosNuevos() throws Exception {
        Long id = crearProducto("Original");
        ProductoRequest req = new ProductoRequest("Modificado", "nueva", new BigDecimal("7777.00"),
                "Jardin", 3, "img2");

        mockMvc.perform(put("/api/productos/" + id)
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Modificado")))
                .andExpect(jsonPath("$.precio", is(7777.00)))
                .andExpect(jsonPath("$.stock", is(3)));
    }

    @Test
    void actualizar_inexistente_devuelve404() throws Exception {
        ProductoRequest req = new ProductoRequest("X", "d", new BigDecimal("1.00"), "c", 1, "i");

        mockMvc.perform(put("/api/productos/999999")
                        .contentType(MediaType.APPLICATION_JSON).content(json(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_existente_devuelve204() throws Exception {
        Long id = crearProducto("Para borrar");

        mockMvc.perform(delete("/api/productos/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_inexistente_devuelve404() throws Exception {
        mockMvc.perform(delete("/api/productos/999999"))
                .andExpect(status().isNotFound());
    }
}
