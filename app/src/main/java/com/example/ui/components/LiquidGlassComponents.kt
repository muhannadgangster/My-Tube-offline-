package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.LiquidGlassStyleConfig
import com.example.ui.theme.liquidGlassInteractive
import com.example.ui.theme.liquidGlassSurface
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import com.example.ui.theme.YtRed

/**
 * Reusable GlassSurface container.
 * When Liquid Glass is enabled: Renders full iOS optical glassmorphism with dynamic depth,
 * refraction, specular bevel, and background transparency.
 * When disabled: Gracefully falls back to a clean conventional Material card/surface.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 6.dp,
    fallbackColor: Color = YtSurfaceDark,
    borderColor: Color = YtBorder,
    interactiveGlow: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current

    Box(
        modifier = modifier
            .then(
                if (glassConfig.isEnabled) {
                    Modifier.liquidGlassSurface(
                        shape = shape,
                        config = glassConfig,
                        accentColor = accentColor,
                        elevation = elevation,
                        interactiveGlow = interactiveGlow
                    )
                } else {
                    Modifier
                        .shadow(elevation.coerceAtMost(3.dp), shape)
                        .clip(shape)
                        .background(fallbackColor)
                        .border(1.dp, borderColor, shape)
                }
            )
    ) {
        content()
    }
}

/**
 * Reusable GlassButton.
 * Interactive tactile button with liquid glass sheen, edge highlight, and responsive tap feedback.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(18.dp),
    tintColor: Color? = null,
    contentColor: Color = Color.White,
    testTag: String = "glass_button",
    enabled: Boolean = true
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current
    val buttonTint = tintColor ?: accentColor

    Box(
        modifier = modifier
            .then(
                if (glassConfig.isEnabled) {
                    Modifier.liquidGlassInteractive(
                        shape = shape,
                        config = glassConfig,
                        accentColor = buttonTint,
                        elevation = 4.dp
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(buttonTint)
                }
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 9.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                if (text != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
            if (text != null) {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Reusable GlassIconButton with circular or rounded glass surface.
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    iconSize: Dp = 20.dp,
    tint: Color = Color.White,
    shape: Shape = CircleShape,
    testTag: String = "glass_icon_button"
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (glassConfig.isEnabled) {
                    Modifier.liquidGlassSurface(
                        shape = shape,
                        config = glassConfig,
                        accentColor = accentColor,
                        elevation = 3.dp
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(Color(0x77000000))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), shape)
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Reusable GlassDock container for bottom bars and floating navigation.
 */
@Composable
fun GlassDock(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    content: @Composable RowScope.() -> Unit
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current

    Box(
        modifier = modifier
            .then(
                if (glassConfig.isEnabled) {
                    Modifier.liquidGlassSurface(
                        shape = shape,
                        config = glassConfig,
                        accentColor = accentColor,
                        elevation = 8.dp
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(Color(0xFF181818))
                        .border(1.dp, YtBorder, shape)
                }
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            content()
        }
    }
}

/**
 * Reusable GlassSlider with numerical live value display and glass track.
 */
@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    valueFormatter: (Float) -> String = { "%.2f".format(it) },
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "glass_slider"
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (enabled) YtTextPrimary else YtTextSecondary.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valueFormatter(value),
                color = if (enabled) accentColor else YtTextSecondary.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = if (glassConfig.isEnabled) Color.White else accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

/**
 * Subtle Video Reflection & Light Spill:
 * Renders a restrained, tasteful reflection or ambient light spill beneath video player or video cards.
 * Sharp video stays sharp, while light spill fades softly into the background.
 * Respects reflection intensity setting.
 */
@Composable
fun GlassVideoReflection(
    modifier: Modifier = Modifier,
    height: Dp = 28.dp,
    tintColor: Color = LocalAppAccentColor.current
) {
    val glassConfig = LocalLiquidGlassConfig.current
    if (!glassConfig.isEnabled || glassConfig.reflectionIntensity <= 0.05f) {
        return
    }

    val reflectionAlpha = (0.22f * glassConfig.reflectionIntensity).coerceIn(0.04f, 0.35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .drawBehind {
                val spillBrush = Brush.verticalGradient(
                    colors = listOf(
                        tintColor.copy(alpha = reflectionAlpha),
                        tintColor.copy(alpha = reflectionAlpha * 0.4f),
                        Color.Transparent
                    )
                )
                drawRect(brush = spillBrush)
            }
    )
}

/**
 * Fully customizable Liquid Glass Subscribe Button:
 * - User-selectable glass tint (white, red, cyan, custom hex)
 * - Adjustable transparency, vibrancy, depth, refraction, edge highlights, and corner radius
 * - Clear distinction between Subscribe and Subscribed states
 * - Preserves high contrast, readability, and full 48dp accessibility touch target
 */
@Composable
fun LiquidGlassSubscribeButton(
    isSubscribed: Boolean,
    onToggleSubscribe: () -> Unit,
    modifier: Modifier = Modifier,
    tintHex: Long? = null,
    customShape: Shape? = null,
    testTag: String = "liquid_glass_subscribe_button"
) {
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current

    val effectiveTintHex = tintHex ?: glassConfig.subscribeGlassTintHex
    val effectiveCornerRadius = glassConfig.subscribeCornerRadius
    val shape = customShape ?: RoundedCornerShape(effectiveCornerRadius.dp)

    val baseTintColor = Color(effectiveTintHex)

    // Clear readable content styling
    val isGlassActive = glassConfig.isEnabled && glassConfig.subscribeGlassEnabled

    val buttonAlpha = animateFloatAsState(
        targetValue = if (isSubscribed) 0.35f else glassConfig.subscribeTransparency.coerceIn(0.2f, 0.95f),
        animationSpec = tween(250),
        label = "sub_alpha"
    ).value

    Box(
        modifier = modifier
            .then(
                if (isGlassActive) {
                    val subConfig = glassConfig.copy(
                        transparency = buttonAlpha,
                        depthEnabled = glassConfig.subscribeDepth,
                        refractionAmount = glassConfig.subscribeRefraction,
                        edgeHighlights = glassConfig.subscribeEdgeHighlight,
                        glassThickness = 1.0f
                    )
                    Modifier.liquidGlassSurface(
                        shape = shape,
                        config = subConfig,
                        accentColor = if (isSubscribed) Color.White else baseTintColor,
                        elevation = if (glassConfig.subscribeDepth && !isSubscribed) 4.dp else 0.dp
                    )
                } else {
                    Modifier
                        .clip(shape)
                        .background(if (isSubscribed) Color(0x33FFFFFF) else baseTintColor)
                        .border(
                            width = 1.dp,
                            color = if (isSubscribed) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                            shape = shape
                        )
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggleSubscribe
            )
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSubscribed) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Subscribed",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Subscribed",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                val textColor = if (baseTintColor.red > 0.8f && baseTintColor.green > 0.8f && baseTintColor.blue > 0.8f) {
                    Color.Black // Classic high-contrast black on white pill
                } else {
                    Color.White
                }
                Text(
                    text = "Subscribe",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
