package com.taller.inventario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia en Room para los repuestos e insumos del taller automotriz.
 * Cumple con el alcance del caso Puente Alto (repuestos, lubricantes, filtros y accesorios).
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey
    val codigo: String,
    val nombre: String,
    val marca: String,
    val categoria: String,
    val unidadMedida: String,
    val cantidad: Int,
    val stockMinimo: Int,
    val precioCompra: Double,
    val precioVenta: Double,
    val ubicacionZona: String,
    val ubicacionEstante: String,
    val ubicacionRepisa: String,
    val proveedorNombre: String,
    val estado: String
) {
    /**
     * Regla de reposición automática mínima definida en el caso:
     * cantidadSugeridaReposicion = maxOf(0, (stockMinimo * 2) - cantidad)
     */
    val cantidadSugeridaReposicion: Int
        get() = maxOf(0, (stockMinimo * 2) - cantidad)

    /**
     * Estado dinámico de inventario según stock y umbral mínimo.
     */
    val estadoInventario: EstadoInventario
        get() = when {
            cantidad <= 0 -> EstadoInventario.AGOTADO
            cantidad <= stockMinimo -> EstadoInventario.BAJO_STOCK
            else -> EstadoInventario.DISPONIBLE
        }

    /**
     * Ubicación completa concatenada para despliegue rápido.
     */
    val ubicacionCompleta: String
        get() = listOfNotNull(
            ubicacionZona.takeIf { it.isNotBlank() },
            ubicacionEstante.takeIf { it.isNotBlank() },
            ubicacionRepisa.takeIf { it.isNotBlank() }
        ).joinToString(" • ")
}

enum class EstadoInventario(val etiqueta: String) {
    DISPONIBLE("Disponible"),
    BAJO_STOCK("Bajo Stock"),
    AGOTADO("Agotado")
}
