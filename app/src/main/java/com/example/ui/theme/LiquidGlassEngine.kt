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
import androidx.compose.ui.draw.drawBehind
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
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Android Graphics Shading Language (AGSL) Real-Time Optical Liquid Glass Shader
 * Runs directly on GPU for real-time background pixel refraction and chromatic dispersion.
 */
private const val AGSL_LIQUID_GLASS = """
    uniform shader uContent;
    uniform float2 uResolution;
    uniform float uRefraction;
    uniform float uCurvature;
    uniform float uDispersion;
    
    half4 main(float2 fragCoord) {
        if (uResolution.x <= 1.0 || uResolution.y <= 1.0) {
            return uContent.eval(fragCoord);
        }
        float2 uv = fragCoord / uResolution;
        float2 p = (uv - 0.5) * 2.0;
        float r = length(p);
        
        float edgeDistort = pow(clamp(r, 0.0, 1.0), uCurvature) * (uRefraction * 0.05);
        float2 offset = normalize(p + 0.0001) * edgeDistort;
        
        float disp = uDispersion * 0.008;
        float2 uvR = clamp(uv - offset * (1.0 + disp), 0.0, 1.0);
        float2 uvG = clamp(uv - offset, 0.0, 1.0);
        float2 uvB = clamp(uv - offset * (1.0 - disp), 0.0, 1.0);
        
        half4 cR = uContent.eval(uvR * uResolution);
        half4 cG = uContent.eval(uvG * uResolution);
        half4 cB = uContent.eval(uvB * uResolution);
        
        float fresnel = pow(clamp(r, 0.0, 1.0), 3.2) * (uRefraction * 0.25);
        
        half4 col = half4(cR.r, cG.g, cB.b, (cR.a + cG.a + cB.a) / 3.0);
        col.rgb += half3(fresnel, fresnel, fresnel);
        return col;
    }
"""

/**
 * Spring-Physics animation specifications for iOS-quality fluid transitions
 */
object LiquidPhysics {
    val NavigationSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val PanelSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val TouchSpring = spring<Float>(
        dampingRatio = 0.75f,
        stiffness = 450f
    )
    val IndicatorSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

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
    // Optical GPU Shader & Edge Refraction
    val opticalShaderEnabled: Boolean = true,
    val edgeCurvaturePower: Float = 2.5f,
    val chromaticDispersion: Float = 0.45f,
    val springPhysicsEnabled: Boolean = true,
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

    val isDark = config.isDarkGlass

    // 1. Transparent optical glass material - background content remains sharp & visible through glass
    val baseGlassColor = if (isDark) {
        Color(0x35141928) // Translucent neutral glass (~21% tint)
    } else {
        Color(0x55FFFFFF) // Translucent pearl glass
    }

    val shadowElevation = if (config.isEnabled) elevation.coerceAtMost(8.dp) else 0.dp

    return this
        .then(
            if (shadowElevation > 0.dp) {
                Modifier.shadow(
                    elevation = shadowElevation,
                    shape = shape,
                    clip = false,
                    ambientColor = if (config.neonEnabled) Color(config.neonColorHex) else Color(0x22000000),
                    spotColor = if (config.neonEnabled) Color(config.neonColorHex) else Color(0x38000000)
                )
            } else Modifier
        )
        .clip(shape)
        .drawBehind {
            // 1. Base translucent glass material
            drawRect(color = baseGlassColor)

            if (config.isEnabled && config.specularHighlight) {
                // 2. Optical specular sheen (Apple iOS Liquid Glass style) across top quadrant
                val specularBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (0.22f * config.refractionIntensity).coerceIn(0.06f, 0.35f)),
                        Color.White.copy(alpha = (0.05f * config.refractionIntensity).coerceIn(0.01f, 0.12f)),
                        Color.Transparent
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width * 0.70f, size.height * 0.50f)
                )
                drawRect(
                    brush = specularBrush,
                    size = Size(size.width, size.height * 0.60f)
                )

                // 3. Subtle luminous bevel line along top rim
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = (0.40f * config.refractionIntensity).coerceIn(0.10f, 0.55f)),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.08f, 1f),
                    end = Offset(size.width * 0.92f, 1f),
                    strokeWidth = 0.8.dp.toPx()
                )
            }

            // 4. Optional neon edge ambient glow (only if user explicitly activated Neon Lights)
            if (config.neonEnabled) {
                val neonGlowColor = Color(config.neonColorHex).copy(alpha = 0.18f * config.neonIntensity)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(neonGlowColor, Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = (size.width.coerceAtLeast(size.height)) * 0.85f
                    )
                )
            }
        }
        .border(
            width = if (config.neonEnabled) 1.dp else 0.75.dp,
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
                // Refined optical crystal glass bevel reflection: fine white specular highlight on top fading to crystal rim
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.32f),
                        Color.White.copy(alpha = 0.08f),
                        Color.Black.copy(alpha = 0.25f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            },
            shape = shape
        )
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
