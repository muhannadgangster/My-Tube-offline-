package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ScalingUtils: Calculates responsive DPI scaling factor based on screenWidthDp
 * to maintain pixel-perfect, consistent sizing for icons and typography across
 * various device densities and screen sizes (compact phones, tablets, foldables).
 */
object ScalingUtils {
    // Standard baseline Android viewport width in dp (360dp standard YouTube baseline)
    private const val BASE_SCREEN_WIDTH_DP = 360f

    /**
     * Calculates the scaling factor normalized against the baseline width.
     * Clamped to safe range [0.85f, 1.25f] to prevent extreme shrinking or bloating.
     */
    @Composable
    fun getScaleFactor(): Float {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp.toFloat()
        val rawFactor = screenWidthDp / BASE_SCREEN_WIDTH_DP
        return rawFactor.coerceIn(0.85f, 1.25f)
    }

    /**
     * Scales a Dp value based on the DPI scaling factor.
     */
    @Composable
    fun scaleDp(baseDp: Dp): Dp {
        val factor = getScaleFactor()
        return (baseDp.value * factor).dp
    }

    /**
     * Scales a TextUnit (Sp) value based on the DPI scaling factor.
     */
    @Composable
    fun scaleSp(baseSp: TextUnit): TextUnit {
        val factor = getScaleFactor()
        return (baseSp.value * factor).sp
    }

    @Composable
    fun scaledDp(value: Float): Dp = (value * getScaleFactor()).dp

    @Composable
    fun scaledDp(value: Int): Dp = (value.toFloat() * getScaleFactor()).dp

    @Composable
    fun scaledSp(value: Float): TextUnit = (value * getScaleFactor()).sp

    @Composable
    fun scaledSp(value: Int): TextUnit = (value.toFloat() * getScaleFactor()).sp
}

/**
 * Convenient Compose extension functions for scaling dimensions and typography.
 */
@Composable
fun Dp.scaled(): Dp = ScalingUtils.scaleDp(this)

@Composable
fun TextUnit.scaled(): TextUnit = ScalingUtils.scaleSp(this)
