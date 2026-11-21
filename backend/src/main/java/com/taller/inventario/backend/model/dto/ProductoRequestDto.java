package com.taller.inventario.backend.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductoRequestDto {

    @NotBlank(message = "El código del producto es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    private String unidadMedida = "Unidad";

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    private Integer cantidad;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0")
    private Integer stockMinimo;

    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra debe ser mayor o igual a 0")
    private Double precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta debe ser mayor o igual a 0")
    private Double precioVenta;

    private String ubicacionZona = "General";
    private String ubicacionEstante = "Estante 01";
    private String ubicacionRepisa = "Nivel 1";
    private String proveedorNombre = "Proveedor General";
    private String estado;

    public ProductoRequestDto() {
    }

    public ProductoRequestDto(String codigo, String nombre, String marca, String categoria, String unidadMedida,
                             Integer cantidad, Integer stockMinimo, Double precioCompra, Double precioVenta,
                             String ubicacionZona, String ubicacionEstante, String ubicacionRepisa,
                             String proveedorNombre, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.ubicacionZona = ubicacionZona;
        this.ubicacionEstante = ubicacionEstante;
        this.ubicacionRepisa = ubicacionRepisa;
        this.proveedorNombre = proveedorNombre;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getUbicacionZona() {
        return ubicacionZona;
    }

    public void setUbicacionZona(String ubicacionZona) {
        this.ubicacionZona = ubicacionZona;
    }

    public String getUbicacionEstante() {
        return ubicacionEstante;
    }

    public void setUbicacionEstante(String ubicacionEstante) {
        this.ubicacionEstante = ubicacionEstante;
    }

    public String getUbicacionRepisa() {
        return ubicacionRepisa;
    }

    public void setUbicacionRepisa(String ubicacionRepisa) {
        this.ubicacionRepisa = ubicacionRepisa;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
