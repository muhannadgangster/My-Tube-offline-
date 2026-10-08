package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.VideoItem
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import kotlin.math.roundToInt

/**
 * Floating Mini-Player (Picture-in-Picture) Component:
 * - Stays active and rendered on top of the entire app stack (Global Overlay)
 * - Draggable across the screen with pan gestures
 * - Swipe left/right or tap 'X' to close
 * - Tap anywhere to seamlessly expand back to full player
 * - Play/Pause quick toggle
 */
@Composable
fun FloatingMiniPlayer(
    video: VideoItem,
    isPaused: Boolean,
    onTogglePlayPause: () -> Unit,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var miniVideoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isVideoPrepared by remember { mutableStateOf(false) }

    DisposableEffect(video.id) {
        onDispose {
            try {
                miniVideoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val playerWidthDp = 220.dp
        val playerHeightDp = 68.dp

        val playerWidthPx = with(density) { playerWidthDp.toPx() }
        val playerHeightPx = with(density) { playerHeightDp.toPx() }

        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        val defaultX = (screenWidthPx - playerWidthPx - with(density) { 16.dp.toPx() }).coerceAtLeast(0f)
        val defaultY = (screenHeightPx - playerHeightPx - with(density) { 76.dp.toPx() }).coerceAtLeast(0f)

        var offsetX by remember(video.id) { mutableFloatStateOf(defaultX) }
        var offsetY by remember(video.id) { mutableFloatStateOf(defaultY) }

        // Horizontal fling/drag tracking for swipe-to-dismiss
        var cumulativeDragX by remember { mutableFloatStateOf(0f) }

        Surface(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .width(playerWidthDp)
                .height(playerHeightDp)
                .shadow(12.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(YtSurfaceDark)
                .border(1.dp, YtBorder, RoundedCornerShape(12.dp))
                .clickable { onExpand() }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { cumulativeDragX = 0f },
                        onDragEnd = {
                            if (kotlin.math.abs(cumulativeDragX) > 200f) {
                                onClose()
                            }
                        },
                        onDragCancel = { cumulativeDragX = 0f }
                    ) { change, dragAmount ->
                        change.consume()
                        cumulativeDragX += dragAmount.x
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidthPx - playerWidthPx)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightPx - playerHeightPx)
                    }
                }
                .testTag("floating_mini_player"),
            color = YtSurfaceDark,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini 16:9 Live Video Player / Thumbnail Box
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
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
                                        if (video.watchPositionMs > 1000L) {
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
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Fallback poster when loading or before video renders
                    if (!isVideoPrepared || video.id.startsWith("sample_")) {
                        VideoThumbnailView(
                            video = video,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Expand indicator badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(2.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand",
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Title and Channel
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 2.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = video.displayTitle,
                        color = YtTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = video.displayChannelName,
                        color = YtTextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Play / Pause Button
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("mini_player_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Play" else "Pause",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("mini_player_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close player",
                        tint = YtTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
