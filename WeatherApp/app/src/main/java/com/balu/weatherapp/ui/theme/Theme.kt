package com.balu.weatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val LightColors = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    secondary = Color(0xFF03A9F4),
    tertiary = Color(0xFF00BCD4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    secondary = Color(0xFF81D4FA)
)

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Uses default Material 3 Typography
        content = content
    )
}