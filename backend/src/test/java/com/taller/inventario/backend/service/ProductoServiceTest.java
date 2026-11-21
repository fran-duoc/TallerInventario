package com.taller.inventario.backend.service;

import com.taller.inventario.backend.exception.BusinessRuleException;
import com.taller.inventario.backend.exception.ResourceNotFoundException;
import com.taller.inventario.backend.model.dto.ProductoRequestDto;
import com.taller.inventario.backend.model.dto.ProductoResponseDto;
import com.taller.inventario.backend.model.entity.Producto;
import com.taller.inventario.backend.repository.ProductoRepository;
import com.taller.inventario.backend.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoPrueba;
    private ProductoRequestDto requestPrueba;

    @BeforeEach
    void setUp() {
        productoPrueba = new Producto(
                "REP-001",
                "Filtro de Aire",
                "Mann Filter",
                "Filtros",
                "Unidad",
                10,
                5,
                5000.0,
                8000.0,
                "Sector A",
                "Estante 1",
                "Repisa 1",
                "Proveedor A",
                "DISPONIBLE"
        );

        requestPrueba = new ProductoRequestDto(
                "REP-001",
                "Filtro de Aire",
                "Mann Filter",
                "Filtros",
                "Unidad",
                10,
                5,
                5000.0,
                8000.0,
                "Sector A",
                "Estante 1",
                "Repisa 1",
                "Proveedor A",
                "DISPONIBLE"
        );
    }

    @Test
    void testListarTodos() {
        when(productoRepository.findAll()).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("REP-001", resultado.get(0).getCodigo());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerPorCodigo_Exitoso() {
        when(productoRepository.findById("REP-001")).thenReturn(Optional.of(productoPrueba));

        ProductoResponseDto resultado = productoService.obtenerPorCodigo("REP-001");

        assertNotNull(resultado);
        assertEquals("Filtro de Aire", resultado.getNombre());
        verify(productoRepository, times(1)).findById("REP-001");
    }

    @Test
    void testObtenerPorCodigo_NoEncontrado() {
        when(productoRepository.findById("NO_EXISTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.obtenerPorCodigo("NO_EXISTE"));
        verify(productoRepository, times(1)).findById("NO_EXISTE");
    }

    @Test
    void testRegistrarProducto_Exitoso() {
        when(productoRepository.existsById("REP-001")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoPrueba);

        ProductoResponseDto resultado = productoService.registrarProducto(requestPrueba);

        assertNotNull(resultado);
        assertEquals("REP-001", resultado.getCodigo());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testRegistrarProducto_CodigoDuplicado() {
        when(productoRepository.existsById("REP-001")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> productoService.registrarProducto(requestPrueba));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testRegistrarProducto_PrecioVentaMenorACompra() {
        when(productoRepository.existsById("REP-001")).thenReturn(false);
        requestPrueba.setPrecioVenta(4000.0);
        requestPrueba.setPrecioCompra(5000.0);

        assertThrows(BusinessRuleException.class, () -> productoService.registrarProducto(requestPrueba));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testActualizarProducto_Exitoso() {
        when(productoRepository.findById("REP-001")).thenReturn(Optional.of(productoPrueba));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoPrueba);

        requestPrueba.setNombre("Filtro de Aire Modificado");
        ProductoResponseDto resultado = productoService.actualizarProducto("REP-001", requestPrueba);

        assertNotNull(resultado);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testActualizarProducto_NoEncontrado() {
        when(productoRepository.findById("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.actualizarProducto("INEXISTENTE", requestPrueba));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testEliminarProducto_Exitoso() {
        when(productoRepository.existsById("REP-001")).thenReturn(true);
        doNothing().when(productoRepository).deleteById("REP-001");

        productoService.eliminarProducto("REP-001");

        verify(productoRepository, times(1)).deleteById("REP-001");
    }

    @Test
    void testEliminarProducto_NoEncontrado() {
        when(productoRepository.existsById("NO_EXISTE")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productoService.eliminarProducto("NO_EXISTE"));
        verify(productoRepository, never()).deleteById(anyString());
    }

    @Test
    void testBuscarProductos() {
        when(productoRepository.searchProductos("Mann")).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.buscarProductos("Mann");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).searchProductos("Mann");
    }

    @Test
    void testBuscarProductos_Vacio() {
        when(productoRepository.findAll()).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.buscarProductos("");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testListarBajoStock() {
        when(productoRepository.findProductosBajoStock()).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.listarBajoStock();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findProductosBajoStock();
    }

    @Test
    void testListarPorCategoria() {
        when(productoRepository.findByCategoriaIgnoreCaseOrderByNombreAsc("Filtros")).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.listarPorCategoria("Filtros");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findByCategoriaIgnoreCaseOrderByNombreAsc("Filtros");
    }

    @Test
    void testListarPorCategoria_Vacio() {
        when(productoRepository.findAll()).thenReturn(List.of(productoPrueba));

        List<ProductoResponseDto> resultado = productoService.listarPorCategoria("");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }
}
