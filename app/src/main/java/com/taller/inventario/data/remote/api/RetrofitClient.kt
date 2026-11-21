package com.taller.inventario.data.remote.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit centralizado para la comunicación con:
 * 1. Backend REST Spring Boot (TallerInventario).
 * 2. API Externa de la NHTSA (Especificaciones Vehiculares y Decodificación de VIN).
 */
object RetrofitClient {

    // 10.0.2.2 es la IP del host accesible desde el emulador oficial de Android.
    // Para dispositivo físico en la misma red Wi-Fi o localhost en tests, puede cambiarse dinámicamente.
    var backendBaseUrl: String = "http://10.0.2.2:8080/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
            _productoApiService = null
        }

    private const val NHTSA_BASE_URL = "https://vpic.nhtsa.dot.gov/api/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private var _productoApiService: ProductoApiService? = null

    val productoApiService: ProductoApiService
        get() {
            if (_productoApiService == null) {
                _productoApiService = Retrofit.Builder()
                    .baseUrl(backendBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ProductoApiService::class.java)
            }
            return _productoApiService!!
        }

    val nhtsaApiService: NhtsaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NHTSA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NhtsaApiService::class.java)
    }
}
