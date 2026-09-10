package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DolbyColorScheme = darkColorScheme(
    primary = DolbyCyanPrimary,
    onPrimary = Color.Black,
    primaryContainer = DolbyBlueSecondary,
    onPrimaryContainer = Color.White,
    secondary = DolbyAmberAccent,
    onSecondary = Color.Black,
    secondaryContainer = DolbyDarkSurfaceElevated,
    onSecondaryContainer = DolbyTextPrimary,
    tertiary = DolbyElectricBlue,
    onTertiary = Color.White,
    background = DolbyDarkBackground,
    onBackground = DolbyTextPrimary,
    surface = DolbyDarkSurface,
    onSurface = DolbyTextPrimary,
    surfaceVariant = DolbyDarkSurfaceVariant,
    onSurfaceVariant = DolbyTextSecondary,
    outline = DolbyDarkCardBorder,
    outlineVariant = DolbyDarkSurfaceElevated
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DolbyColorScheme,
        typography = Typography,
        content = content
    )
}

