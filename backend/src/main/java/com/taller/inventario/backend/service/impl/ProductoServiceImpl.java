package com.taller.inventario.backend.service.impl;

import com.taller.inventario.backend.exception.BusinessRuleException;
import com.taller.inventario.backend.exception.ResourceNotFoundException;
import com.taller.inventario.backend.model.dto.ProductoRequestDto;
import com.taller.inventario.backend.model.dto.ProductoResponseDto;
import com.taller.inventario.backend.model.entity.Producto;
import com.taller.inventario.backend.repository.ProductoRepository;
import com.taller.inventario.backend.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(ProductoResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDto obtenerPorCodigo(String codigo) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el repuesto con código: " + codigo));
        return ProductoResponseDto.fromEntity(producto);
    }

    @Override
    public ProductoResponseDto registrarProducto(ProductoRequestDto request) {
        String codigoNormalizado = request.getCodigo().trim().toUpperCase();

        if (productoRepository.existsById(codigoNormalizado)) {
            throw new BusinessRuleException("Ya existe un repuesto registrado con el código: " + codigoNormalizado);
        }

        validarReglasDeNegocio(request);

        String estadoCalculado = calcularEstado(request.getCantidad(), request.getStockMinimo());

        Producto producto = new Producto(
                codigoNormalizado,
                request.getNombre().trim(),
                request.getMarca().trim(),
                request.getCategoria().trim(),
                (request.getUnidadMedida() != null && !request.getUnidadMedida().isBlank()) ? request.getUnidadMedida().trim() : "Unidad",
                request.getCantidad(),
                request.getStockMinimo(),
                request.getPrecioCompra(),
                request.getPrecioVenta(),
                (request.getUbicacionZona() != null && !request.getUbicacionZona().isBlank()) ? request.getUbicacionZona().trim() : "General",
                (request.getUbicacionEstante() != null && !request.getUbicacionEstante().isBlank()) ? request.getUbicacionEstante().trim() : "Estante 01",
                (request.getUbicacionRepisa() != null && !request.getUbicacionRepisa().isBlank()) ? request.getUbicacionRepisa().trim() : "Nivel 1",
                (request.getProveedorNombre() != null && !request.getProveedorNombre().isBlank()) ? request.getProveedorNombre().trim() : "Proveedor General",
                estadoCalculado
        );

        Producto guardado = productoRepository.save(producto);
        return ProductoResponseDto.fromEntity(guardado);
    }

    @Override
    public ProductoResponseDto actualizarProducto(String codigo, ProductoRequestDto request) {
        String codigoNormalizado = codigo.trim().toUpperCase();

        Producto productoExistente = productoRepository.findById(codigoNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el repuesto con código: " + codigoNormalizado));

        validarReglasDeNegocio(request);

        String estadoCalculado = calcularEstado(request.getCantidad(), request.getStockMinimo());

        productoExistente.setNombre(request.getNombre().trim());
        productoExistente.setMarca(request.getMarca().trim());
        productoExistente.setCategoria(request.getCategoria().trim());
        productoExistente.setUnidadMedida((request.getUnidadMedida() != null && !request.getUnidadMedida().isBlank()) ? request.getUnidadMedida().trim() : "Unidad");
        productoExistente.setCantidad(request.getCantidad());
        productoExistente.setStockMinimo(request.getStockMinimo());
        productoExistente.setPrecioCompra(request.getPrecioCompra());
        productoExistente.setPrecioVenta(request.getPrecioVenta());
        productoExistente.setUbicacionZona((request.getUbicacionZona() != null && !request.getUbicacionZona().isBlank()) ? request.getUbicacionZona().trim() : "General");
        productoExistente.setUbicacionEstante((request.getUbicacionEstante() != null && !request.getUbicacionEstante().isBlank()) ? request.getUbicacionEstante().trim() : "Estante 01");
        productoExistente.setUbicacionRepisa((request.getUbicacionRepisa() != null && !request.getUbicacionRepisa().isBlank()) ? request.getUbicacionRepisa().trim() : "Nivel 1");
        productoExistente.setProveedorNombre((request.getProveedorNombre() != null && !request.getProveedorNombre().isBlank()) ? request.getProveedorNombre().trim() : "Proveedor General");
        productoExistente.setEstado(estadoCalculado);

        Producto actualizado = productoRepository.save(productoExistente);
        return ProductoResponseDto.fromEntity(actualizado);
    }

    @Override
    public void eliminarProducto(String codigo) {
        String codigoNormalizado = codigo.trim().toUpperCase();
        if (!productoRepository.existsById(codigoNormalizado)) {
            throw new ResourceNotFoundException("No se puede eliminar porque no existe el repuesto con código: " + codigoNormalizado);
        }
        productoRepository.deleteById(codigoNormalizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> buscarProductos(String query) {
        if (query == null || query.isBlank()) {
            return listarTodos();
        }
        return productoRepository.searchProductos(query.trim())
                .stream()
                .map(ProductoResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listarBajoStock() {
        return productoRepository.findProductosBajoStock()
                .stream()
                .map(ProductoResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listarPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return listarTodos();
        }
        return productoRepository.findByCategoriaIgnoreCaseOrderByNombreAsc(categoria.trim())
                .stream()
                .map(ProductoResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void validarReglasDeNegocio(ProductoRequestDto request) {
        if (request.getPrecioCompra() != null && request.getPrecioVenta() != null) {
            if (request.getPrecioVenta() < request.getPrecioCompra()) {
                throw new BusinessRuleException(String.format(
                        "El precio de venta (%.2f) no puede ser inferior al precio de costo/compra (%.2f)",
                        request.getPrecioVenta(), request.getPrecioCompra()));
            }
        }
    }

    private String calcularEstado(Integer cantidad, Integer stockMinimo) {
        int cant = (cantidad != null) ? cantidad : 0;
        int stkMin = (stockMinimo != null) ? stockMinimo : 0;
        if (cant <= 0) return "AGOTADO";
        if (cant <= stkMin) return "BAJO_STOCK";
        return "DISPONIBLE";
    }
}
