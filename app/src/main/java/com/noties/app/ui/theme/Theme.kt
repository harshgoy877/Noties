package com.noties.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5B95),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEFDBFF),
    onTertiaryContainer = Color(0xFF26004E),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F),
    inverseOnSurface = Color(0xFFF1F0F4),
    inverseSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF9ECAFF)
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD3BFE6),
    onTertiary = Color(0xFF3C1A65),
    tertiaryContainer = Color(0xFF533D7C),
    onTertiaryContainer = Color(0xFFEFDBFF),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199),
    inverseOnSurface = Color(0xFF111318),
    inverseSurface = Color(0xFFE2E2E6),
    inversePrimary = Color(0xFF1976D2)
)

// Note color scheme for color tags
object NoteColors {
    val Blue = Color(0xFFE3F2FD)
    val Green = Color(0xFFE8F5E8)
    val Yellow = Color(0xFFFFF9C4)
    val Orange = Color(0xFFFFE0B2)
    val Red = Color(0xFFFFEBEE)
    val Purple = Color(0xFFF3E5F5)
    val Teal = Color(0xFFE0F2F1)
    val Pink = Color(0xFFFCE4EC)

    val BlueDark = Color(0xFF1E3A8A)
    val GreenDark = Color(0xFF166534)
    val YellowDark = Color(0xFF92400E)
    val OrangeDark = Color(0xFFEA580C)
    val RedDark = Color(0xFFDC2626)
    val PurpleDark = Color(0xFF7C3AED)
    val TealDark = Color(0xFF0F766E)
    val PinkDark = Color(0xFFDB2777)

    fun getNoteColor(colorTag: String, isDark: Boolean = false): Color {
        return when (colorTag.lowercase()) {
            "blue" -> if (isDark) BlueDark else Blue
            "green" -> if (isDark) GreenDark else Green
            "yellow" -> if (isDark) YellowDark else Yellow
            "orange" -> if (isDark) OrangeDark else Orange
            "red" -> if (isDark) RedDark else Red
            "purple" -> if (isDark) PurpleDark else Purple
            "teal" -> if (isDark) TealDark else Teal
            "pink" -> if (isDark) PinkDark else Pink
            else -> if (isDark) BlueDark else Blue
        }
    }
}

@Composable
fun NotiesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
