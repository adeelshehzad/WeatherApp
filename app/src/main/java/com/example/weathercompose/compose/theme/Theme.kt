package com.example.weathercompose.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme = darkColorScheme(
    primary = Color(primaryDark),
    onPrimary = Color(onPrimaryDark),
    primaryContainer = Color(primaryContainerDark),
    secondary = Color(secondaryDark),
    onSecondary = Color(onSecondaryDark),
    background = Color(backgroundDark),
    surface = Color(surfaceDark),
    onSurface = Color(onSurfaceDark),
    outline = Color(outlineDark),
    tertiary = Color(tertiaryDark)
)


val LightColorScheme = lightColorScheme(
    primary = Color(primaryLight),
    onPrimary = Color(onPrimaryLight),
    primaryContainer = Color(primaryContainerLight),
    secondary = Color(secondaryLight),
    onSecondary = Color(onSecondaryLight),
    background = Color(backgroundLight),
    surface = Color(surfaceLight),
    onSurface = Color(onSurfaceLight),
    outline = Color(outlineLight),
    tertiary = Color(tertiaryLight)
)

@Composable
fun WeatherComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}