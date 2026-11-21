package com.taller.inventario

import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.api.NhtsaApiService
import com.taller.inventario.data.remote.api.ProductoApiService
import com.taller.inventario.data.remote.dto.NhtsaModelItem
import com.taller.inventario.data.remote.dto.NhtsaModelsResponse
import com.taller.inventario.data.remote.dto.NhtsaVinItem
import com.taller.inventario.data.remote.dto.NhtsaVinResponse
import com.taller.inventario.data.remote.dto.ProductoNetworkDto
import com.taller.inventario.data.repository.ProductoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoRepositoryTest {

    private val productoDao: ProductoDao = mockk(relaxed = true)
    private val apiService: ProductoApiService = mockk(relaxed = true)
    private val nhtsaApiService: NhtsaApiService = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: ProductoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = ProductoRepository(
            productoDao = productoDao,
            apiService = apiService,
            nhtsaApiService = nhtsaApiService,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAllProductos_emitsFromDao() = runTest {
        val dummyList = listOf(
            ProductoEntity(
                codigo = "REP-001",
                nombre = "Filtro Aceite",
                marca = "Bosch",
                categoria = "Filtros",
                unidadMedida = "Unidad",
                cantidad = 10,
                stockMinimo = 5,
                precioCompra = 5000.0,
                precioVenta = 8000.0,
                ubicacionZona = "A",
                ubicacionEstante = "1",
                ubicacionRepisa = "1",
                proveedorNombre = "Proveedor 1",
                estado = "ACTIVO"
            )
        )
        coEvery { productoDao.getAllProductos() } returns flowOf(dummyList)

        val flow = repository.getAllProductos()
        val result = flow.first()

        assertEquals(1, result.size)
        assertEquals("REP-001", result[0].codigo)
    }

    @Test
    fun getProductoByCodigo_returnsEntity() = runTest {
        val entity = ProductoEntity(
            codigo = "REP-002",
            nombre = "Pastillas de Freno",
            marca = "Brembo",
            categoria = "Frenos",
            unidadMedida = "Juego",
            cantidad = 4,
            stockMinimo = 2,
            precioCompra = 25000.0,
            precioVenta = 35000.0,
            ubicacionZona = "B",
            ubicacionEstante = "2",
            ubicacionRepisa = "1",
            proveedorNombre = "Frenos Chile",
            estado = "ACTIVO"
        )
        coEvery { productoDao.getProductoByCodigo("REP-002") } returns entity

        val result = repository.getProductoByCodigo("REP-002")

        assertNotNull(result)
        assertEquals("Pastillas de Freno", result?.nombre)
    }

    @Test
    fun insertOrUpdate_persistsInDaoAndAttemptsApiCall() = runTest {
        val entity = ProductoEntity(
            codigo = "REP-003",
            nombre = "Bujía Platino",
            marca = "NGK",
            categoria = "Encendido",
            unidadMedida = "Unidad",
            cantidad = 20,
            stockMinimo = 10,
            precioCompra = 3000.0,
            precioVenta = 6000.0,
            ubicacionZona = "C",
            ubicacionEstante = "1",
            ubicacionRepisa = "2",
            proveedorNombre = "Repuestos Central",
            estado = "ACTIVO"
        )
        coEvery { productoDao.insertOrUpdate(entity) } returns Unit
        coEvery { apiService.getProductoByCodigo(entity.codigo) } returns Response.success(null)
        coEvery { apiService.createProducto(any()) } returns Response.success(
            ProductoNetworkDto.fromEntity(entity)
        )

        val res = repository.insertOrUpdate(entity)

        assertTrue(res.isSuccess)
        coVerify(exactly = 1) { productoDao.insertOrUpdate(entity) }
    }

    @Test
    fun decodificarVin_returnsNhtsaItemOnSuccess() = runTest {
        val vinItem = NhtsaVinItem(
            vin = "1HGBH41JXMN109186",
            make = "HONDA",
            model = "CIVIC",
            modelYear = "2021",
            engineHp = "158",
            engineCylinders = "4",
            displacementL = "2.0",
            fuelTypePrimary = "Gasoline",
            driveType = "FWD",
            plantCountry = "UNITED STATES",
            vehicleType = "PASSENGER CAR",
            errorCode = "0",
            errorText = "0 - VIN decoded clean"
        )
        val vinResponse = NhtsaVinResponse(
            count = 1,
            message = "Results returned successfully",
            results = listOf(vinItem)
        )
        coEvery { nhtsaApiService.decodeVin("1HGBH41JXMN109186") } returns Response.success(vinResponse)

        val result = repository.decodificarVin("1HGBH41JXMN109186")

        assertTrue(result.isSuccess)
        assertEquals("HONDA", result.getOrNull()?.make)
        assertEquals("CIVIC", result.getOrNull()?.model)
    }

    @Test
    fun obtenerModelosPorMarca_returnsListOnSuccess() = runTest {
        val modelItem = NhtsaModelItem(
            makeId = 448,
            makeName = "TOYOTA",
            modelId = 1861,
            modelName = "Corolla"
        )
        val response = NhtsaModelsResponse(
            count = 1,
            message = "Results returned successfully",
            results = listOf(modelItem)
        )
        coEvery { nhtsaApiService.getModelsForMake("toyota") } returns Response.success(response)

        val result = repository.obtenerModelosPorMarca("toyota")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Corolla", result.getOrNull()?.get(0)?.modelName)
    }
}
