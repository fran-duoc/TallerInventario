package com.taller.inventario.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.taller.inventario.ui.producto.ListaProductosScreen
import com.taller.inventario.ui.producto.ProductoFormScreen
import com.taller.inventario.ui.producto.ProductoViewModel

/**
 * Orquestación central de navegación Compose con animaciones visuales fluidas
 * (transiciones horizontales con desvanecimiento entre pantallas de la aplicación).
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: ProductoViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ListaProductos.route,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 350)
            ) + fadeIn(animationSpec = tween(durationMillis = 350))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 350)
            ) + fadeOut(animationSpec = tween(durationMillis = 350))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 350)
            ) + fadeIn(animationSpec = tween(durationMillis = 350))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 350)
            ) + fadeOut(animationSpec = tween(durationMillis = 350))
        }
    ) {
        composable(route = Screen.ListaProductos.route) {
            ListaProductosScreen(
                viewModel = viewModel,
                onNavigateToForm = { codigo ->
                    navController.navigate(Screen.ProductoForm.createRoute(codigo))
                }
            )
        }

        composable(
            route = Screen.ProductoForm.route,
            arguments = listOf(
                navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo")

            LaunchedEffect(codigo) {
                if (!codigo.isNullOrBlank()) {
                    viewModel.cargarProductoParaEdicion(codigo)
                } else {
                    viewModel.iniciarCreacion()
                }
            }

            ProductoFormScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
