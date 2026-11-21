package com.taller.inventario.data.repository

import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import com.taller.inventario.data.remote.api.NhtsaApiService
import com.taller.inventario.data.remote.api.ProductoApiService
import com.taller.inventario.data.remote.api.RetrofitClient
import com.taller.inventario.data.remote.dto.NhtsaModelItem
import com.taller.inventario.data.remote.dto.NhtsaVinItem
import com.taller.inventario.data.remote.dto.ProductoNetworkDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositorio unificado que orquesta:
 * 1. Persistencia local reactiva con Room (offline-first).
 * 2. Sincronización remota CRUD con el backend REST de Spring Boot mediante Retrofit.
 * 3. Consultas técnicas vehiculares a la API externa de la NHTSA.
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val apiService: ProductoApiService = RetrofitClient.productoApiService,
    private val nhtsaApiService: NhtsaApiService = RetrofitClient.nhtsaApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // -------------------------------------------------------------
    // Consultas Reactivas de Base de Datos Local (Room)
    // -------------------------------------------------------------

    fun getAllProductos(): Flow<List<ProductoEntity>> {
        return productoDao.getAllProductos().flowOn(ioDispatcher)
    }

    suspend fun getProductoByCodigo(codigo: String): ProductoEntity? = withContext(ioDispatcher) {
        productoDao.getProductoByCodigo(codigo)
    }

    fun searchProductos(query: String): Flow<List<ProductoEntity>> {
        return productoDao.searchProductos(query).flowOn(ioDispatcher)
    }

    fun getProductosBajoStock(): Flow<List<ProductoEntity>> {
        return productoDao.getProductosBajoStock().flowOn(ioDispatcher)
    }

    // -------------------------------------------------------------
    // Operaciones CRUD Sincronizadas (Room + Spring Boot Retrofit)
    // -------------------------------------------------------------

    suspend fun insertOrUpdate(producto: ProductoEntity): Result<Unit> = withContext(ioDispatcher) {
        try {
            // Persistir localmente en Room primero
            productoDao.insertOrUpdate(producto)

            // Intentar replicar en backend Spring Boot
            val dto = ProductoNetworkDto.fromEntity(producto)
            val respuestaVerificacion = apiService.getProductoByCodigo(producto.codigo)
            if (respuestaVerificacion.isSuccessful) {
                apiService.updateProducto(producto.codigo, dto)
            } else {
                apiService.createProducto(dto)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Se garantiza persistencia local aunque el backend remoto esté temporalmente inaccesible
            Result.success(Unit)
        }
    }

    suspend fun delete(producto: ProductoEntity): Result<Unit> = withContext(ioDispatcher) {
        try {
            productoDao.delete(producto)
            apiService.deleteProducto(producto.codigo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    /**
     * Sincroniza el catálogo descargando la versión más reciente del backend Spring Boot.
     */
    suspend fun sincronizarConBackend(): Result<Int> = withContext(ioDispatcher) {
        try {
            val response = apiService.getProductos()
            if (response.isSuccessful && response.body() != null) {
                val remotos = response.body()!!
                remotos.forEach { dto ->
                    productoDao.insertOrUpdate(dto.toEntity())
                }
                Result.success(remotos.size)
            } else {
                Result.failure(Exception("Error HTTP del backend: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // Integración API Externa (NHTSA Vehicle API)
    // -------------------------------------------------------------

    suspend fun decodificarVin(vin: String): Result<NhtsaVinItem> = withContext(ioDispatcher) {
        try {
            val response = nhtsaApiService.decodeVin(vin.trim().uppercase())
            if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                val item = response.body()!!.results!!.first()
                Result.success(item)
            } else {
                Result.failure(Exception("No se encontraron datos para el VIN especificado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerModelosPorMarca(marca: String): Result<List<NhtsaModelItem>> = withContext(ioDispatcher) {
        try {
            val response = nhtsaApiService.getModelsForMake(marca.trim())
            if (response.isSuccessful && response.body()?.results != null) {
                Result.success(response.body()!!.results!!)
            } else {
                Result.failure(Exception("Error al consultar modelos de $marca"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
