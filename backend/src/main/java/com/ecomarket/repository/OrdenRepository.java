package com.ecomarket.repository;

import com.ecomarket.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    /** Historial de ordenes, de la mas reciente a la mas antigua. */
    List<Orden> findAllByOrderByFechaHoraDesc();
}
