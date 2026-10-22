package com.taller.inventario.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = RedOnPrimary,
    primaryContainer = RedPrimaryContainer,
    onPrimaryContainer = RedOnPrimaryContainer,
    secondary = CharcoalSecondary,
    onSecondary = CharcoalOnSecondary,
    secondaryContainer = CharcoalSecondaryContainer,
    onSecondaryContainer = CharcoalOnSecondaryContainer,
    tertiary = SteelTertiary,
    onTertiary = SteelOnTertiary,
    tertiaryContainer = SteelTertiaryContainer,
    onTertiaryContainer = SteelOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,
    onPrimary = RedOnPrimary,
    primaryContainer = RedOnPrimaryContainer,
    onPrimaryContainer = RedPrimaryContainer,
    secondary = CharcoalSecondaryContainer,
    onSecondary = CharcoalOnSecondaryContainer,
    secondaryContainer = CharcoalSecondary,
    onSecondaryContainer = CharcoalOnSecondary,
    tertiary = SteelTertiaryContainer,
    onTertiary = SteelOnTertiaryContainer,
    tertiaryContainer = SteelTertiary,
    onTertiaryContainer = SteelOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun TallerInventarioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
