package com.taller.inventario.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProductoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taller_inventario_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        precargarProductosIniciales(database.productoDao())
                    }
                }
            }

            private suspend fun precargarProductosIniciales(dao: ProductoDao) {
                val productosIniciales = listOf(
                    ProductoEntity(
                        codigo = "LUB-CAST-5W30",
                        nombre = "Aceite Sintético Castrol EDGE 5W-30",
                        marca = "Castrol",
                        categoria = "Aceites y Lubricantes",
                        unidadMedida = "Bidón 4L",
                        cantidad = 14,
                        stockMinimo = 5,
                        precioCompra = 24990.0,
                        precioVenta = 38990.0,
                        ubicacionZona = "Zona A (Lubricantes)",
                        ubicacionEstante = "Estante 01",
                        ubicacionRepisa = "Nivel 1 (Piso)",
                        proveedorNombre = "Lubricentro Central SpA",
                        estado = "ACTIVO"
                    ),
                    ProductoEntity(
                        codigo = "FIL-MANN-W712",
                        nombre = "Filtro de Aceite Blindado W712/22",
                        marca = "Mann Filter",
                        categoria = "Filtros",
                        unidadMedida = "Unidad",
                        cantidad = 3,
                        stockMinimo = 6, // Bajo Stock provocado intencionalmente
                        precioCompra = 4200.0,
                        precioVenta = 8500.0,
                        ubicacionZona = "Zona B (Filtros)",
                        ubicacionEstante = "Estante 02",
                        ubicacionRepisa = "Nivel 3",
                        proveedorNombre = "Repuestos Chile SpA",
                        estado = "BAJO_STOCK"
                    ),
                    ProductoEntity(
                        codigo = "FRE-BOSCH-DOT4",
                        nombre = "Líquido de Frenos DOT 4 Racing 500ml",
                        marca = "Bosch",
                        categoria = "Frenos",
                        unidadMedida = "Frasco 500ml",
                        cantidad = 8,
                        stockMinimo = 4,
                        precioCompra = 3800.0,
                        precioVenta = 6990.0,
                        ubicacionZona = "Zona C (Químicos y Frenos)",
                        ubicacionEstante = "Estante 01",
                        ubicacionRepisa = "Nivel 2",
                        proveedorNombre = "Frenos y Embragues del Sur",
                        estado = "ACTIVO"
                    ),
                    ProductoEntity(
                        codigo = "BUJ-DENSO-IK20",
                        nombre = "Bujías de Encendido Iridium Power IK20",
                        marca = "Denso",
                        categoria = "Encendido",
                        unidadMedida = "Kit 4 Unidades",
                        cantidad = 0, // Agotado provocado intencionalmente
                        stockMinimo = 3,
                        precioCompra = 18500.0,
                        precioVenta = 29900.0,
                        ubicacionZona = "Zona D (Electricidad)",
                        ubicacionEstante = "Estante 03",
                        ubicacionRepisa = "Nivel 2",
                        proveedorNombre = "Importadora Automotriz Santiago",
                        estado = "AGOTADO"
                    ),
                    ProductoEntity(
                        codigo = "FRE-BREM-P060",
                        nombre = "Pastillas de Freno Cerámicas Delanteras P060",
                        marca = "Brembo",
                        categoria = "Frenos",
                        unidadMedida = "Juego",
                        cantidad = 5,
                        stockMinimo = 2,
                        precioCompra = 32000.0,
                        precioVenta = 49990.0,
                        ubicacionZona = "Zona C (Químicos y Frenos)",
                        ubicacionEstante = "Estante 02",
                        ubicacionRepisa = "Nivel 1",
                        proveedorNombre = "Frenos y Embragues del Sur",
                        estado = "ACTIVO"
                    )
                )

                for (producto in productosIniciales) {
                    dao.insertOrUpdate(producto)
                }
            }
        }
    }
}
