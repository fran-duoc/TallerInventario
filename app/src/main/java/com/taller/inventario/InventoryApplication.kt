package com.taller.inventario

import android.app.Application
import com.taller.inventario.data.local.AppDatabase
import com.taller.inventario.data.repository.ProductoRepository

/**
 * Clase Application para inicializar de forma perezosa y segura
 * la persistencia Room y el repositorio desacoplado.
 */
class InventoryApplication : Application() {

    val database by lazy {
        AppDatabase.getDatabase(this)
    }

    val repository by lazy {
        ProductoRepository(database.productoDao())
    }
}
