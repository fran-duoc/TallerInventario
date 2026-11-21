package com.taller.inventario.backend.model.dto;

import com.taller.inventario.backend.model.entity.Producto;

public class ProductoResponseDto {

    private String codigo;
    private String nombre;
    private String marca;
    private String categoria;
    private String unidadMedida;
    private Integer cantidad;
    private Integer stockMinimo;
    private Double precioCompra;
    private Double precioVenta;
    private String ubicacionZona;
    private String ubicacionEstante;
    private String ubicacionRepisa;
    private String proveedorNombre;
    private String estado;
    private Integer cantidadSugeridaReposicion;
    private String estadoInventario;
    private String ubicacionCompleta;

    public ProductoResponseDto() {
    }

    public static ProductoResponseDto fromEntity(Producto p) {
        if (p == null) return null;
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setCodigo(p.getCodigo());
        dto.setNombre(p.getNombre());
        dto.setMarca(p.getMarca());
        dto.setCategoria(p.getCategoria());
        dto.setUnidadMedida(p.getUnidadMedida());
        dto.setCantidad(p.getCantidad());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setPrecioCompra(p.getPrecioCompra());
        dto.setPrecioVenta(p.getPrecioVenta());
        dto.setUbicacionZona(p.getUbicacionZona());
        dto.setUbicacionEstante(p.getUbicacionEstante());
        dto.setUbicacionRepisa(p.getUbicacionRepisa());
        dto.setProveedorNombre(p.getProveedorNombre());
        dto.setEstado(p.getEstado());

        dto.setCantidadSugeridaReposicion(p.getCantidadSugeridaReposicion());
        dto.setEstadoInventario(p.getCalculoEstadoInventario());

        StringBuilder ubicacion = new StringBuilder();
        if (p.getUbicacionZona() != null && !p.getUbicacionZona().isBlank()) {
            ubicacion.append(p.getUbicacionZona());
        }
        if (p.getUbicacionEstante() != null && !p.getUbicacionEstante().isBlank()) {
            if (ubicacion.length() > 0) ubicacion.append(" • ");
            ubicacion.append(p.getUbicacionEstante());
        }
        if (p.getUbicacionRepisa() != null && !p.getUbicacionRepisa().isBlank()) {
            if (ubicacion.length() > 0) ubicacion.append(" • ");
            ubicacion.append(p.getUbicacionRepisa());
        }
        dto.setUbicacionCompleta(ubicacion.toString());

        return dto;
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

    public Integer getCantidadSugeridaReposicion() {
        return cantidadSugeridaReposicion;
    }

    public void setCantidadSugeridaReposicion(Integer cantidadSugeridaReposicion) {
        this.cantidadSugeridaReposicion = cantidadSugeridaReposicion;
    }

    public String getEstadoInventario() {
        return estadoInventario;
    }

    public void setEstadoInventario(String estadoInventario) {
        this.estadoInventario = estadoInventario;
    }

    public String getUbicacionCompleta() {
        return ubicacionCompleta;
    }

    public void setUbicacionCompleta(String ubicacionCompleta) {
        this.ubicacionCompleta = ubicacionCompleta;
    }
}
