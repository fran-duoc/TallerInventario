package com.taller.inventario.data.remote.api

import com.taller.inventario.data.remote.dto.ProductoNetworkDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para conectar la app Android con los endpoints CRUD de Spring Boot.
 */
interface ProductoApiService {

    @GET("api/productos")
    suspend fun getProductos(
        @Query("categoria") categoria: String? = null
    ): Response<List<ProductoNetworkDto>>

    @GET("api/productos/{codigo}")
    suspend fun getProductoByCodigo(
        @Path("codigo") codigo: String
    ): Response<ProductoNetworkDto>

    @POST("api/productos")
    suspend fun createProducto(
        @Body producto: ProductoNetworkDto
    ): Response<ProductoNetworkDto>

    @PUT("api/productos/{codigo}")
    suspend fun updateProducto(
        @Path("codigo") codigo: String,
        @Body producto: ProductoNetworkDto
    ): Response<ProductoNetworkDto>

    @DELETE("api/productos/{codigo}")
    suspend fun deleteProducto(
        @Path("codigo") codigo: String
    ): Response<Unit>

    @GET("api/productos/buscar")
    suspend fun searchProductos(
        @Query("query") query: String
    ): Response<List<ProductoNetworkDto>>

    @GET("api/productos/bajo-stock")
    suspend fun getProductosBajoStock(): Response<List<ProductoNetworkDto>>
}
