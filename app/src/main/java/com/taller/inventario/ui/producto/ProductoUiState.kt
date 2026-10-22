package com.taller.inventario.ui.producto

import com.taller.inventario.data.local.ProductoEntity

enum class ScannerTarget {
    BUSQUEDA,
    CODIGO_FORM
}

/**
 * Estado inmutable de la interfaz de usuario para el módulo de inventario.
 * Proporciona campos desacoplados en String para la entrada de datos en formularios,
 * errores independientes por campo y estados reactivos del flujo general.
 */
data class ProductoUiState(
    // Campos del formulario
    val codigo: String = "",
    val nombre: String = "",
    val marca: String = "",
    val categoria: String = "Aceites y Lubricantes",
    val unidadMedida: String = "Unidad",
    val cantidad: String = "",
    val stockMinimo: String = "",
    val precioCompra: String = "",
    val precioVenta: String = "",
    val ubicacionZona: String = "",
    val ubicacionEstante: String = "",
    val ubicacionRepisa: String = "",
    val proveedorNombre: String = "",
    val estado: String = "ACTIVO",

    // Manejo de errores por campo
    val codigoError: String? = null,
    val nombreError: String? = null,
    val marcaError: String? = null,
    val categoriaError: String? = null,
    val cantidadError: String? = null,
    val stockMinimoError: String? = null,
    val precioCompraError: String? = null,
    val precioVentaError: String? = null,
    val precioError: String? = null,

    // Banderas de control de interfaz y flujo
    val isEditing: Boolean = false,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,
    val searchQuery: String = "",
    val filtroCategoria: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null,

    // Colección de datos
    val productos: List<ProductoEntity> = emptyList(),

    // Control de diálogos modales y escáner nativo
    val productoAEliminar: ProductoEntity? = null,
    val mostrarDialogoEliminar: Boolean = false,
    val mostrarScanner: Boolean = false,
    val scannerTarget: ScannerTarget = ScannerTarget.CODIGO_FORM
) {
    /**
     * Devuelve true si no hay errores en los campos del formulario.
     */
    val tieneErrores: Boolean
        get() = codigoError != null ||
                nombreError != null ||
                marcaError != null ||
                categoriaError != null ||
                cantidadError != null ||
                stockMinimoError != null ||
                precioCompraError != null ||
                precioVentaError != null ||
                precioError != null
}
