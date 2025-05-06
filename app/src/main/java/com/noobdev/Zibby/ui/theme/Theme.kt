package com.noobdev.Zibby.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// Enum for available color schemes
enum class AppTheme {
    SYSTEM, // Uses system light/dark
    CYBERPUNK,
    SUNSET,
    PASTEL,
    MIDNIGHT_GARDEN,
    FOREST,
    GALAXY,
    RETRO_WAVE,
    OCEAN,
    AUTUMN
}

// Local composition for current theme selection
val LocalAppTheme = staticCompositionLocalOf { AppTheme.SYSTEM }

@Composable
fun ZibbyTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine color scheme based on selected theme
    val colorScheme = when (appTheme) {
        AppTheme.SYSTEM -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
        AppTheme.CYBERPUNK -> darkColorScheme(
            primary = CyberpunkPalette.primary,
            secondary = CyberpunkPalette.secondary,
            tertiary = CyberpunkPalette.tertiary,
            background = CyberpunkPalette.background,
            surface = CyberpunkPalette.surface,
            onPrimary = CyberpunkPalette.onPrimary,
            onSecondary = CyberpunkPalette.onSecondary,
            onTertiary = CyberpunkPalette.onTertiary,
            onBackground = CyberpunkPalette.onBackground,
            onSurface = CyberpunkPalette.onSurface
        )
        AppTheme.SUNSET -> darkColorScheme(
            primary = SunsetPalette.primary,
            secondary = SunsetPalette.secondary,
            tertiary = SunsetPalette.tertiary,
            background = SunsetPalette.background,
            surface = SunsetPalette.surface,
            onPrimary = SunsetPalette.onPrimary,
            onSecondary = SunsetPalette.onSecondary,
            onTertiary = SunsetPalette.onTertiary,
            onBackground = SunsetPalette.onBackground,
            onSurface = SunsetPalette.onSurface
        )
        AppTheme.PASTEL -> lightColorScheme(
            primary = PastelPalette.primary,
            secondary = PastelPalette.secondary,
            tertiary = PastelPalette.tertiary,
            background = PastelPalette.background,
            surface = PastelPalette.surface,
            onPrimary = PastelPalette.onPrimary,
            onSecondary = PastelPalette.onSecondary,
            onTertiary = PastelPalette.onTertiary,
            onBackground = PastelPalette.onBackground,
            onSurface = PastelPalette.onSurface
        )
        AppTheme.MIDNIGHT_GARDEN -> darkColorScheme(
            primary = MidnightGardenPalette.primary,
            secondary = MidnightGardenPalette.secondary,
            tertiary = MidnightGardenPalette.tertiary,
            background = MidnightGardenPalette.background,
            surface = MidnightGardenPalette.surface,
            onPrimary = MidnightGardenPalette.onPrimary,
            onSecondary = MidnightGardenPalette.onSecondary,
            onTertiary = MidnightGardenPalette.onTertiary,
            onBackground = MidnightGardenPalette.onBackground,
            onSurface = MidnightGardenPalette.onSurface
        )
        AppTheme.FOREST -> lightColorScheme(
            primary = ForestPalette.primary,
            secondary = ForestPalette.secondary,
            tertiary = ForestPalette.tertiary,
            background = ForestPalette.background,
            surface = ForestPalette.surface,
            onPrimary = ForestPalette.onPrimary,
            onSecondary = ForestPalette.onSecondary,
            onTertiary = ForestPalette.onTertiary,
            onBackground = ForestPalette.onBackground,
            onSurface = ForestPalette.onSurface
        )
        AppTheme.GALAXY -> darkColorScheme(
            primary = GalaxyPalette.primary,
            secondary = GalaxyPalette.secondary,
            tertiary = GalaxyPalette.tertiary,
            background = GalaxyPalette.background,
            surface = GalaxyPalette.surface,
            onPrimary = GalaxyPalette.onPrimary,
            onSecondary = GalaxyPalette.onSecondary,
            onTertiary = GalaxyPalette.onTertiary,
            onBackground = GalaxyPalette.onBackground,
            onSurface = GalaxyPalette.onSurface
        )
        AppTheme.RETRO_WAVE -> darkColorScheme(
            primary = RetroWavePalette.primary,
            secondary = RetroWavePalette.secondary,
            tertiary = RetroWavePalette.tertiary,
            background = RetroWavePalette.background,
            surface = RetroWavePalette.surface,
            onPrimary = RetroWavePalette.onPrimary,
            onSecondary = RetroWavePalette.onSecondary,
            onTertiary = RetroWavePalette.onTertiary,
            onBackground = RetroWavePalette.onBackground,
            onSurface = RetroWavePalette.onSurface
        )
        AppTheme.OCEAN -> lightColorScheme(
            primary = OceanPalette.primary,
            secondary = OceanPalette.secondary,
            tertiary = OceanPalette.tertiary,
            background = OceanPalette.background,
            surface = OceanPalette.surface,
            onPrimary = OceanPalette.onPrimary,
            onSecondary = OceanPalette.onSecondary,
            onTertiary = OceanPalette.onTertiary,
            onBackground = OceanPalette.onBackground,
            onSurface = OceanPalette.onSurface
        )
        AppTheme.AUTUMN -> lightColorScheme(
            primary = AutumnPalette.primary,
            secondary = AutumnPalette.secondary,
            tertiary = AutumnPalette.tertiary,
            background = AutumnPalette.background,
            surface = AutumnPalette.surface,
            onPrimary = AutumnPalette.onPrimary,
            onSecondary = AutumnPalette.onSecondary,
            onTertiary = AutumnPalette.onTertiary,
            onBackground = AutumnPalette.onBackground,
            onSurface = AutumnPalette.onSurface
        )
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}