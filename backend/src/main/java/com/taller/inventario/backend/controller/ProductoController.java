package com.taller.inventario.backend.controller;

import com.taller.inventario.backend.model.dto.ProductoRequestDto;
import com.taller.inventario.backend.model.dto.ProductoResponseDto;
import com.taller.inventario.backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listarTodos(
            @RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoResponseDto> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> registrarProducto(
            @Valid @RequestBody ProductoRequestDto request) {
        ProductoResponseDto creado = productoService.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProductoResponseDto> actualizarProducto(
            @PathVariable String codigo,
            @Valid @RequestBody ProductoRequestDto request) {
        ProductoResponseDto actualizado = productoService.actualizarProducto(codigo, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String codigo) {
        productoService.eliminarProducto(codigo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDto>> buscarProductos(@RequestParam String query) {
        return ResponseEntity.ok(productoService.buscarProductos(query));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoResponseDto>> listarBajoStock() {
        return ResponseEntity.ok(productoService.listarBajoStock());
    }
}
