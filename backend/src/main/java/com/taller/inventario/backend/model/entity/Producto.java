package com.taller.inventario.backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "marca", length = 100, nullable = false)
    private String marca;

    @Column(name = "categoria", length = 100, nullable = false)
    private String categoria;

    @Column(name = "unidad_medida", length = 50, nullable = false)
    private String unidadMedida;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    @Column(name = "ubicacion_zona", length = 100)
    private String ubicacionZona;

    @Column(name = "ubicacion_estante", length = 100)
    private String ubicacionEstante;

    @Column(name = "ubicacion_repisa", length = 100)
    private String ubicacionRepisa;

    @Column(name = "proveedor_nombre", length = 150)
    private String proveedorNombre;

    @Column(name = "estado", length = 50)
    private String estado;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String marca, String categoria, String unidadMedida,
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

    public Integer getCantidadSugeridaReposicion() {
        if (stockMinimo == null || cantidad == null) return 0;
        return Math.max(0, (stockMinimo * 2) - cantidad);
    }

    public String getCalculoEstadoInventario() {
        if (cantidad == null || cantidad <= 0) return "AGOTADO";
        if (stockMinimo != null && cantidad <= stockMinimo) return "BAJO_STOCK";
        return "DISPONIBLE";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(codigo, producto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
