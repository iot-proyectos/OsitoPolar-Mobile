package com.ia.ositopolar.tech.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Mapeamos los colores web a Material Design 3 (Forzamos Dark Mode)
private val OsitoPolarColorScheme = darkColorScheme(
    primary = PolarCyan,
    onPrimary = androidx.compose.ui.graphics.Color.Black, // Letras negras sobre botones celestes
    background = PolarNavy,
    onBackground = PolarTextWhite,
    surface = PolarSurface,
    onSurface = PolarTextWhite,
    error = StatusRed,
    onError = PolarTextWhite
)

@Composable
fun OsitoPolarTecnicoViewTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = OsitoPolarColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pintamos la barra de estado superior (hora/batería) del mismo color que el fondo
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}