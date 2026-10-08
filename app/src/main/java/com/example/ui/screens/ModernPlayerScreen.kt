package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.VideoItem
import com.example.ui.components.VideoThumbnailView
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtAvatarTeal
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtPillActive
import com.example.ui.theme.YtPillActiveText
import com.example.ui.theme.YtPillBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceHigher
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPlayerScreen(
    video: VideoItem,
    upNextVideos: List<VideoItem>,
    shortsList: List<VideoItem> = emptyList(),
    onClose: () -> Unit,
    onEditTitleClick: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDislike: () -> Unit,
    onToggleSubscribe: () -> Unit,
    onToggleDownload: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit,
    onSelectUpNext: (VideoItem) -> Unit,
    onShortClick: ((VideoItem) -> Unit)? = null,
    onProgressUpdate: (Long) -> Unit,
    onMinimize: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableLongStateOf(video.watchPositionMs) }
    var totalDurationMs by remember { mutableLongStateOf(video.durationMs.coerceAtLeast(60000L)) }
    var showControls by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showSpeedOverlay by remember { mutableStateOf(false) }
    var speedFeedbackText by remember { mutableStateOf<String?>(null) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var isSpeedBoosting by remember { mutableStateOf(false) }

    val onSpeedSelected: (Float) -> Unit = { speed ->
        playbackSpeed = speed
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val params = mediaPlayerRef?.playbackParams ?: android.media.PlaybackParams()
                params.speed = speed
                mediaPlayerRef?.playbackParams = params
            }
        } catch (_: Exception) {}
        val speedStr = when (speed) {
            0.5f -> "0.5x"
            1.0f -> "1x"
            1.5f -> "1.5x"
            2.0f -> "2x"
            3.0f -> "3x"
            else -> "${speed}x"
        }
        speedFeedbackText = "$speedStr Speed"
        showSpeedOverlay = false
    }

    // Double tap feedback
    var skipFeedbackText by remember { mutableStateOf<String?>(null) }

    // Handle back press - minimize to floating PiP
    BackHandler {
        if (isFullscreen) {
            isFullscreen = false
        } else {
            onProgressUpdate(currentPositionMs)
            if (onMinimize != null) onMinimize() else onClose()
        }
    }

    val listState = rememberLazyListState()

    // Deduplicated recommendations without the currently playing video
    val uniqueUpNext = remember(upNextVideos, video.id) {
        upNextVideos
            .filter { it.id != video.id }
            .distinctBy { it.id }
    }

    // Auto-reset playback states and scroll to top when a new video is clicked (from Up Next or Search)
    LaunchedEffect(video.id) {
        currentPositionMs = video.watchPositionMs
        totalDurationMs = video.durationMs.coerceAtLeast(60000L)
        isPlaying = true
        showControls = true
        playbackSpeed = 1.0f
        try {
            listState.scrollToItem(0)
        } catch (_: Exception) {}
    }

    // Timer to update current position
    LaunchedEffect(isPlaying, video.id) {
        while (true) {
            delay(500)
            if (isPlaying) {
                if (videoViewRef != null && mediaPlayerRef != null) {
                    try {
                        val pos = videoViewRef!!.currentPosition.toLong()
                        if (pos > 0) {
                            currentPositionMs = pos
                            val dur = videoViewRef!!.duration.toLong()
                            if (dur > 0) totalDurationMs = dur
                        }
                    } catch (_: Exception) {}
                } else if (video.id.startsWith("sample_")) {
                    currentPositionMs = (currentPositionMs + 500).let {
                        if (it >= totalDurationMs) 0L else it
                    }
                }
            }
        }
    }

    // Auto-hide controls after 3 seconds
    LaunchedEffect(showControls) {
        if (showControls && isPlaying && !isLocked) {
            delay(3500)
            showControls = false
        }
    }

    // Offline Comments (Local Storage / Preferences / State)
    val sharedPrefs = remember(context) {
        context.getSharedPreferences("offline_yt_comments", android.content.Context.MODE_PRIVATE)
    }
    var localCommentsList by remember(video.id) {
        val savedSet = sharedPrefs.getStringSet("comments_${video.id}", null) ?: emptySet()
        val defaultList = if (video.userNotes.isNotBlank()) listOf(video.userNotes) else listOf("Vote for moni di bf face reveal 💖", "Amazing offline playback! 🔥")
        mutableStateOf((defaultList + savedSet.toList()).distinct())
    }
    var commentInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YtDarkBackground)
    ) {
        // Top 16:9 Video Player Container (Subtle rounded corners matching YouTube player UI)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isFullscreen) Modifier.fillMaxSize()
                    else Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .aspectRatio(16f / 9f)
                )
                .background(Color.Black)
                .pointerInput(playbackSpeed) {
                    detectTapGestures(
                        onPress = {
                            var isLongPressed = false
                            val pressJob = coroutineScope.launch {
                                delay(300)
                                isLongPressed = true
                                isSpeedBoosting = true
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        val params = mediaPlayerRef?.playbackParams ?: android.media.PlaybackParams()
                                        params.speed = 2.0f
                                        mediaPlayerRef?.playbackParams = params
                                    }
                                } catch (_: Exception) {}
                            }
                            try {
                                tryAwaitRelease()
                            } finally {
                                pressJob.cancel()
                                if (isLongPressed) {
                                    isSpeedBoosting = false
                                    try {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                            val params = mediaPlayerRef?.playbackParams ?: android.media.PlaybackParams()
                                            params.speed = playbackSpeed
                                            mediaPlayerRef?.playbackParams = params
                                        }
                                    } catch (_: Exception) {}
                                }
                            }
                        },
                        onTap = {
                            showControls = !showControls
                        },
                        onDoubleTap = { offset ->
                            val isRightSide = offset.x > size.width / 2
                            if (isRightSide) {
                                // Skip forward 10s
                                val newPos = (currentPositionMs + 10000).coerceAtMost(totalDurationMs)
                                currentPositionMs = newPos
                                try { videoViewRef?.seekTo(newPos.toInt()) } catch (_: Exception) {}
                                skipFeedbackText = "+10s"
                            } else {
                                // Skip backward 10s
                                val newPos = (currentPositionMs - 10000).coerceAtLeast(0L)
                                currentPositionMs = newPos
                                try { videoViewRef?.seekTo(newPos.toInt()) } catch (_: Exception) {}
                                skipFeedbackText = "-10s"
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (dragAmount.y > 25f && !isFullscreen) {
                            change.consume()
                            onProgressUpdate(currentPositionMs)
                            if (onMinimize != null) onMinimize() else onClose()
                        }
                    }
                }
                .testTag("modern_video_player_box")
        ) {
            // AndroidView wrapping native VideoView with dynamic video reloading on click
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
                                mediaPlayerRef = mp
                                mp.isLooping = true
                                val dur = mp.duration.toLong()
                                if (dur > 0) totalDurationMs = dur
                                if (video.watchPositionMs > 1000L) {
                                    seekTo(video.watchPositionMs.toInt())
                                }
                                if (isPlaying) start()
                            }
                        } catch (_: Exception) {}
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    videoViewRef = vv
                    // When video changes, stop current playback and load new video source
                    val currentTag = vv.tag as? String
                    if (currentTag != video.id) {
                        vv.tag = video.id
                        try {
                            vv.stopPlayback()
                            mediaPlayerRef = null
                            if (!video.id.startsWith("sample_")) {
                                vv.setVideoURI(Uri.parse(video.uriString))
                            }
                        } catch (_: Exception) {}
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Backdrop poster if sample or video not yet rendered
            if (mediaPlayerRef == null || video.id.startsWith("sample_")) {
                VideoThumbnailView(
                    video = video,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Double Tap Ripple Animation Indicator
            LaunchedEffect(skipFeedbackText) {
                if (skipFeedbackText != null) {
                    delay(800)
                    skipFeedbackText = null
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = skipFeedbackText != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = skipFeedbackText ?: "",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Long Press 2x Speed Gesture Top Overlay Indicator
            androidx.compose.animation.AnimatedVisibility(
                visible = isSpeedBoosting,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xD9000000))
                        .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("player_2x_speed_indicator"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            tint = YtRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2x Speed",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Controls Overlay
            androidx.compose.animation.AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x77000000))
                ) {
                    // Top Player Bar (Official YouTube mobile style)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Top-left: Collapse arrow (v)
                        IconButton(
                            onClick = {
                                onProgressUpdate(currentPositionMs)
                                if (onMinimize != null) onMinimize() else onClose()
                            },
                            modifier = Modifier.testTag("player_minimize_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Collapse video player",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Top-right: Cast icon
                        IconButton(
                            onClick = {
                                speedFeedbackText = "Connecting Cast..."
                            },
                            modifier = Modifier.testTag("player_cast_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cast,
                                contentDescription = "Cast",
                                tint = Color.White
                            )
                        }

                        // Top-right: CC icon
                        IconButton(
                            onClick = {
                                speedFeedbackText = "Captions (CC) toggled"
                            },
                            modifier = Modifier.testTag("player_cc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ClosedCaption,
                                contentDescription = "Subtitles/CC",
                                tint = Color.White
                            )
                        }

                        // Top-right: Settings gear (⚙️)
                        IconButton(
                            onClick = {
                                showSpeedOverlay = true
                            },
                            modifier = Modifier.testTag("player_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }

                        // Top-right: Close button (✕)
                        IconButton(
                            onClick = {
                                onProgressUpdate(currentPositionMs)
                                onClose()
                            },
                            modifier = Modifier.testTag("player_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close player",
                                tint = Color.White
                            )
                        }
                    }

                    if (!isLocked) {
                        // Center Play / Pause and Rewind/Forward (-60s / +60s) Controls
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            // Rewind (-60s button)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        val newPos = (currentPositionMs - 60000).coerceAtLeast(0L)
                                        currentPositionMs = newPos
                                        try { videoViewRef?.seekTo(newPos.toInt()) } catch (_: Exception) {}
                                        skipFeedbackText = "-60s"
                                    }
                                    .padding(8.dp)
                                    .testTag("player_rewind_60s_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastRewind,
                                    contentDescription = "Rewind 60s",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "-60s",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Center: Play / Pause (▶ / ❚❚)
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x99000000))
                                    .clickable {
                                        isPlaying = !isPlaying
                                        if (isPlaying) {
                                            videoViewRef?.start()
                                        } else {
                                            videoViewRef?.pause()
                                        }
                                    }
                                    .testTag("player_play_pause_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            // Forward (+60s button)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        val newPos = (currentPositionMs + 60000).coerceAtMost(totalDurationMs)
                                        currentPositionMs = newPos
                                        try { videoViewRef?.seekTo(newPos.toInt()) } catch (_: Exception) {}
                                        skipFeedbackText = "+60s"
                                    }
                                    .padding(8.dp)
                                    .testTag("player_forward_60s_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Forward 60s",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "+60s",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Bottom Player Bar: Red Progress Seekbar with Timestamp Display
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${formatTime(currentPositionMs)} / ${formatTime(totalDurationMs)}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.testTag("player_time_display")
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (playbackSpeed != 1.0f) {
                                    Text(
                                        text = "${playbackSpeed}x",
                                        color = YtRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                IconButton(
                                    onClick = { isFullscreen = !isFullscreen },
                                    modifier = Modifier.size(28.dp).testTag("player_fullscreen_button")
                                ) {
                                    Icon(
                                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Slider(
                                value = if (totalDurationMs > 0) (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f,
                                onValueChange = { frac ->
                                    val target = (frac * totalDurationMs).toLong()
                                    currentPositionMs = target
                                    try { videoViewRef?.seekTo(target.toInt()) } catch (_: Exception) {}
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = YtRed,
                                    activeTrackColor = YtRed,
                                    inactiveTrackColor = Color(0x66FFFFFF)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .testTag("player_seekbar")
                            )
                        }
                    }
                }
            }

            // Speed Change Animated Feedback Badge
            LaunchedEffect(speedFeedbackText) {
                if (speedFeedbackText != null) {
                    delay(800)
                    speedFeedbackText = null
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = speedFeedbackText != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xEE1E1E1E))
                        .border(1.dp, YtRed, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = YtRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = speedFeedbackText ?: "",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dedicated Playback Speed Control Overlay (0.5x, 1x, 1.5x, 2x, 3x)
            androidx.compose.animation.AnimatedVisibility(
                visible = showSpeedOverlay,
                enter = fadeIn() + scaleIn(initialScale = 0.95f),
                exit = fadeOut() + scaleOut(targetScale = 0.95f),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xD9000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showSpeedOverlay = false }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E1E1E))
                            .border(1.dp, Color(0xFF383838), RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* Prevent click through */ }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header with Title and Close Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = YtRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Playback Speed",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .clickable { showSpeedOverlay = false }
                                    .testTag("speed_overlay_close_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Speed Options: 0.5x, 1x, 1.5x, 2x, 3x
                        val speedList = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            speedList.forEach { speed ->
                                val isSelected = playbackSpeed == speed
                                val label = when (speed) {
                                    0.5f -> "0.5x"
                                    1.0f -> "1x"
                                    1.5f -> "1.5x"
                                    2.0f -> "2x"
                                    3.0f -> "3x"
                                    else -> "${speed}x"
                                }
                                val subLabel = when (speed) {
                                    0.5f -> "Slow"
                                    1.0f -> "Normal"
                                    1.5f -> "Fast"
                                    2.0f -> "2x"
                                    3.0f -> "3x"
                                    else -> ""
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) YtRed else Color(0xFF2A2A2A))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) Color.White else Color(0xFF404040),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSpeedSelected(speed) }
                                        .padding(vertical = 10.dp)
                                        .testTag("speed_pill_$label"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = label,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = subLabel,
                                            color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFFAAAAAA),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tap to toggle speed: 0.5x • 1x • 1.5x • 2x • 3x",
                            color = Color(0xFF888888),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // When fullscreen, don't show the bottom scroll content
        if (!isFullscreen) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("player_details_column"),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
            ) {
                // Video Title with '+' / Edit Icon right next to it! (Screenshot 3 & Requirement 3)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = video.displayTitle,
                            color = YtTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Custom Title Edit / Plus button
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(YtSurfaceVariant)
                                .clickable { onEditTitleClick() }
                                .testTag("player_edit_title_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Set Custom Title",
                                tint = YtTextPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                // Subtitle Line (Screenshot 2: "@Channel • 25k likes • 979k views • 1 day ago • #prank ...more")
                item {
                    Text(
                        text = "@${video.displayChannelName.replace(" ", "")} • 25k likes • ${video.viewsCount} • ${video.uploadedAgo} • #offline ...more",
                        color = YtTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Channel Row with Subscribe Pill Button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(YtAvatarPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = video.displayChannelName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = video.displayChannelName,
                                    color = YtTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = YtTextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "128K subscribers",
                                color = YtTextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Subscribe Pill (Screenshot 2 style)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (video.isSubscribed) YtSurfaceVariant else YtPillActive)
                                .clickable { onToggleSubscribe() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("player_subscribe_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (video.isSubscribed) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = YtTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Subscribed",
                                    color = YtTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "Subscribe",
                                    color = YtPillActiveText,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Modern Pill Action Buttons Row (Like/Dislike, Share, Download, Clip, More)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Combined Like / Dislike Pill (Screenshot 2)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(YtSurfaceVariant)
                                .height(36.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { onToggleLike() }
                                    .testTag("player_like_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (video.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    contentDescription = "Like",
                                    tint = if (video.isLiked) YtRed else YtTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (video.isLiked) "Liked" else "25K",
                                    color = YtTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(18.dp)
                                    .background(YtBorder)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .clickable { onToggleDislike() }
                                    .testTag("player_dislike_button")
                            ) {
                                Icon(
                                    imageVector = if (video.isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                    contentDescription = "Dislike",
                                    tint = YtTextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // 2. Share Pill
                        PlayerActionButton(
                            icon = Icons.Default.Share,
                            label = "Share",
                            testTag = "player_share_button",
                            onClick = onShare
                        )

                        // 3. Download / Saved Pill
                        PlayerActionButton(
                            icon = Icons.Default.Download,
                            label = if (video.isDownloaded) "Downloaded" else "Download",
                            active = video.isDownloaded,
                            testTag = "player_download_button",
                            onClick = onToggleDownload
                        )

                        // 4. Clip Pill
                        PlayerActionButton(
                            icon = Icons.Default.ContentCut,
                            label = "Clip",
                            testTag = "player_clip_button",
                            onClick = { /* Clip */ }
                        )

                        // 5. 3-dots Pill
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(YtSurfaceVariant)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More actions",
                                tint = YtTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 3. OFFLINE COMMENTS CARD (Local Storage / Offline Comments)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(YtSurfaceVariant)
                            .padding(12.dp)
                            .testTag("player_comments_card")
                    ) {
                        Column {
                            // Total Comments Count & Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenComments() },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Comments",
                                    color = YtTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${localCommentsList.size + 142}",
                                    color = YtTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "View all",
                                    color = YtTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Local Storage Input Field ("Add a comment...")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E88E5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("U", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = commentInputText,
                                    onValueChange = { commentInputText = it },
                                    placeholder = {
                                        Text("Add a comment...", fontSize = 12.sp, color = YtTextSecondary)
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = YtRed,
                                        unfocusedBorderColor = Color(0x33FFFFFF),
                                        focusedTextColor = YtTextPrimary,
                                        unfocusedTextColor = YtTextPrimary,
                                        cursorColor = YtRed
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("player_comment_input")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (commentInputText.isNotBlank()) {
                                            val newComment = commentInputText.trim()
                                            val updated = (listOf(newComment) + localCommentsList).distinct()
                                            localCommentsList = updated
                                            val currentSaved = sharedPrefs.getStringSet("comments_${video.id}", null) ?: emptySet()
                                            sharedPrefs.edit()
                                                .putStringSet("comments_${video.id}", currentSaved + newComment)
                                                .apply()
                                            commentInputText = ""
                                        }
                                    },
                                    enabled = commentInputText.isNotBlank(),
                                    modifier = Modifier.size(36.dp).testTag("player_comment_send_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Post Comment",
                                        tint = if (commentInputText.isNotBlank()) YtRed else Color(0x44FFFFFF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Dynamic list showing locally saved comments under active video
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                localCommentsList.take(3).forEachIndexed { idx, commentItem ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (idx % 2 == 0) Color(0xFF2E7D32) else Color(0xFFE65100)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (idx % 2 == 0) "B" else "A",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = commentItem,
                                                color = YtTextPrimary,
                                                fontSize = 12.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. SHORTS FEED CAROUSEL (Horizontal Scroll with 9:16 vertical cards)
                val effectiveShorts = if (shortsList.isNotEmpty()) shortsList else upNextVideos.filter { it.isShort }
                if (effectiveShorts.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            // Header with Red Shorts Icon Badge + Title
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(YtRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⚡", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Shorts",
                                    color = YtTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Horizontal scrollable carousel with 9:16 vertical cards
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 14.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                effectiveShorts.take(6).forEach { shortItem ->
                                    Box(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .aspectRatio(9f / 16f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(YtSurfaceDark)
                                            .clickable {
                                                if (onShortClick != null) onShortClick(shortItem) else onSelectUpNext(shortItem)
                                            }
                                    ) {
                                        VideoThumbnailView(
                                            video = shortItem,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Dark gradient bottom overlay scrim
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color(0x33000000), Color(0xEE000000))
                                                    )
                                                )
                                        )

                                        // 3-dots menu button in top right corner
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0x66000000)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "Options",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        // Title and Views Overlay on bottom
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = shortItem.displayTitle,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = shortItem.viewsCount,
                                                color = Color(0xCCFFFFFF),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. CATEGORY FILTER CHIPS ROW (Directly below Shorts Carousel)
                item {
                    val filterChips = listOf("All", "From channel", "Comedy", "Related")
                    var selectedChip by remember { mutableStateOf("All") }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filterChips.forEach { chip ->
                            val isSelected = selectedChip == chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) YtTextPrimary else YtSurfaceVariant)
                                    .clickable { selectedChip = chip }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = chip,
                                    color = if (isSelected) Color.Black else YtTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 6. "Up Next" Header
                item {
                    Text(
                        text = "Up Next",
                        color = YtTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                // Up Next Recommendations List
                items(uniqueUpNext, key = { it.id }) { nextVideo ->
                    UpNextVideoCard(
                        video = nextVideo,
                        onClick = { onSelectUpNext(nextVideo) }
                    )
                }
            }
        }
    }

    DisposableEffect(video.id) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }
}

@Composable
private fun PlayerActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (active) Color(0xFF383838) else YtSurfaceVariant)
            .height(36.dp)
            .clickable { onClick() }
            .padding(horizontal = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) YtRed else YtTextPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = YtTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun UpNextVideoCard(
    video: VideoItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
            .testTag("up_next_card_${video.id}")
    ) {
        // Full-width 16:9 Thumbnail on top with Duration Badge at bottom-right
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            VideoThumbnailView(
                video = video,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Duration badge overlay at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = video.formattedDuration,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Details Row below thumbnail: Channel Avatar, Title, Channel Name, Views, 3-dots Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(YtAvatarPurple),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.displayChannelName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Channel / Views Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.displayTitle,
                    color = YtTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${video.displayChannelName} • ${video.viewsCount} • ${video.uploadedAgo}",
                    color = YtTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3-dot Option Menu
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = YtTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "0:00"
    val totalSec = millis / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    val hr = min / 60
    return if (hr > 0) {
        String.format("%d:%02d:%02d", hr, min % 60, sec)
    } else {
        String.format("%d:%02d", min, sec)
    }
}
