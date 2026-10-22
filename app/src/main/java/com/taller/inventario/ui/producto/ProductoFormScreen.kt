package com.taller.inventario.ui.producto

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.inventario.ui.producto.components.QrScannerDialog
import com.taller.inventario.util.NotificationHelper

private val CATEGORIAS_DISPONIBLES = listOf(
    "Aceites y Lubricantes",
    "Filtros",
    "Frenos",
    "Encendido",
    "Suspensión y Dirección",
    "Transmisión",
    "Refrigeración",
    "Accesorios e Iluminación"
)

private val UNIDADES_MEDIDA = listOf(
    "Unidad",
    "Bidón 4L",
    "Bidón 5L",
    "Litros",
    "Kit",
    "Juego",
    "Frasco 500ml",
    "Caja"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: ProductoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var expCategoria by remember { mutableStateOf(false) }
    var expUnidad by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditing) "Editar Repuesto" else "Nuevo Repuesto",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // -------------------------------------------------------------
            // SECCIÓN 1: Identificación del Repuesto
            // -------------------------------------------------------------
            SectionCard(
                title = "1. Identificación del Repuesto",
                icon = Icons.AutoMirrored.Filled.Label
            ) {
                // Campo Código con Botón de Cámara Integrado (Recurso Nativo 1: CameraX + ML Kit)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.codigo,
                        onValueChange = { viewModel.onCodigoChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Código de Repuesto *") },
                        placeholder = { Text("Ej: LUB-CAST-5W30") },
                        enabled = !state.isEditing,
                        isError = state.codigoError != null,
                        supportingText = {
                            state.codigoError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (state.codigoError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true
                    )

                    if (!state.isEditing) {
                        IconButton(
                            onClick = { viewModel.abrirScanner(ScannerTarget.CODIGO_FORM) },
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(50.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Escanear Código con Cámara",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Nombre con ícono y feedback visual
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre del Producto / Repuesto *") },
                    placeholder = { Text("Ej: Aceite Sintético Castrol EDGE 5W-30") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (state.nombreError != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = state.nombreError != null,
                    supportingText = {
                        state.nombreError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true
                )

                // Marca con ícono temático automotriz
                OutlinedTextField(
                    value = state.marca,
                    onValueChange = { viewModel.onMarcaChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Marca Automotriz *") },
                    placeholder = { Text("Ej: Castrol, Bosch, Mann Filter, Brembo") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    trailingIcon = {
                        if (state.marcaError != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = state.marcaError != null,
                    supportingText = {
                        state.marcaError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    singleLine = true
                )

                // Categoría (Selector Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expCategoria,
                    onExpandedChange = { expCategoria = !expCategoria }
                ) {
                    OutlinedTextField(
                        value = state.categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expCategoria) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = state.categoriaError != null,
                        supportingText = {
                            state.categoriaError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expCategoria,
                        onDismissRequest = { expCategoria = false }
                    ) {
                        CATEGORIAS_DISPONIBLES.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.onCategoriaChange(cat)
                                    expCategoria = false
                                }
                            )
                        }
                    }
                }

                // Unidad de Medida (Selector Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expUnidad,
                    onExpandedChange = { expUnidad = !expUnidad }
                ) {
                    OutlinedTextField(
                        value = state.unidadMedida,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad de Medida *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expUnidad) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Straighten,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expUnidad,
                        onDismissRequest = { expUnidad = false }
                    ) {
                        UNIDADES_MEDIDA.forEach { unidad ->
                            DropdownMenuItem(
                                text = { Text(unidad) },
                                onClick = {
                                    viewModel.onUnidadMedidaChange(unidad)
                                    expUnidad = false
                                }
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECCIÓN 2: Inventario, Stock y Precios Referenciales
            // -------------------------------------------------------------
            SectionCard(
                title = "2. Inventario, Stock y Precios",
                icon = Icons.Default.Inventory
            ) {
                // Stock Actual y Mínimo en dos columnas con íconos dedicados
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.cantidad,
                        onValueChange = { viewModel.onCantidadChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Cantidad Actual *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (state.cantidadError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.cantidadError != null,
                        supportingText = {
                            state.cantidadError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.stockMinimo,
                        onValueChange = { viewModel.onStockMinimoChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Stock Mínimo *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        trailingIcon = {
                            if (state.stockMinimoError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.stockMinimoError != null,
                        supportingText = {
                            state.stockMinimoError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true
                    )
                }

                // Cálculo Reactivo con Animación de la Regla de Reposición Sugerida
                val cantInt = state.cantidad.toIntOrNull() ?: 0
                val stkMinInt = state.stockMinimo.toIntOrNull() ?: 0
                val reposicionSugerida = maxOf(0, (stkMinInt * 2) - cantInt)

                AnimatedVisibility(
                    visible = reposicionSugerida > 0,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Reposición Mínima Sugerida: Pedir $reposicionSugerida ${state.unidadMedida} (Stock insuficiente)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Precios en dos columnas (Compra y Venta)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.precioCompra,
                        onValueChange = { viewModel.onPrecioCompraChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Precio Compra ($) *") },
                        placeholder = { Text("0") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        },
                        trailingIcon = {
                            if (state.precioCompraError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.precioCompraError != null,
                        supportingText = {
                            state.precioCompraError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.precioVenta,
                        onValueChange = { viewModel.onPrecioVentaChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Precio Venta ($) *") },
                        placeholder = { Text("0") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (state.precioVentaError != null) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.precioVentaError != null,
                        supportingText = {
                            state.precioVentaError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true
                    )
                }
            }

            // -------------------------------------------------------------
            // SECCIÓN 3: Ubicación en Bodega y Proveedor Asignado
            // -------------------------------------------------------------
            SectionCard(
                title = "3. Ubicación Física y Proveedor",
                icon = Icons.Default.Place
            ) {
                OutlinedTextField(
                    value = state.ubicacionZona,
                    onValueChange = { viewModel.onUbicacionZonaChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Sector / Zona de Bodega") },
                    placeholder = { Text("Ej: Zona A (Lubricantes y Fluidos)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.ubicacionEstante,
                        onValueChange = { viewModel.onUbicacionEstanteChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Estante") },
                        placeholder = { Text("Ej: Estante 02") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.ubicacionRepisa,
                        onValueChange = { viewModel.onUbicacionRepisaChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Repisa / Nivel") },
                        placeholder = { Text("Ej: Nivel 3 - Posición 1") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.proveedorNombre,
                        onValueChange = { viewModel.onProveedorNombreChange(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Proveedor Principal Asignado") },
                        placeholder = { Text("Ej: Repuestos Chile SpA, Lubricentro Central") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        singleLine = true
                    )

                    // Botón para llamada telefónica nativa al proveedor (Recurso Nativo 3)
                    IconButton(
                        onClick = {
                            try {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+56987654321")
                                }
                                context.startActivity(dialIntent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(50.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Llamar Proveedor",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // Botones de Acción (Cancelar y Guardar)
            // -------------------------------------------------------------
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val cantVal = state.cantidad.toIntOrNull() ?: 0
                        val stkVal = state.stockMinimo.toIntOrNull() ?: 0
                        val sugVal = maxOf(0, (stkVal * 2) - cantVal)
                        val nomVal = state.nombre
                        val codVal = state.codigo

                        viewModel.validarYGuardar(onSuccess = {
                            // Si el repuesto guardado se encuentra en estado crítico o agotado,
                            // emitir la notificación nativa del sistema
                            if (cantVal <= stkVal) {
                                NotificationHelper.emitirAlertaStock(
                                    context = context,
                                    nombreProducto = nomVal,
                                    codigo = codVal,
                                    cantidadActual = cantVal,
                                    reposicionSugerida = sugVal
                                )
                            }
                            onNavigateBack()
                        })
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !state.guardando,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (state.guardando) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isEditing) "Actualizar" else "Guardar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Modal de Escaneo Nativo (CameraX + ML Kit) cuando se solicita desde el formulario
    if (state.mostrarScanner) {
        QrScannerDialog(
            onCodigoDetectado = { codigo ->
                viewModel.onCodigoEscaneado(codigo)
            },
            onDismiss = {
                viewModel.cerrarScanner()
            }
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}
