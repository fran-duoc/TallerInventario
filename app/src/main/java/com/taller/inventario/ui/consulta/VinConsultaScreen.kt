package com.taller.inventario.ui.consulta

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taller.inventario.data.remote.dto.NhtsaVinItem
import com.taller.inventario.ui.producto.ProductoViewModel
import com.taller.inventario.ui.producto.ScannerTarget
import com.taller.inventario.ui.producto.components.QrScannerDialog

/**
 * Pantalla de consulta vehicular que integra la API externa NHTSA.
 * Permite decodificar un VIN de 17 caracteres y buscar modelos por marca.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinConsultaScreen(
    viewModel: ProductoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    // Mostrar escáner si está activo
    if (state.mostrarScanner) {
        QrScannerDialog(
            onCodigoDetectado = { codigo -> viewModel.onCodigoEscaneado(codigo) },
            onDismiss = { viewModel.cerrarScanner() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "  Consulta VIN / Vehículos",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ----------------------------------------------------------------
            // Sección 1: Decodificador VIN
            // ----------------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Decodificador de VIN",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "API NHTSA – National Highway Traffic Safety Administration",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.vinQuery,
                        onValueChange = viewModel::onVinQueryChange,
                        label = { Text("VIN (17 caracteres)") },
                        placeholder = { Text("Ej: 1HGBH41JXMN109186") },
                        isError = state.vinError != null,
                        supportingText = {
                            state.vinError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                                ?: Text("${state.vinQuery.length}/17")
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.abrirScanner(ScannerTarget.VIN_SCANNER) }) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Escanear código de barras VIN"
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = viewModel::decodificarVin,
                            enabled = !state.vinLoading && state.vinQuery.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (state.vinLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null)
                                Text(" Decodificar")
                            }
                        }
                    }

                    // Resultado VIN
                    state.vinResult?.let { vin ->
                        Spacer(Modifier.height(12.dp))
                        Divider()
                        Spacer(Modifier.height(12.dp))
                        VinResultCard(vin)
                    }
                }
            }

            // ----------------------------------------------------------------
            // Sección 2: Búsqueda de modelos por marca
            // ----------------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Modelos por Marca",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Consulta los modelos disponibles de una marca de vehículo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.marcaConsulta,
                        onValueChange = viewModel::onMarcaConsultaChange,
                        label = { Text("Marca del vehículo") },
                        placeholder = { Text("Ej: Toyota, Honda, Ford") },
                        isError = state.modelosError != null,
                        supportingText = state.modelosError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = viewModel::buscarModelosPorMarca,
                        enabled = !state.modelosLoading && state.marcaConsulta.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.modelosLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Text(" Buscar modelos")
                        }
                    }

                    // Lista de modelos
                    if (state.modelosConsulta.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Divider()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${state.modelosConsulta.size} modelos encontrados para \"${state.marcaConsulta}\"",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(6.dp))
                        state.modelosConsulta.take(30).forEach { modelo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "  ${modelo.modelName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        if (state.modelosConsulta.size > 30) {
                            Text(
                                text = "... y ${state.modelosConsulta.size - 30} más",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta que muestra los datos decodificados de un VIN desde la API NHTSA.
 */
@Composable
private fun VinResultCard(vin: NhtsaVinItem) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Resultado del VIN",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Divider()
        val campos = listOfNotNull(
            vin.make?.let { "Fabricante" to it },
            vin.model?.let { "Modelo" to it },
            vin.modelYear?.let { "Año" to it },
            vin.vehicleType?.let { "Tipo de vehículo" to it },
            vin.engineHp?.let { "Potencia (HP)" to it },
            vin.engineCylinders?.let { "Cilindros" to it },
            vin.displacementL?.let { "Cilindrada (L)" to it },
            vin.fuelTypePrimary?.let { "Combustible" to it },
            vin.driveType?.let { "Tracción" to it },
            vin.plantCountry?.let { "País de fabricación" to it },
            vin.vin?.let { "VIN" to it }
        )
        if (campos.isEmpty()) {
            val error = vin.errorText ?: "No se encontraron datos para este VIN"
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            campos.forEach { (label, value) ->
                VinDataRow(label, value)
            }
        }
    }
}

@Composable
private fun VinDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.45f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.55f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
