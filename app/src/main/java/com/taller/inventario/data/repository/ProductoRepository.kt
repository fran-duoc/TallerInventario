package com.taller.inventario.data.repository

import com.taller.inventario.data.local.ProductoDao
import com.taller.inventario.data.local.ProductoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositorio que desacopla la fuente de datos local (Room) del ViewModel,
 * garantizando la ejecución de I/O en el despachador Dispatchers.IO.
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getAllProductos(): Flow<List<ProductoEntity>> {
        return productoDao.getAllProductos().flowOn(ioDispatcher)
    }

    suspend fun getProductoByCodigo(codigo: String): ProductoEntity? = withContext(ioDispatcher) {
        productoDao.getProductoByCodigo(codigo)
    }

    suspend fun insertOrUpdate(producto: ProductoEntity) = withContext(ioDispatcher) {
        productoDao.insertOrUpdate(producto)
    }

    suspend fun delete(producto: ProductoEntity) = withContext(ioDispatcher) {
        productoDao.delete(producto)
    }

    fun searchProductos(query: String): Flow<List<ProductoEntity>> {
        return productoDao.searchProductos(query).flowOn(ioDispatcher)
    }

    fun getProductosBajoStock(): Flow<List<ProductoEntity>> {
        return productoDao.getProductosBajoStock().flowOn(ioDispatcher)
    }
}
