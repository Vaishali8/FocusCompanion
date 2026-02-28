package com.saathi.focuscompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SaathiColorScheme = lightColorScheme(
    primary = ChaiOrange,
    onPrimary = Color.White,
    primaryContainer = ChaiOrange.copy(alpha = 0.15f),
    onPrimaryContainer = WarmBrown,
    secondary = WarmBrown,
    onSecondary = CreamWhite,
    background = CreamWhite,
    onBackground = WarmBrown,
    surface = CreamWhite,
    onSurface = WarmBrown,
    surfaceVariant = CreamWhite,
    onSurfaceVariant = WarmBrown,
    outline = ChaiOrange.copy(alpha = 0.5f),
    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun SaathiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SaathiColorScheme,
        typography = SaathiTypography,
        content = content
    )
}
