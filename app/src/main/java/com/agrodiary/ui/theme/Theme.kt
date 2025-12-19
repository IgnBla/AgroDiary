package com.agrodiary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = OnPrimaryLight,
    primaryContainer = Green80,
    onPrimaryContainer = Green20,
    secondary = Brown40,
    onSecondary = OnSecondaryLight,
    secondaryContainer = Brown80,
    onSecondaryContainer = Brown20,
    tertiary = Gold40,
    onTertiary = OnPrimaryLight,
    tertiaryContainer = Gold80,
    onTertiaryContainer = Gold20,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = OnSurfaceLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorLight.copy(alpha = 0.1f),
    onErrorContainer = ErrorLight,
    outline = DividerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = OnPrimaryDark,
    primaryContainer = Green40,
    onPrimaryContainer = Green80,
    secondary = Brown80,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Brown40,
    onSecondaryContainer = Brown80,
    tertiary = Gold80,
    onTertiary = OnPrimaryDark,
    tertiaryContainer = Gold40,
    onTertiaryContainer = Gold80,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorDark.copy(alpha = 0.1f),
    onErrorContainer = ErrorDark,
    outline = DividerDark
)

@Composable
fun AgroDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
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
