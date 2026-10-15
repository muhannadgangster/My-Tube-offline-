package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.VideoItem
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.liquidGlassSurface
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import kotlin.math.abs

/**
 * YouTube-Style Floating Mini-Player Component:
 * - Wide horizontal docked card floating cleanly above the bottom navigation bar
 * - Properly sized 16:9 video preview on the left
 * - Rounded corners, dark translucent liquid glass styling, fine specular border, and elevation shadow
 * - Clean title and channel name with high contrast and readable text
 * - Responsive Play/Pause and Close buttons with comfortable touch targets
 * - Real-time progress bar along the bottom edge
 * - Tap anywhere to expand seamlessly back to full player without restarting playback
 * - Swipe left or right to dismiss
 */
@Composable
fun FloatingMiniPlayer(
    video: VideoItem,
    isPaused: Boolean,
    onTogglePlayPause: () -> Unit,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    onProgressUpdate: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current

    var miniVideoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isVideoPrepared by remember { mutableStateOf(false) }
    var currentPositionMs by remember(video.id) { mutableLongStateOf(video.watchPositionMs) }
    var totalDurationMs by remember(video.id) { mutableLongStateOf(video.durationMs.coerceAtLeast(1000L)) }

    // Horizontal swipe-to-dismiss tracking
    var cumulativeDragX by remember { mutableFloatStateOf(0f) }

    DisposableEffect(video.id) {
        onDispose {
            try {
                onProgressUpdate(currentPositionMs)
                miniVideoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .then(
                if (glassConfig.isEnabled) {
                    Modifier.liquidGlassSurface(
                        shape = cardShape,
                        config = glassConfig,
                        accentColor = accentColor,
                        elevation = 6.dp
                    )
                } else {
                    Modifier
                        .shadow(elevation = 6.dp, shape = cardShape, spotColor = Color(0x40000000))
                        .clip(cardShape)
                        .background(Color(0xE61A1D27))
                        .border(
                            width = 0.75.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.White.copy(alpha = 0.06f),
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            ),
                            shape = cardShape
                        )
                }
            )
            .clickable { onExpand() }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { cumulativeDragX = 0f },
                    onDragEnd = {
                        if (abs(cumulativeDragX) > 140f) {
                            onClose()
                        }
                        cumulativeDragX = 0f
                    },
                    onDragCancel = { cumulativeDragX = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    cumulativeDragX += dragAmount
                }
            }
            .testTag("floating_mini_player")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Mini 16:9 Live Video Preview on the Left with rounded corners
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            try {
                                setOnErrorListener { _, _, _ -> true }
                                if (!video.id.startsWith("sample_")) {
                                    setVideoURI(Uri.parse(video.uriString))
                                }
                                setOnPreparedListener { mp ->
                                    isVideoPrepared = true
                                    mp.isLooping = true
                                    val dur = mp.duration.toLong()
                                    if (dur > 0) totalDurationMs = dur
                                    if (video.watchPositionMs > 500L) {
                                        seekTo(video.watchPositionMs.toInt())
                                    }
                                    if (!isPaused) start()
                                }
                            } catch (_: Exception) {}
                            miniVideoViewRef = this
                        }
                    },
                    update = { vv ->
                        miniVideoViewRef = vv
                        try {
                            if (isPaused) {
                                if (vv.isPlaying) vv.pause()
                            } else {
                                if (!vv.isPlaying) vv.start()
                            }
                            val cur = vv.currentPosition.toLong()
                            if (cur > 0) {
                                currentPositionMs = cur
                                onProgressUpdate(cur)
                            }
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Fallback crisp thumbnail poster while loading or before live frames render
                if (!isVideoPrepared || video.id.startsWith("sample_")) {
                    VideoThumbnailView(
                        video = video,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Tiny subtle expand pill indicator on top-right of preview
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(3.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Expand",
                        tint = Color.White,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 2. Center: Video Title & Channel Information
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = video.displayTitle,
                    color = YtTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = video.displayChannelName,
                    color = YtTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Right: Play/Pause, Expand, and Close controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Play / Pause Button
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("mini_player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Play" else "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Expand Full Player Button
                IconButton(
                    onClick = onExpand,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_expand")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Expand player",
                        tint = YtTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mini_player_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close player",
                        tint = YtTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        // 4. Slender Progress Bar at bottom of card
        val progressFraction = if (totalDurationMs > 0) {
            (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)),
            color = accentColor,
            trackColor = Color.Transparent
        )
    }
}
