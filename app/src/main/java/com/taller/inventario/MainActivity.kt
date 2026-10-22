package com.taller.inventario

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.taller.inventario.ui.navigation.AppNavigation
import com.taller.inventario.ui.producto.ProductoViewModel
import com.taller.inventario.ui.producto.ProductoViewModelFactory
import com.taller.inventario.ui.theme.TallerInventarioTheme
import com.taller.inventario.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: ProductoViewModel by viewModels {
        val app = application as InventoryApplication
        ProductoViewModelFactory(app.repository)
    }

    // Solicitud en runtime para el recurso nativo de notificaciones del sistema
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Callback de respuesta del usuario
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del canal para el recurso nativo de alertas de inventario
        NotificationHelper.crearCanalNotificaciones(this)

        // Gestión segura de permisos para notificaciones (Android 13+ / API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            TallerInventarioTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
