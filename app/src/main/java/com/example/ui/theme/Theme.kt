package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

// Local theme name provider
val LocalUiTheme = staticCompositionLocalOf { "Dark Mode" }

// Local Accent Color provider (Replaces or tints Red elements across the app: Subscribe, Like, Progress bars, etc.)
val LocalAppAccentColor = staticCompositionLocalOf { YtRed }

// 1. YouTube Dark Color Scheme (Standard Pure Dark)
private val YouTubeDarkColorScheme = darkColorScheme(
    primary = YtRed,
    onPrimary = Color.White,
    primaryContainer = YtSurfaceVariant,
    onPrimaryContainer = YtTextPrimary,
    secondary = YtBlue,
    onSecondary = Color.White,
    secondaryContainer = YtSurfaceVariant,
    onSecondaryContainer = YtTextPrimary,
    background = YtDarkBackground,
    onBackground = YtTextPrimary,
    surface = YtDarkBackground,
    onSurface = YtTextPrimary,
    surfaceVariant = YtSurfaceVariant,
    onSurfaceVariant = YtTextSecondary,
    outline = YtBorder,
    outlineVariant = YtSurfaceHigher
)

// 2. YouTube Light Color Scheme (Clean White & Grey)
private val YouTubeLightColorScheme = lightColorScheme(
    primary = YtRed,
    onPrimary = Color.White,
    primaryContainer = YtLightSurfaceVariant,
    onPrimaryContainer = YtLightTextPrimary,
    secondary = YtBlue,
    onSecondary = Color.White,
    secondaryContainer = YtLightSurfaceVariant,
    onSecondaryContainer = YtLightTextPrimary,
    background = YtLightBackground,
    onBackground = YtLightTextPrimary,
    surface = YtLightSurface,
    onSurface = YtLightTextPrimary,
    surfaceVariant = YtLightSurfaceVariant,
    onSurfaceVariant = YtLightTextSecondary,
    outline = YtLightBorder,
    outlineVariant = Color(0xFFD6D6D6)
)

// 3. Liquid Glass (Dark) - Frosted Glass on Midnight
private val LiquidGlassDarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color.White,
    primaryContainer = YtGlassDarkSurfaceVariant,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF818CF8),
    onSecondary = Color.White,
    secondaryContainer = YtGlassDarkSurfaceVariant,
    onSecondaryContainer = Color.White,
    background = YtGlassDarkBackground,
    onBackground = Color.White,
    surface = YtGlassDarkSurface,
    onSurface = Color.White,
    surfaceVariant = YtGlassDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = YtGlassDarkBorder,
    outlineVariant = Color(0x3338BDF8)
)

// 4. Liquid Glass (Light) - Frosted Milky Glass on Pearl
private val LiquidGlassLightColorScheme = lightColorScheme(
    primary = YtRed,
    onPrimary = Color.White,
    primaryContainer = YtGlassLightSurfaceVariant,
    onPrimaryContainer = YtGlassLightTextPrimary,
    secondary = YtBlue,
    onSecondary = Color.White,
    secondaryContainer = YtGlassLightSurfaceVariant,
    onSecondaryContainer = YtGlassLightTextPrimary,
    background = YtGlassLightBackground,
    onBackground = YtGlassLightTextPrimary,
    surface = YtGlassLightSurface,
    onSurface = YtGlassLightTextPrimary,
    surfaceVariant = YtGlassLightSurfaceVariant,
    onSurfaceVariant = YtGlassLightTextSecondary,
    outline = YtGlassLightBorder,
    outlineVariant = Color(0x44CBD5E1)
)

// 5. Liquid Glass with Reflection - Iridescent Specular Highlighting
private val LiquidGlassReflectColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color.White,
    primaryContainer = YtGlassReflectSurfaceVariant,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFA78BFA),
    onSecondary = Color.White,
    secondaryContainer = YtGlassReflectSurfaceVariant,
    onSecondaryContainer = Color.White,
    background = YtGlassReflectBackground,
    onBackground = Color.White,
    surface = YtGlassReflectSurface,
    onSurface = Color.White,
    surfaceVariant = YtGlassReflectSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = YtGlassReflectBorder,
    outlineVariant = Color(0x6638BDF8)
)

/**
 * Modifier extension to apply frosted glass with subtle borders and specular reflective highlight line
 */
fun Modifier.glassEffect(
    shape: Shape,
    hasReflection: Boolean = false,
    backgroundColor: Color = Color(0xD9101726),
    borderColor: Color = Color(0x4438BDF8)
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(0.8.dp, borderColor, shape)
    .then(
        if (hasReflection) {
            Modifier.drawWithContent {
                drawContent()
                // Reflective diagonal highlight line across the upper surface
                val highlightBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.6f, size.height * 0.4f)
                )
                drawLine(
                    brush = highlightBrush,
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.85f, size.height * 0.25f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        } else {
            Modifier
        }
    )

@Composable
fun MyApplicationTheme(
    uiTheme: String = "Dark Mode",
    darkTheme: Boolean = (uiTheme != "Light Mode" && uiTheme != "Liquid Glass (Light)"),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentDensity = LocalDensity.current
    val adaptiveDensity = Density(
        density = currentDensity.density.coerceIn(1.0f, 3.2f),
        fontScale = currentDensity.fontScale.coerceIn(0.85f, 1.15f)
    )

    val colorScheme = when (uiTheme) {
        "Light Mode" -> YouTubeLightColorScheme
        "Liquid Glass (Dark)" -> LiquidGlassDarkColorScheme
        "Liquid Glass (Light)" -> LiquidGlassLightColorScheme
        "Liquid Glass with Reflection" -> LiquidGlassReflectColorScheme
        else -> YouTubeDarkColorScheme
    }

    CompositionLocalProvider(
        LocalDensity provides adaptiveDensity,
        LocalUiTheme provides uiTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
