package com.example.riji.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A1A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8E8E3),
    onPrimaryContainer = Color(0xFF1A1A1A),
    secondary = BlueAccent,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8F0FF),
    onSecondaryContainer = Color(0xFF1A1A1A),
    tertiary = GreenAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8FFE8),
    onTertiaryContainer = Color(0xFF1A1A1A),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightCardBackground,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnCard,
    outline = LightDivider,
    error = ErrorColor,
    onError = Color(0xFFFFFFFF)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = BlueAccent,
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF1A2A3A),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = GreenAccent,
    onTertiary = Color(0xFF1A1A1A),
    tertiaryContainer = Color(0xFF1A2A1A),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkCardBackground,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnCard,
    outline = DarkDivider,
    error = ErrorColor,
    onError = Color(0xFFFFFFFF)
)

@Composable
fun RijiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = colorScheme.background.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
