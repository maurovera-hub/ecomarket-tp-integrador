package com.ecomarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion global de CORS.
 *
 * El frontend (React + Vite) corre por defecto en http://localhost:5173 y,
 * en su defecto, en http://localhost:3000 (Create React App). Habilitamos
 * ambos origenes para que el navegador no bloquee las requests por la
 * politica same-origin.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${ecomarket.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
