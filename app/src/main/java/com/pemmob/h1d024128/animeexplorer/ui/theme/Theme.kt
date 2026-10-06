package com.pemmob.h1d024128.animeexplorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AnimePurple,
    onPrimary = Color.White,

    primaryContainer = AnimeLavender,
    onPrimaryContainer = AnimePurpleDark,

    secondary = AnimeGold,
    onSecondary = Color.White,

    secondaryContainer = AnimeGoldLight,
    onSecondaryContainer = TextPrimary,

    tertiary = AnimePink,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceLight,
    onSurface = TextPrimary,

    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondary,

    outline = Color(0xFF8E8496)
)

private val DarkColors = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Color.Black,

    primaryContainer = Color(0xFF3B2A5E),
    onPrimaryContainer = AnimeLavender,

    secondary = GoldDark,
    onSecondary = Color.Black,

    secondaryContainer = Color(0xFF4A3B12),
    onSecondaryContainer = AnimeGoldLight,

    tertiary = AnimePink,

    background = BackgroundDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White,

    surfaceVariant = CardDark,
    onSurfaceVariant = Color(0xFFD0C8D4),

    outline = Color(0xFF9A8FA3)
)

@Composable
fun AnimeExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
