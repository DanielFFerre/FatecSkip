package com.example.fatecskip.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SonecaGamerColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = TextPrimary,
    primaryContainer = AccentPurpleContainer,
    onPrimaryContainer = TextPrimary,
    secondary = SafeGreen,
    onSecondary = GamerBackground,
    secondaryContainer = GamerSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = WarningYellow,
    onTertiary = GamerBackground,
    background = GamerBackground,
    onBackground = TextPrimary,
    surface = GamerSurface,
    onSurface = TextPrimary,
    surfaceVariant = GamerCardBorder,
    onSurfaceVariant = TextSecondary,
    error = PanicRed,
    onError = TextPrimary
)

@Composable
fun FatecSkipTheme(
    darkTheme: Boolean = true, // Soneca Gamer é Dark Mode nativo
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SonecaGamerColorScheme,
        typography = Typography,
        content = content
    )
}
