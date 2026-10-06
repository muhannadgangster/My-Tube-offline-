package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtRed
import kotlinx.coroutines.delay

/**
 * Authentic YouTube Mobile App Opening Splash Screen:
 * - Pure Dark background (#0F0F0F)
 * - Official centered YouTube Red Play Logo + bold YouTube typography
 * - Smooth scale-up and subtle fade transition (1.2 to 1.4 seconds duration)
 * - Auto-triggers onComplete callback to seamlessly reveal main feed
 */
@Composable
fun YouTubeSplashScreen(
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.82f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // Hold for 1350ms for authentic official YouTube launch feel
        delay(1350)
        onAnimationComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(YtDarkBackground)
            .testTag("youtube_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        ) {
            // Authentic YouTube Logo Row: Red Play Badge + "YouTube" text
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red Play Icon Badge
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 32.dp)
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

                // YouTube Typography
                Text(
                    text = "YouTube",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.8).sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle / offline badge
            Text(
                text = "Premium Offline Player",
                color = Color(0xFF888888),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )
        }
    }
}
