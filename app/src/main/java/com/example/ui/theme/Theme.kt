package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TrisaktiEmerald,
    onPrimary = TrisaktiBackground,
    primaryContainer = TrisaktiEmeraldDark,
    onPrimaryContainer = TrisaktiEmeraldLight,

    secondary = TrisaktiGold,
    onSecondary = TrisaktiBackground,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = TrisaktiGoldLight,

    tertiary = TrisaktiSky,
    onTertiary = TrisaktiBackground,

    background = TrisaktiBackground,
    onBackground = TrisaktiTextPrimary,

    surface = TrisaktiSurface,
    onSurface = TrisaktiTextPrimary,
    surfaceVariant = TrisaktiCardBg,
    onSurfaceVariant = TrisaktiTextSecondary,

    error = TrisaktiCoral,
    onError = TrisaktiBackground,
    outline = TrisaktiCardBorder
)

@Composable
fun TrisaktiTradersTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) = TrisaktiTradersTheme(content = content)

