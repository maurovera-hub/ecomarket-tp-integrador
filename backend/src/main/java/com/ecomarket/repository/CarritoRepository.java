package com.ecomarket.repository;

import com.ecomarket.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /** Devuelve el primer (y unico) carrito activo si existe. */
    Optional<Carrito> findFirstByOrderByIdAsc();
}
