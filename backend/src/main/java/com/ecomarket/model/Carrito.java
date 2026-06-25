package com.ecomarket.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras. La aplicacion maneja un unico carrito activo
 * (no hay usuarios autenticados en el alcance del TP), que se vacia
 * cuando el cliente confirma una orden.
 */
@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    public Carrito() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public void agregarItem(ItemCarrito item) {
        item.setCarrito(this);
        this.items.add(item);
    }

    public void eliminarItem(ItemCarrito item) {
        this.items.remove(item);
        item.setCarrito(null);
    }

    public void vaciar() {
        this.items.clear();
    }

    /** Total del carrito sumando precio unitario por cantidad de cada item. */
    public BigDecimal getTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
