package com.taller.inventario.data.remote.api

import com.taller.inventario.data.remote.dto.NhtsaModelsResponse
import com.taller.inventario.data.remote.dto.NhtsaVinResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Servicio Retrofit para la API externa de especificaciones vehiculares de la NHTSA.
 */
interface NhtsaApiService {

    @GET("vehicles/DecodeVinValues/{vin}?format=json")
    suspend fun decodeVin(
        @Path("vin") vin: String
    ): Response<NhtsaVinResponse>

    @GET("vehicles/GetModelsForMake/{make}?format=json")
    suspend fun getModelsForMake(
        @Path("make") make: String
    ): Response<NhtsaModelsResponse>
}
