package com.taller.inventario

import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.dto.NhtsaModelItem
import com.taller.inventario.data.remote.dto.NhtsaVinItem
import com.taller.inventario.data.repository.ProductoRepository
import com.taller.inventario.ui.producto.ProductoViewModel
import com.taller.inventario.ui.producto.ScannerTarget
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private val repository: ProductoRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ProductoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllProductos() } returns flowOf(emptyList())
        viewModel = ProductoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCodigoChange_updatesState() {
        viewModel.onCodigoChange("REP-999")
        assertEquals("REP-999", viewModel.uiState.value.codigo)
    }

    @Test
    fun onNombreChange_updatesState() {
        viewModel.onNombreChange("Aceite Sintético 5W30")
        assertEquals("Aceite Sintético 5W30", viewModel.uiState.value.nombre)
    }

    @Test
    fun abrirYCerrarScanner_togglesState() {
        viewModel.abrirScanner(ScannerTarget.VIN_SCANNER)
        assertTrue(viewModel.uiState.value.mostrarScanner)
        assertEquals(ScannerTarget.VIN_SCANNER, viewModel.uiState.value.scannerTarget)

        viewModel.cerrarScanner()
        assertFalse(viewModel.uiState.value.mostrarScanner)
    }

    @Test
    fun onCodigoEscaneado_vinTarget_updatesVinQuery() {
        viewModel.abrirScanner(ScannerTarget.VIN_SCANNER)
        viewModel.onCodigoEscaneado("1HGBH41JXMN109186")

        assertFalse(viewModel.uiState.value.mostrarScanner)
        assertEquals("1HGBH41JXMN109186", viewModel.uiState.value.vinQuery)
    }

    @Test
    fun decodificarVin_success_updatesVinResult() = runTest {
        val sampleItem = NhtsaVinItem(
            vin = "1HGBH41JXMN109186",
            make = "HONDA",
            model = "CIVIC",
            modelYear = "2021",
            engineHp = "158",
            engineCylinders = "4",
            displacementL = "2.0",
            fuelTypePrimary = "Gasoline",
            driveType = "FWD",
            plantCountry = "USA",
            vehicleType = "PASSENGER CAR",
            errorCode = "0",
            errorText = null
        )
        coEvery { repository.decodificarVin("1HGBH41JXMN109186") } returns Result.success(sampleItem)

        viewModel.onVinQueryChange("1HGBH41JXMN109186")
        viewModel.decodificarVin()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.vinResult)
        assertEquals("HONDA", viewModel.uiState.value.vinResult?.make)
        assertEquals("CIVIC", viewModel.uiState.value.vinResult?.model)
    }

    @Test
    fun buscarModelosPorMarca_success_updatesModelos() = runTest {
        val models = listOf(
            NhtsaModelItem(makeId = 1, makeName = "Ford", modelId = 10, modelName = "F-150")
        )
        coEvery { repository.obtenerModelosPorMarca("Ford") } returns Result.success(models)

        viewModel.onMarcaConsultaChange("Ford")
        viewModel.buscarModelosPorMarca()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.modelosConsulta.size)
        assertEquals("F-150", viewModel.uiState.value.modelosConsulta[0].modelName)
    }

    @Test
    fun sincronizarConBackend_updatesSyncMessage() = runTest {
        coEvery { repository.sincronizarConBackend() } returns Result.success(5)

        viewModel.sincronizarConBackend()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSyncing)
        assertTrue(viewModel.uiState.value.syncMessage?.contains("5") == true)
    }
}
