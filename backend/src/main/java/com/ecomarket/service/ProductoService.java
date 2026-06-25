package com.ecomarket.service;

import com.ecomarket.dto.ProductoDTO;
import com.ecomarket.dto.ProductoRequest;
import com.ecomarket.exception.RecursoNoEncontradoException;
import com.ecomarket.mapper.EcoMapper;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio del catalogo de productos.
 */
@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listar() {
        return productoRepository.findAll().stream()
                .map(EcoMapper::toProductoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtener(Long id) {
        Producto producto = buscarOFallar(id);
        return EcoMapper.toProductoDTO(producto);
    }

    public ProductoDTO crear(ProductoRequest request) {
        Producto producto = new Producto(
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.categoria(),
                request.stock(),
                request.imagenUrl());
        Producto guardado = productoRepository.save(producto);
        return EcoMapper.toProductoDTO(guardado);
    }

    public ProductoDTO actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarOFallar(id);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setCategoria(request.categoria());
        producto.setStock(request.stock());
        producto.setImagenUrl(request.imagenUrl());
        Producto actualizado = productoRepository.save(producto);
        return EcoMapper.toProductoDTO(actualizado);
    }

    public void eliminar(Long id) {
        Producto producto = buscarOFallar(id);
        productoRepository.delete(producto);
    }

    private Producto buscarOFallar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el producto con id " + id));
    }
}
