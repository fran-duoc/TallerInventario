package com.taller.inventario.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.taller.inventario.data.local.ProductoEntity

/**
 * DTO para la comunicación de red con el backend Spring Boot.
 */
data class ProductoNetworkDto(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("marca") val marca: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("unidadMedida") val unidadMedida: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("stockMinimo") val stockMinimo: Int,
    @SerializedName("precioCompra") val precioCompra: Double,
    @SerializedName("precioVenta") val precioVenta: Double,
    @SerializedName("ubicacionZona") val ubicacionZona: String?,
    @SerializedName("ubicacionEstante") val ubicacionEstante: String?,
    @SerializedName("ubicacionRepisa") val ubicacionRepisa: String?,
    @SerializedName("proveedorNombre") val proveedorNombre: String?,
    @SerializedName("estado") val estado: String?
) {
    fun toEntity(): ProductoEntity {
        return ProductoEntity(
            codigo = codigo,
            nombre = nombre,
            marca = marca,
            categoria = categoria,
            unidadMedida = unidadMedida.ifBlank { "Unidad" },
            cantidad = cantidad,
            stockMinimo = stockMinimo,
            precioCompra = precioCompra,
            precioVenta = precioVenta,
            ubicacionZona = ubicacionZona?.ifBlank { "General" } ?: "General",
            ubicacionEstante = ubicacionEstante?.ifBlank { "Estante 01" } ?: "Estante 01",
            ubicacionRepisa = ubicacionRepisa?.ifBlank { "Nivel 1" } ?: "Nivel 1",
            proveedorNombre = proveedorNombre?.ifBlank { "Proveedor General" } ?: "Proveedor General",
            estado = estado ?: when {
                cantidad <= 0 -> "AGOTADO"
                cantidad <= stockMinimo -> "BAJO_STOCK"
                else -> "DISPONIBLE"
            }
        )
    }

    companion object {
        fun fromEntity(entity: ProductoEntity): ProductoNetworkDto {
            return ProductoNetworkDto(
                codigo = entity.codigo,
                nombre = entity.nombre,
                marca = entity.marca,
                categoria = entity.categoria,
                unidadMedida = entity.unidadMedida,
                cantidad = entity.cantidad,
                stockMinimo = entity.stockMinimo,
                precioCompra = entity.precioCompra,
                precioVenta = entity.precioVenta,
                ubicacionZona = entity.ubicacionZona,
                ubicacionEstante = entity.ubicacionEstante,
                ubicacionRepisa = entity.ubicacionRepisa,
                proveedorNombre = entity.proveedorNombre,
                estado = entity.estado
            )
        }
    }
}
