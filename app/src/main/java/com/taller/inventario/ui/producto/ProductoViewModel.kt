package com.taller.inventario.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState(isLoading = true))
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _filtroCategoria = MutableStateFlow<String?>(null)

    init {
        observarProductos()
    }

    /**
     * Observa el catálogo completo de productos desde Room y aplica reactivamente
     * los filtros de búsqueda por texto (código, nombre o marca) y por categoría.
     */
    private fun observarProductos() {
        viewModelScope.launch {
            combine(
                repository.getAllProductos(),
                _searchQuery,
                _filtroCategoria
            ) { productos, query, categoria ->
                val queryNormalizada = query.trim().lowercase()
                productos.filter { item ->
                    val coincideTexto = if (queryNormalizada.isBlank()) {
                        true
                    } else {
                        item.nombre.lowercase().contains(queryNormalizada) ||
                                item.codigo.lowercase().contains(queryNormalizada) ||
                                item.marca.lowercase().contains(queryNormalizada) ||
                                item.categoria.lowercase().contains(queryNormalizada)
                    }

                    val coincideCategoria = if (categoria.isNullOrBlank()) {
                        true
                    } else if (categoria == "Bajo Stock / Crítico") {
                        item.cantidad <= item.stockMinimo
                    } else {
                        item.categoria.equals(categoria, ignoreCase = true)
                    }

                    coincideTexto && coincideCategoria
                }
            }.collect { listaFiltrada ->
                _uiState.update { state ->
                    state.copy(
                        productos = listaFiltrada,
                        isLoading = false,
                        searchQuery = _searchQuery.value,
                        filtroCategoria = _filtroCategoria.value
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Filtros y Búsqueda
    // -------------------------------------------------------------

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoriaFilterChange(categoria: String?) {
        _filtroCategoria.value = categoria
        _uiState.update { it.copy(filtroCategoria = categoria) }
    }

    // -------------------------------------------------------------
    // Formularios: Entrada y actualización de campos
    // -------------------------------------------------------------

    fun onCodigoChange(valor: String) {
        _uiState.update { it.copy(codigo = valor.trim().uppercase(), codigoError = null) }
    }

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onMarcaChange(valor: String) {
        _uiState.update { it.copy(marca = valor, marcaError = null) }
    }

    fun onCategoriaChange(valor: String) {
        _uiState.update { it.copy(categoria = valor, categoriaError = null) }
    }

    fun onUnidadMedidaChange(valor: String) {
        _uiState.update { it.copy(unidadMedida = valor) }
    }

    fun onCantidadChange(valor: String) {
        val filtrado = valor.filter { it.isDigit() }
        _uiState.update { it.copy(cantidad = filtrado, cantidadError = null) }
    }

    fun onStockMinimoChange(valor: String) {
        val filtrado = valor.filter { it.isDigit() }
        _uiState.update { it.copy(stockMinimo = filtrado, stockMinimoError = null) }
    }

    fun onPrecioCompraChange(valor: String) {
        val filtrado = valor.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                precioCompra = filtrado,
                precioCompraError = null,
                precioError = null,
                precioVentaError = null
            )
        }
    }

    fun onPrecioVentaChange(valor: String) {
        val filtrado = valor.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                precioVenta = filtrado,
                precioVentaError = null,
                precioError = null
            )
        }
    }

    fun onUbicacionZonaChange(valor: String) {
        _uiState.update { it.copy(ubicacionZona = valor) }
    }

    fun onUbicacionEstanteChange(valor: String) {
        _uiState.update { it.copy(ubicacionEstante = valor) }
    }

    fun onUbicacionRepisaChange(valor: String) {
        _uiState.update { it.copy(ubicacionRepisa = valor) }
    }

    fun onProveedorNombreChange(valor: String) {
        _uiState.update { it.copy(proveedorNombre = valor) }
    }

    fun onEstadoChange(valor: String) {
        _uiState.update { it.copy(estado = valor) }
    }

    // -------------------------------------------------------------
    // Flujo de navegación y preparación de datos
    // -------------------------------------------------------------

    fun iniciarCreacion() {
        _uiState.update {
            ProductoUiState(
                productos = it.productos,
                searchQuery = it.searchQuery,
                filtroCategoria = it.filtroCategoria,
                isEditing = false,
                guardadoExitosamente = false
            )
        }
    }

    fun cargarProductoParaEdicion(codigo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val producto = repository.getProductoByCodigo(codigo)
            if (producto != null) {
                _uiState.update { state ->
                    state.copy(
                        codigo = producto.codigo,
                        nombre = producto.nombre,
                        marca = producto.marca,
                        categoria = producto.categoria,
                        unidadMedida = producto.unidadMedida,
                        cantidad = producto.cantidad.toString(),
                        stockMinimo = producto.stockMinimo.toString(),
                        precioCompra = if (producto.precioCompra % 1.0 == 0.0) producto.precioCompra.toInt().toString() else producto.precioCompra.toString(),
                        precioVenta = if (producto.precioVenta % 1.0 == 0.0) producto.precioVenta.toInt().toString() else producto.precioVenta.toString(),
                        ubicacionZona = producto.ubicacionZona,
                        ubicacionEstante = producto.ubicacionEstante,
                        ubicacionRepisa = producto.ubicacionRepisa,
                        proveedorNombre = producto.proveedorNombre,
                        estado = producto.estado,
                        isEditing = true,
                        isLoading = false,
                        guardadoExitosamente = false,
                        codigoError = null,
                        nombreError = null,
                        marcaError = null,
                        categoriaError = null,
                        cantidadError = null,
                        stockMinimoError = null,
                        precioCompraError = null,
                        precioVentaError = null,
                        precioError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se encontró el repuesto con código: $codigo"
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Validaciones estrictas y guardado en Room
    // -------------------------------------------------------------

    fun validarYGuardar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value

            var tieneError = false
            var codError: String? = null
            var nomError: String? = null
            var marError: String? = null
            var catError: String? = null
            var canError: String? = null
            var stkError: String? = null
            var preComError: String? = null
            var preVenError: String? = null

            // 1. Código obligatorio y único (si es nuevo)
            val codigoTrim = currentState.codigo.trim()
            if (codigoTrim.isBlank()) {
                codError = "El código del producto es obligatorio"
                tieneError = true
            } else if (!currentState.isEditing) {
                val existe = repository.getProductoByCodigo(codigoTrim)
                if (existe != null) {
                    codError = "Ya existe un repuesto con el código '$codigoTrim'"
                    tieneError = true
                }
            }

            // 2. Nombre obligatorio
            val nombreTrim = currentState.nombre.trim()
            if (nombreTrim.isBlank()) {
                nomError = "El nombre del producto es obligatorio"
                tieneError = true
            }

            // 3. Marca obligatoria
            val marcaTrim = currentState.marca.trim()
            if (marcaTrim.isBlank()) {
                marError = "La marca es obligatoria (ej: Castrol, Bosch, Mann Filter)"
                tieneError = true
            }

            // 4. Categoría obligatoria
            val categoriaTrim = currentState.categoria.trim()
            if (categoriaTrim.isBlank()) {
                catError = "La categoría es obligatoria"
                tieneError = true
            }

            // 5. Cantidad: Entero >= 0
            val cantidadInt = currentState.cantidad.toIntOrNull()
            if (cantidadInt == null || cantidadInt < 0) {
                canError = "La cantidad debe ser un número entero mayor o igual a 0"
                tieneError = true
            }

            // 6. Stock Mínimo: Entero >= 0
            val stockMinimoInt = currentState.stockMinimo.toIntOrNull()
            if (stockMinimoInt == null || stockMinimoInt < 0) {
                stkError = "El stock mínimo debe ser un número entero mayor o igual a 0"
                tieneError = true
            }

            // 7. Precios: Reales >= 0 y regla de negocio (Venta >= Compra)
            val precioCompraDbl = currentState.precioCompra.toDoubleOrNull()
            if (precioCompraDbl == null || precioCompraDbl < 0.0) {
                preComError = "El precio de compra debe ser mayor o igual a 0"
                tieneError = true
            }

            val precioVentaDbl = currentState.precioVenta.toDoubleOrNull()
            if (precioVentaDbl == null || precioVentaDbl < 0.0) {
                preVenError = "El precio de venta debe ser mayor o igual a 0"
                tieneError = true
            } else if (precioCompraDbl != null && precioVentaDbl < precioCompraDbl) {
                preVenError = "El precio de venta ($precioVentaDbl) no puede ser inferior al precio de compra ($precioCompraDbl)"
                tieneError = true
            }

            if (tieneError) {
                _uiState.update {
                    it.copy(
                        codigoError = codError,
                        nombreError = nomError,
                        marcaError = marError,
                        categoriaError = catError,
                        cantidadError = canError,
                        stockMinimoError = stkError,
                        precioCompraError = preComError,
                        precioVentaError = preVenError
                    )
                }
                return@launch
            }

            // Actualización automática de estado según stock y umbral mínimo
            val cant = cantidadInt ?: 0
            val stkMin = stockMinimoInt ?: 0
            val estadoCalculado = when {
                cant == 0 -> "AGOTADO"
                cant <= stkMin -> "BAJO_STOCK"
                else -> "DISPONIBLE"
            }

            val entidad = ProductoEntity(
                codigo = codigoTrim,
                nombre = nombreTrim,
                marca = marcaTrim,
                categoria = categoriaTrim,
                unidadMedida = currentState.unidadMedida.ifBlank { "Unidad" },
                cantidad = cant,
                stockMinimo = stkMin,
                precioCompra = precioCompraDbl ?: 0.0,
                precioVenta = precioVentaDbl ?: 0.0,
                ubicacionZona = currentState.ubicacionZona.trim().ifBlank { "General" },
                ubicacionEstante = currentState.ubicacionEstante.trim().ifBlank { "Estante 01" },
                ubicacionRepisa = currentState.ubicacionRepisa.trim().ifBlank { "Nivel 1" },
                proveedorNombre = currentState.proveedorNombre.trim().ifBlank { "Proveedor General" },
                estado = estadoCalculado
            )

            _uiState.update { it.copy(guardando = true) }
            try {
                repository.insertOrUpdate(entidad)
                _uiState.update {
                    it.copy(
                        guardando = false,
                        guardadoExitosamente = true,
                        userMessage = if (currentState.isEditing) "Repuesto actualizado con éxito" else "Repuesto registrado con éxito"
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorMessage = "Error al persistir el repuesto: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Eliminación con confirmación
    // -------------------------------------------------------------

    fun solicitarEliminacion(producto: ProductoEntity) {
        _uiState.update {
            it.copy(
                productoAEliminar = producto,
                mostrarDialogoEliminar = true
            )
        }
    }

    fun cancelarEliminacion() {
        _uiState.update {
            it.copy(
                productoAEliminar = null,
                mostrarDialogoEliminar = false
            )
        }
    }

    fun confirmarEliminacion() {
        viewModelScope.launch {
            val producto = _uiState.value.productoAEliminar
            if (producto != null) {
                try {
                    repository.delete(producto)
                    _uiState.update {
                        it.copy(
                            productoAEliminar = null,
                            mostrarDialogoEliminar = false,
                            userMessage = "Repuesto '${producto.nombre}' eliminado correctamente"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            productoAEliminar = null,
                            mostrarDialogoEliminar = false,
                            errorMessage = "Error al eliminar repuesto: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Escaneo Nativo de Cámara (CameraX + ML Kit)
    // -------------------------------------------------------------

    fun abrirScanner(target: ScannerTarget = ScannerTarget.CODIGO_FORM) {
        _uiState.update {
            it.copy(
                mostrarScanner = true,
                scannerTarget = target
            )
        }
    }

    fun cerrarScanner() {
        _uiState.update { it.copy(mostrarScanner = false) }
    }

    fun onCodigoEscaneado(codigo: String) {
        val target = _uiState.value.scannerTarget
        _uiState.update { it.copy(mostrarScanner = false) }

        when (target) {
            ScannerTarget.BUSQUEDA -> {
                onSearchQueryChange(codigo)
            }
            ScannerTarget.CODIGO_FORM -> {
                onCodigoChange(codigo)
            }
        }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitosamente = false) }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(errorMessage = null, userMessage = null) }
    }
}

/**
 * Fábrica para instanciar ProductoViewModel con su repositorio dependiente.
 */
class ProductoViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            return ProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
    }
}
