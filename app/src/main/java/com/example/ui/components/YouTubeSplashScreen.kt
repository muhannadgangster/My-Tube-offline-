package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YtRed
import kotlinx.coroutines.delay

/**
 * Authentic Native YouTube-Style Startup Splash Screen with Custom Intro Animation:
 * 1. Initial State: Centered YouTube play logo & typography on dark background (#0F0F0F).
 * 2. Logo Zoom & Transition: Logo shrinks and scales down smoothly into a small white play icon.
 * 3. Loading Progress Bar: Sleek horizontal bar where the red progress indicator smoothly fills
 *    from 0% to 100% over 2-3 seconds.
 * 4. Seamless Transition: Once complete, smoothly fades out & scales up to reveal the app.
 */
@Composable
fun YouTubeSplashScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Initial Logo Centered, 1 = Transitioning to Progress Bar, 2 = Completed
    val splashAlpha = remember { Animatable(1.0f) }
    val splashScale = remember { Animatable(1.0f) }
    val initialLogoAlpha = remember { Animatable(1.0f) }
    val initialLogoScale = remember { Animatable(1.0f) }

    val progressBarAlpha = remember { Animatable(0.0f) }
    val progress = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        // Step 1: Display centered YouTube logo for 600ms
        delay(600)

        // Step 2: Animate logo shrink and crossfade to loading bar
        initialLogoScale.animateTo(
            targetValue = 0.85f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
        initialLogoAlpha.animateTo(
            targetValue = 0.0f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        )

        // Fade in progress bar & play icon
        progressBarAlpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        )

        // Step 3: Smooth progress fill from 0% to 100% over 1800ms (Total ~2.7s)
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )

        delay(150)

        // Step 4: Seamless fade out & scale up overlay transition
        splashScale.animateTo(
            targetValue = 1.06f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
        splashAlpha.animateTo(
            targetValue = 0.0f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )

        onAnimationComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .alpha(splashAlpha.value)
            .scale(splashScale.value)
            .testTag("youtube_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Initial State: Centered YouTube Logo
        if (initialLogoAlpha.value > 0f) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .scale(initialLogoScale.value)
                    .alpha(initialLogoAlpha.value)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Red YouTube Play Badge
                    Box(
                        modifier = Modifier
                            .size(width = 46.dp, height = 32.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(YtRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "YouTube Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "YouTube",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.8).sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "MyTube Offline",
                    color = Color(0xFF717171),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }
        }

        // 2. Loading State: Small white triangular play icon + Horizontal Red Progress Bar
        if (progressBarAlpha.value > 0f) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(progressBarAlpha.value)
                    .fillMaxWidth()
            ) {
                // Sleek small white play icon
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Loading indicator",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Sleek horizontal loading progress track (matching Image 2)
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF272727))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = progress.value)
                            .clip(RoundedCornerShape(2.dp))
                            .background(YtRed)
                    )
                }
            }
        }
    }
}
