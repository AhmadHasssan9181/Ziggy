package com.noobdev.Zibby.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Default Material 3 color schemes
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    tertiary = Color(0xFFBB86FC),
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    tertiary = Color(0xFFBB86FC),
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

// Custom theme palettes
object CyberpunkPalette {
    val primary = Color(0xFF00F0FF)      // Bright cyan
    val secondary = Color(0xFFFF0055)    // Neon pink
    val tertiary = Color(0xFFFFFF00)     // Yellow
    val background = Color(0xFF0A0E17)   // Dark blue-black
    val surface = Color(0xFF1A1F2B)      // Dark blue-gray
    val onPrimary = Color(0xFF000000)    // Black
    val onSecondary = Color(0xFF000000)  // Black
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFFECECEC) // Off-white
    val onSurface = Color(0xFFECECEC)    // Off-white
}

object SunsetPalette {
    val primary = Color(0xFFFF5722)      // Orange
    val secondary = Color(0xFFE91E63)    // Pink
    val tertiary = Color(0xFFFF9800)     // Light orange
    val background = Color(0xFF1E1215)   // Deep burgundy-black
    val surface = Color(0xFF2D1A21)      // Deep burgundy
    val onPrimary = Color(0xFF000000)    // Black
    val onSecondary = Color(0xFFFFFFFF)  // White
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFFECECEC) // Off-white
    val onSurface = Color(0xFFECECEC)    // Off-white
}

object PastelPalette {
    val primary = Color(0xFFFFC3A0)      // Soft peach
    val secondary = Color(0xFFFFAFCC)    // Soft pink
    val tertiary = Color(0xFFA0DDFF)     // Soft blue
    val background = Color(0xFFF8F9FA)   // Off-white
    val surface = Color(0xFFFFFFFF)      // White
    val onPrimary = Color(0xFF442C2E)    // Dark brown
    val onSecondary = Color(0xFF442C2E)  // Dark brown
    val onTertiary = Color(0xFF442C2E)   // Dark brown
    val onBackground = Color(0xFF442C2E) // Dark brown
    val onSurface = Color(0xFF442C2E)    // Dark brown
}

object MidnightGardenPalette {
    val primary = Color(0xFF9C27B0)      // Purple
    val secondary = Color(0xFF00BFA5)    // Teal
    val tertiary = Color(0xFF7C4DFF)     // Deep purple
    val background = Color(0xFF0A0A15)   // Very dark blue
    val surface = Color(0xFF111122)      // Dark blue
    val onPrimary = Color(0xFFFFFFFF)    // White
    val onSecondary = Color(0xFF000000)  // Black
    val onTertiary = Color(0xFFFFFFFF)   // White
    val onBackground = Color(0xFFECECEC) // Off-white
    val onSurface = Color(0xFFECECEC)    // Off-white
}

object ForestPalette {
    val primary = Color(0xFF2E7D32)      // Dark green
    val secondary = Color(0xFF795548)    // Brown
    val tertiary = Color(0xFFFFB300)     // Amber
    val background = Color(0xFFEFEFEF)   // Off-white
    val surface = Color(0xFFFFFFFF)      // White
    val onPrimary = Color(0xFFFFFFFF)    // White
    val onSecondary = Color(0xFFFFFFFF)  // White
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFF2C2C2C) // Dark gray
    val onSurface = Color(0xFF2C2C2C)    // Dark gray
}

object GalaxyPalette {
    val primary = Color(0xFF8C9EFF)      // Light blue
    val secondary = Color(0xFFB388FF)    // Light purple
    val tertiary = Color(0xFF80D8FF)     // Cyan
    val background = Color(0xFF000022)   // Very dark blue
    val surface = Color(0xFF060633)      // Dark blue
    val onPrimary = Color(0xFF000000)    // Black
    val onSecondary = Color(0xFF000000)  // Black
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFFECECEC) // Off-white
    val onSurface = Color(0xFFECECEC)    // Off-white
}

object RetroWavePalette {
    val primary = Color(0xFF00E5FF)      // Cyan
    val secondary = Color(0xFFFF00FF)    // Magenta
    val tertiary = Color(0xFFFFD700)     // Gold
    val background = Color(0xFF0B0B2B)   // Dark blue
    val surface = Color(0xFF161677)      // Medium blue
    val onPrimary = Color(0xFF000000)    // Black
    val onSecondary = Color(0xFF000000)  // Black
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFFECECEC) // Off-white
    val onSurface = Color(0xFFECECEC)    // Off-white
}

object OceanPalette {
    val primary = Color(0xFF0288D1)      // Medium blue
    val secondary = Color(0xFF00BCD4)    // Cyan
    val tertiary = Color(0xFF26A69A)     // Teal
    val background = Color(0xFFE3F2FD)   // Very light blue
    val surface = Color(0xFFFFFFFF)      // White
    val onPrimary = Color(0xFFFFFFFF)    // White
    val onSecondary = Color(0xFF000000)  // Black
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFF202020) // Dark gray
    val onSurface = Color(0xFF202020)    // Dark gray
}

object AutumnPalette {
    val primary = Color(0xFFE65100)      // Burnt orange
    val secondary = Color(0xFF795548)    // Brown
    val tertiary = Color(0xFFFFB300)     // Amber
    val background = Color(0xFFFFF8E1)   // Cream
    val surface = Color(0xFFFFFFFF)      // White
    val onPrimary = Color(0xFFFFFFFF)    // White
    val onSecondary = Color(0xFFFFFFFF)  // White
    val onTertiary = Color(0xFF000000)   // Black
    val onBackground = Color(0xFF3E2723) // Dark brown
    val onSurface = Color(0xFF3E2723)    // Dark brown
}