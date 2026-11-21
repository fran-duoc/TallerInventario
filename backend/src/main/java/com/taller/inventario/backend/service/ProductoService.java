package com.taller.inventario.backend.service;

import com.taller.inventario.backend.model.dto.ProductoRequestDto;
import com.taller.inventario.backend.model.dto.ProductoResponseDto;

import java.util.List;

public interface ProductoService {

    List<ProductoResponseDto> listarTodos();

    ProductoResponseDto obtenerPorCodigo(String codigo);

    ProductoResponseDto registrarProducto(ProductoRequestDto request);

    ProductoResponseDto actualizarProducto(String codigo, ProductoRequestDto request);

    void eliminarProducto(String codigo);

    List<ProductoResponseDto> buscarProductos(String query);

    List<ProductoResponseDto> listarBajoStock();

    List<ProductoResponseDto> listarPorCategoria(String categoria);
}
