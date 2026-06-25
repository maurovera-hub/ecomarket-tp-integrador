package com.ecomarket.config;

import com.ecomarket.model.Producto;
import com.ecomarket.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga datos de prueba en el catalogo al iniciar la app, solo si esta vacio.
 * Se desactiva en el perfil "test" para no interferir con los tests.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DataSeeder(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }

        List<Producto> productos = List.of(
                new Producto("Cepillo de dientes de bambu",
                        "Cepillo biodegradable con cerdas de carbon vegetal. Pack x2.",
                        new BigDecimal("2500.00"), "Higiene personal", 120,
                        "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=600"),
                new Producto("Bolsa reutilizable de algodon organico",
                        "Bolsa de tela resistente, ideal para las compras. Lavable.",
                        new BigDecimal("3200.00"), "Hogar", 200,
                        "https://images.unsplash.com/photo-1597348989645-46b190ce4918?w=600"),
                new Producto("Botella termica de acero inoxidable",
                        "Mantiene la temperatura 12hs. Libre de BPA. 750ml.",
                        new BigDecimal("8900.00"), "Cocina", 80,
                        "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600"),
                new Producto("Shampoo solido natural",
                        "Barra de shampoo sin plastico, apto todo tipo de cabello.",
                        new BigDecimal("4100.00"), "Higiene personal", 150,
                        "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=600"),
                new Producto("Set de sorbetes de acero",
                        "Pack x4 con cepillo de limpieza y bolsa de transporte.",
                        new BigDecimal("3500.00"), "Cocina", 100,
                        "https://images.unsplash.com/photo-1572297870735-066bf1a1c9c0?w=600"),
                new Producto("Vela de cera de soja",
                        "Aroma lavanda, biodegradable y libre de toxinas. 200g.",
                        new BigDecimal("5600.00"), "Hogar", 60,
                        "https://images.unsplash.com/photo-1602874801006-94d6c8a3a3c8?w=600"),
                new Producto("Jabon artesanal de caléndula",
                        "Elaborado con ingredientes naturales y aceites esenciales.",
                        new BigDecimal("2200.00"), "Higiene personal", 180,
                        "https://images.unsplash.com/photo-1600857544200-b2f666a9a2ec?w=600"),
                new Producto("Composteras domestica",
                        "Compostera para interior, ideal para reducir residuos organicos.",
                        new BigDecimal("15900.00"), "Jardin", 35,
                        "https://images.unsplash.com/photo-1591105575633-922c8897af9d?w=600")
        );

        productoRepository.saveAll(productos);
    }
}
