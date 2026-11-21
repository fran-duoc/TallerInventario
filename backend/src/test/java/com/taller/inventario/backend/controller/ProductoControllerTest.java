package com.taller.inventario.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.inventario.backend.model.dto.ProductoRequestDto;
import com.taller.inventario.backend.model.dto.ProductoResponseDto;
import com.taller.inventario.backend.model.entity.Producto;
import com.taller.inventario.backend.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoResponseDto responsePrueba;
    private ProductoRequestDto requestValido;

    @BeforeEach
    void setUp() {
        Producto producto = new Producto(
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
        responsePrueba = ProductoResponseDto.fromEntity(producto);

        requestValido = new ProductoRequestDto(
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
    void testListarTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of(responsePrueba));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("REP-001"))
                .andExpect(jsonPath("$[0].nombre").value("Filtro de Aire"));

        verify(productoService, times(1)).listarTodos();
    }

    @Test
    void testListarPorCategoria() throws Exception {
        when(productoService.listarPorCategoria("Filtros")).thenReturn(List.of(responsePrueba));

        mockMvc.perform(get("/api/productos?categoria=Filtros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Filtros"));

        verify(productoService, times(1)).listarPorCategoria("Filtros");
    }

    @Test
    void testObtenerPorCodigo() throws Exception {
        when(productoService.obtenerPorCodigo("REP-001")).thenReturn(responsePrueba);

        mockMvc.perform(get("/api/productos/REP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("REP-001"));

        verify(productoService, times(1)).obtenerPorCodigo("REP-001");
    }

    @Test
    void testRegistrarProducto_Valido() throws Exception {
        when(productoService.registrarProducto(any(ProductoRequestDto.class))).thenReturn(responsePrueba);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("REP-001"));

        verify(productoService, times(1)).registrarProducto(any(ProductoRequestDto.class));
    }

    @Test
    void testRegistrarProducto_Invalido() throws Exception {
        ProductoRequestDto requestInvalido = new ProductoRequestDto();
        // Campos vacíos

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(productoService, never()).registrarProducto(any(ProductoRequestDto.class));
    }

    @Test
    void testActualizarProducto() throws Exception {
        when(productoService.actualizarProducto(eq("REP-001"), any(ProductoRequestDto.class))).thenReturn(responsePrueba);

        mockMvc.perform(put("/api/productos/REP-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("REP-001"));

        verify(productoService, times(1)).actualizarProducto(eq("REP-001"), any(ProductoRequestDto.class));
    }

    @Test
    void testEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminarProducto("REP-001");

        mockMvc.perform(delete("/api/productos/REP-001"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminarProducto("REP-001");
    }

    @Test
    void testBuscarProductos() throws Exception {
        when(productoService.buscarProductos("Mann")).thenReturn(List.of(responsePrueba));

        mockMvc.perform(get("/api/productos/buscar?query=Mann"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("REP-001"));

        verify(productoService, times(1)).buscarProductos("Mann");
    }

    @Test
    void testListarBajoStock() throws Exception {
        when(productoService.listarBajoStock()).thenReturn(List.of(responsePrueba));

        mockMvc.perform(get("/api/productos/bajo-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("REP-001"));

        verify(productoService, times(1)).listarBajoStock();
    }
}
