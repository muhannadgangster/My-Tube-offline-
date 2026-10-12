package com.example.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Full-app Liquid Glass & Neon styling configuration state
 */
@Immutable
data class LiquidGlassStyleConfig(
    val isEnabled: Boolean = true,
    val glassIntensity: Float = 0.85f, // 0.0f .. 1.0f
    val blurRadius: Float = 24f, // 0..50
    val transparency: Float = 0.70f, // 0.2 .. 0.95
    val refractionIntensity: Float = 0.65f, // 0.0 .. 1.0 (edge lens dispersion)
    val specularHighlight: Boolean = true,
    val borderThicknessDp: Float = 1.0f,
    val isDarkGlass: Boolean = true,
    val isFrosted: Boolean = true,
    // Neon Lights
    val neonEnabled: Boolean = false,
    val neonColorHex: Long = 0xFF00E5FFL, // Electric Cyan default
    val neonIntensity: Float = 0.85f,
    val neonAnimated: Boolean = false,
    val neonAnimationSpeed: Float = 1.0f
)

val LocalLiquidGlassConfig = staticCompositionLocalOf { LiquidGlassStyleConfig() }

/**
 * Dimensional, physically-inspired iOS optical glass surface modifier:
 * - Dimensional layering (surface depth, curved bevel, optical reflection)
 * - Beveled specular edge reflections & luminous top-left rim
 * - Multi-stop gradient simulating refractive internal caustic dispersion
 * - Cohesive Neon edge illumination bloom if Neon is enabled
 */
fun Modifier.liquidGlassSurface(
    shape: Shape,
    config: LiquidGlassStyleConfig,
    accentColor: Color = Color(0xFF38BDF8),
    elevation: Dp = 6.dp,
    interactiveGlow: Boolean = false
): Modifier {
    if (!config.isEnabled && !config.neonEnabled) {
        return this
    }

    val alpha = (1f - config.transparency.coerceIn(0.2f, 0.92f))
    val isDark = config.isDarkGlass

    val baseGlassColor = if (isDark) {
        Color(0xFF0F172A).copy(alpha = alpha.coerceIn(0.25f, 0.88f))
    } else {
        Color(0xFFF8FAFC).copy(alpha = alpha.coerceIn(0.35f, 0.92f))
    }

    val rimHighlightColor = if (isDark) {
        Color.White.copy(alpha = (0.45f * config.refractionIntensity).coerceIn(0.12f, 0.7f))
    } else {
        Color.White.copy(alpha = (0.75f * config.refractionIntensity).coerceIn(0.25f, 0.9f))
    }

    val bottomShadowColor = if (isDark) {
        Color.Black.copy(alpha = 0.55f)
    } else {
        Color(0xFF64748B).copy(alpha = 0.22f)
    }

    // Border: luminous optical glass or neon
    val borderColor = if (config.neonEnabled) {
        Color(config.neonColorHex).copy(alpha = (0.85f * config.neonIntensity).coerceIn(0.3f, 1f))
    } else if (isDark) {
        Color(0xFF38BDF8).copy(alpha = (0.35f * config.glassIntensity).coerceIn(0.15f, 0.6f))
    } else {
        Color(0xFF94A3B8).copy(alpha = (0.45f * config.glassIntensity).coerceIn(0.15f, 0.6f))
    }

    val shadowElevation = if (config.isEnabled) elevation else 0.dp

    return this
        .then(
            if (shadowElevation > 0.dp) {
                Modifier.shadow(
                    elevation = shadowElevation,
                    shape = shape,
                    clip = false,
                    ambientColor = if (config.neonEnabled) Color(config.neonColorHex) else Color(0x33000000),
                    spotColor = if (config.neonEnabled) Color(config.neonColorHex) else Color(0x66000000)
                )
            } else Modifier
        )
        .clip(shape)
        .background(baseGlassColor)
        .border(
            width = (config.borderThicknessDp.dp).coerceAtLeast(0.6.dp),
            brush = if (config.neonEnabled) {
                Brush.sweepGradient(
                    listOf(
                        Color(config.neonColorHex),
                        Color(config.neonColorHex).copy(alpha = 0.4f),
                        Color.White,
                        Color(config.neonColorHex)
                    )
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(
                        rimHighlightColor,
                        borderColor,
                        bottomShadowColor
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            },
            shape = shape
        )
        .drawWithContent {
            drawContent()

            if (config.isEnabled && config.specularHighlight) {
                // Top-left curved optical specular sheen (Apple iOS Liquid Glass style)
                val specularBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.38f * config.refractionIntensity),
                        Color.White.copy(alpha = 0.10f * config.refractionIntensity),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.75f, size.height * 0.45f)
                )
                drawRect(
                    brush = specularBrush,
                    size = Size(size.width, size.height * 0.55f)
                )

                // Thin luminous bevel stroke line across top rim
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.65f * config.refractionIntensity),
                            Color.White.copy(alpha = 0.25f * config.refractionIntensity),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.05f, 1f),
                    end = Offset(size.width * 0.95f, 1f),
                    strokeWidth = 1.5.dp.toPx()
                )
            }

            // Optional neon edge halo illumination
            if (config.neonEnabled) {
                val neonGlowColor = Color(config.neonColorHex).copy(alpha = 0.22f * config.neonIntensity)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(neonGlowColor, Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = (size.width.coerceAtLeast(size.height)) * 0.85f
                    )
                )
            }
        }
}

/**
 * Modifier for buttons, chips, and small controls with animated specular or neon glow
 */
@Composable
fun Modifier.liquidGlassInteractive(
    shape: Shape,
    config: LiquidGlassStyleConfig,
    accentColor: Color = LocalAppAccentColor.current,
    elevation: Dp = 4.dp
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_sheen")
    val sweepOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (4000 / config.neonAnimationSpeed.coerceAtLeast(0.5f)).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheen_anim"
    )

    return this
        .liquidGlassSurface(shape, config, accentColor, elevation)
        .then(
            if (config.neonEnabled && config.neonAnimated) {
                Modifier.drawWithContent {
                    drawContent()
                    val pulseAlpha = (sweepOffset % 300f) / 300f
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(config.neonColorHex).copy(alpha = 0.25f * pulseAlpha),
                                Color.Transparent
                            ),
                            startX = sweepOffset - 120f,
                            endX = sweepOffset + 120f
                        )
                    )
                }
            } else Modifier
        )
}
