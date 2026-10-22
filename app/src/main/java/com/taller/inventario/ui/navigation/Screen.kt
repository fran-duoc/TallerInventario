package com.taller.inventario.ui.navigation

sealed class Screen(val route: String) {
    data object ListaProductos : Screen("lista_productos")
    
    data object ProductoForm : Screen("producto_form?codigo={codigo}") {
        fun createRoute(codigo: String? = null): String {
            return if (!codigo.isNullOrBlank()) {
                "producto_form?codigo=$codigo"
            } else {
                "producto_form"
            }
        }
    }
}
