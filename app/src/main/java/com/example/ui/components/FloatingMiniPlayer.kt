package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.VideoItem
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.liquidGlassSurface
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * YouTube-Style Floating Draggable Mini Video Player (Floating Inside App):
 * - Displays active moving video in a floating 16:9 rectangular window
 * - Positioned near the bottom-right corner by default, above navigation bar
 * - Smoothly draggable around the usable screen area with boundary clamping
 * - Prominent Pause/Play button on the left side of the video
 * - Prominent Close (X) button on the right side of the video
 * - Video is sharp and visible behind subtle translucent controls
 * - Tapping anywhere on the video reopens full player at the exact playback position
 * - Continuous playback without restarting or audio glitch
 */
@Composable
fun FloatingMiniPlayer(
    video: VideoItem,
    isPaused: Boolean,
    onTogglePlayPause: () -> Unit,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    onProgressUpdate: (Long) -> Unit = {},
    onEnterPip: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current

    var miniVideoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isVideoPrepared by remember { mutableStateOf(false) }
    var currentPositionMs by remember(video.id) { mutableLongStateOf(video.watchPositionMs) }
    var totalDurationMs by remember(video.id) { mutableLongStateOf(video.durationMs.coerceAtLeast(1000L)) }

    DisposableEffect(video.id) {
        onDispose {
            try {
                onProgressUpdate(currentPositionMs)
                miniVideoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    // Outer container fills screen space to allow free dragging
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        val playerWidthDp = 210.dp
        val playerHeightDp = 118.dp // 16:9 aspect ratio

        val playerWidthPx = with(density) { playerWidthDp.toPx() }
        val playerHeightPx = with(density) { playerHeightDp.toPx() }

        val marginPx = with(density) { 14.dp.toPx() }
        val bottomNavMarginPx = with(density) { 86.dp.toPx() }
        val topStatusMarginPx = with(density) { 54.dp.toPx() }

        val defaultX = max(marginPx, maxWidthPx - playerWidthPx - marginPx)
        val defaultY = max(topStatusMarginPx, maxHeightPx - playerHeightPx - bottomNavMarginPx)

        var offsetX by remember { mutableFloatStateOf(defaultX) }
        var offsetY by remember { mutableFloatStateOf(defaultY) }
        var hasUserMoved by remember { mutableStateOf(false) }

        // If user hasn't moved the player, stay anchored near bottom-right on layout changes
        if (!hasUserMoved) {
            offsetX = defaultX
            offsetY = defaultY
        }

        val cardShape = RoundedCornerShape(14.dp)

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(width = playerWidthDp, height = playerHeightDp)
                .shadow(
                    elevation = 10.dp,
                    shape = cardShape,
                    spotColor = Color(0x99000000)
                )
                .clip(cardShape)
                .background(Color.Black)
                .then(
                    if (glassConfig.isEnabled) {
                        Modifier.liquidGlassSurface(
                            shape = cardShape,
                            config = glassConfig,
                            accentColor = accentColor,
                            elevation = 8.dp
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0.10f)
                                )
                            ),
                            shape = cardShape
                        )
                    }
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {},
                        onDragEnd = {},
                        onDragCancel = {}
                    ) { change, dragAmount ->
                        change.consume()
                        hasUserMoved = true
                        val minX = marginPx
                        val maxX = max(minX, maxWidthPx - playerWidthPx - marginPx)
                        val minY = topStatusMarginPx
                        val maxY = max(minY, maxHeightPx - playerHeightPx - bottomNavMarginPx)

                        offsetX = (offsetX + dragAmount.x).coerceIn(minX, maxX)
                        offsetY = (offsetY + dragAmount.y).coerceIn(minY, maxY)
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onProgressUpdate(currentPositionMs)
                    onExpand()
                }
                .testTag("floating_mini_player")
        ) {
            // 1. Full 16:9 Live Video Player View
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        tag = video.id
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
                    val currentTag = vv.tag as? String
                    if (currentTag != video.id) {
                        vv.tag = video.id
                        isVideoPrepared = false
                        try {
                            vv.stopPlayback()
                            if (!video.id.startsWith("sample_")) {
                                vv.setVideoURI(Uri.parse(video.uriString))
                            }
                        } catch (_: Exception) {}
                    }
                    try {
                        if (isPaused) {
                            if (vv.isPlaying) vv.pause()
                        } else {
                            if (!vv.isPlaying && isVideoPrepared) vv.start()
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

            // Fallback crisp thumbnail while loading
            if (!isVideoPrepared || video.id.startsWith("sample_")) {
                VideoThumbnailView(
                    video = video,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Translucent Overlaid Controls (Exact Match to Reference Image)
            // Left: Prominent Pause/Play button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .border(0.75.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTogglePlayPause()
                        }
                        .testTag("mini_player_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Play" else "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Top-Right Controls: Close (X) button + PiP button
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onEnterPip != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x88000000))
                            .border(0.75.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onProgressUpdate(currentPositionMs)
                                onEnterPip()
                            }
                            .testTag("mini_player_pip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureInPictureAlt,
                            contentDescription = "System PiP",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .border(0.75.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onClose()
                        }
                        .testTag("mini_player_close"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close player",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom-Right: Subtle Expand Icon
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 6.dp, end = 8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0x77000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onProgressUpdate(currentPositionMs)
                        onExpand()
                    }
                    .testTag("mini_player_expand"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Expand Full Player",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            // Slender progress indicator along bottom edge
            val progressFraction = if (totalDurationMs > 0) {
                (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
            } else 0f

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .align(Alignment.BottomCenter),
                color = accentColor,
                trackColor = Color.White.copy(alpha = 0.25f)
            )
        }
    }
}
