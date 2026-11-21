package com.taller.inventario.backend.repository;

import com.taller.inventario.backend.model.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();

        Producto p1 = new Producto(
                "REP-001",
                "Filtro de Aceite",
                "Mann Filter",
                "Filtros",
                "Unidad",
                2,
                5,
                4000.0,
                7000.0,
                "Sector B",
                "Estante 1",
                "Repisa 1",
                "Distribuidora Sur",
                "BAJO_STOCK"
        );

        Producto p2 = new Producto(
                "REP-002",
                "Aceite 10W-40",
                "Castrol",
                "Aceites y Lubricantes",
                "Bidón 4L",
                15,
                5,
                20000.0,
                30000.0,
                "Sector A",
                "Estante 2",
                "Repisa 2",
                "Castrol Oficial",
                "DISPONIBLE"
        );

        productoRepository.saveAll(List.of(p1, p2));
    }

    @Test
    void testFindById() {
        Optional<Producto> producto = productoRepository.findById("REP-001");
        assertTrue(producto.isPresent());
        assertEquals("Filtro de Aceite", producto.get().getNombre());
    }

    @Test
    void testSearchProductos() {
        List<Producto> encontrados = productoRepository.searchProductos("Mann");
        assertEquals(1, encontrados.size());
        assertEquals("REP-001", encontrados.get(0).getCodigo());

        List<Producto> encontrados2 = productoRepository.searchProductos("Aceite");
        assertEquals(2, encontrados2.size());
    }

    @Test
    void testFindProductosBajoStock() {
        List<Producto> bajoStock = productoRepository.findProductosBajoStock();
        assertEquals(1, bajoStock.size());
        assertEquals("REP-001", bajoStock.get(0).getCodigo());
    }

    @Test
    void testFindByCategoriaIgnoreCaseOrderByNombreAsc() {
        List<Producto> filtros = productoRepository.findByCategoriaIgnoreCaseOrderByNombreAsc("Filtros");
        assertEquals(1, filtros.size());
        assertEquals("Filtro de Aceite", filtros.get(0).getNombre());
    }
}
