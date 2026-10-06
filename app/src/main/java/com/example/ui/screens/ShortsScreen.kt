package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.VideoItem
import com.example.ui.components.VideoThumbnailView
import com.example.ui.theme.YtAvatarTeal
import com.example.ui.theme.YtRed
import com.example.util.ScalingUtils
import com.example.util.scaled
import kotlinx.coroutines.delay

/**
 * Standard YouTube Shorts Layout:
 * - 9:16 Full-screen object-fit cover
 * - Clean unobstructed center
 * - Bottom-Right vertical Action Buttons: Like, Comment, Save, Share, Remix, Audio Thumbnail Card
 * - Bottom-Left Info Section: Channel row with avatar, @handle, white Subscribe capsule pill,
 *   compact 2-line title with edit button, hashtags, and sound ticker pill
 * - Safe area bottom padding (74dp) to avoid overlapping the bottom navigation bar
 */
@Composable
fun ShortsScreen(
    shortsList: List<VideoItem>,
    initialShortId: String? = null,
    onConsumedInitialShort: (() -> Unit)? = null,
    onToggleLike: (VideoItem) -> Unit,
    onToggleDislike: (VideoItem) -> Unit = {},
    onToggleSubscribe: (VideoItem) -> Unit,
    onOpenComments: (VideoItem) -> Unit,
    onToggleSave: (VideoItem) -> Unit = {},
    onRemixClick: (VideoItem) -> Unit = {},
    onEditTitle: (VideoItem) -> Unit,
    onShare: (VideoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shortsList.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("No shorts found. Tap '+' to create or scan gallery media!", color = Color.White)
        }
        return
    }

    val initialPage = remember(initialShortId) {
        if (!initialShortId.isNullOrBlank()) {
            val idx = shortsList.indexOfFirst { it.id == initialShortId }
            if (idx >= 0) idx else 0
        } else {
            0
        }
    }

    // Unlimited infinite scrolling for YouTube Shorts feed
    val infinitePageCount = 1_000_000
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { infinitePageCount }
    )

    LaunchedEffect(initialShortId) {
        if (!initialShortId.isNullOrBlank()) {
            val idx = shortsList.indexOfFirst { it.id == initialShortId }
            if (idx >= 0 && pagerState.currentPage % shortsList.size != idx) {
                pagerState.scrollToPage(idx)
            }
            onConsumedInitialShort?.invoke()
        }
    }

    VerticalPager(
        state = pagerState,
        key = { page -> "short_page_${page}_${shortsList[page % shortsList.size].id}" },
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_vertical_pager")
    ) { page ->
        val video = shortsList[page % shortsList.size]
        val isCurrentPage = pagerState.currentPage == page

        ShortsItemPage(
            video = video,
            pageIndex = page,
            isActive = isCurrentPage,
            onToggleLike = { onToggleLike(video) },
            onToggleSubscribe = { onToggleSubscribe(video) },
            onOpenComments = { onOpenComments(video) },
            onToggleSave = { onToggleSave(video) },
            onRemixClick = { onRemixClick(video) },
            onEditTitle = { onEditTitle(video) },
            onShare = { onShare(video) }
        )
    }
}

@Composable
private fun ShortsItemPage(
    video: VideoItem,
    pageIndex: Int,
    isActive: Boolean,
    onToggleLike: () -> Unit,
    onToggleSubscribe: () -> Unit,
    onOpenComments: () -> Unit,
    onToggleSave: () -> Unit,
    onRemixClick: () -> Unit,
    onEditTitle: () -> Unit,
    onShare: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showPauseOverlay by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Spinning disc animation for sound card
    val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPlaying = !isPlaying
                if (!video.isPhotoSlideshow) {
                    if (isPlaying) {
                        videoViewRef?.start()
                    } else {
                        videoViewRef?.pause()
                    }
                }
                showPauseOverlay = true
            }
    ) {
        if (video.isPhotoSlideshow) {
            // PHOTO-TO-VIDEO ANIMATION ENGINE (Ken Burns zoom/pan effect)
            KenBurnsPhotoSlideshow(
                video = video,
                isActive = isActive,
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // VIDEO PLAYER (Native VideoView with 9:16 Full Screen Cover)
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        try {
                            setOnErrorListener { _, _, _ -> true }
                            if (!video.id.startsWith("sample_")) {
                                setVideoURI(Uri.parse(video.uriString))
                            }
                            setOnPreparedListener { mp ->
                                mp.isLooping = (video.clipEndMs <= 0 || video.clipEndMs >= video.durationMs)
                                if (video.clipStartMs > 0) {
                                    seekTo(video.clipStartMs.toInt())
                                }
                                if (isActive && isPlaying) {
                                    start()
                                }
                            }
                        } catch (_: Exception) {}
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    videoViewRef = vv
                    if (isActive && isPlaying) {
                        if (!vv.isPlaying) {
                            try { vv.start() } catch (_: Exception) {}
                        }
                    } else {
                        if (vv.isPlaying) {
                            try { vv.pause() } catch (_: Exception) {}
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Loop 5-10s Snippet
            if (video.clipEndMs > video.clipStartMs && video.clipEndMs < video.durationMs) {
                LaunchedEffect(isActive, isPlaying) {
                    while (isActive && isPlaying) {
                        delay(250)
                        val pos = videoViewRef?.currentPosition?.toLong() ?: 0L
                        if (pos >= video.clipEndMs) {
                            videoViewRef?.seekTo(video.clipStartMs.toInt())
                        }
                    }
                }
            }

            // Fallback poster when paused or sample video
            if (!isPlaying || video.id.startsWith("sample_")) {
                VideoThumbnailView(
                    video = video,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Tap Play/Pause Feedback Overlay
        LaunchedEffect(showPauseOverlay) {
            if (showPauseOverlay) {
                delay(650)
                showPauseOverlay = false
            }
        }

        AnimatedVisibility(
            visible = showPauseOverlay,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp.scaled())
                    .clip(CircleShape)
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp.scaled())
                )
            }
        }

        // Gentle transparent bottom gradient overlay for readability without obstructing the video center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp.scaled())
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x22000000), Color(0x88000000))
                    )
                )
        )

        // Bottom Left Info Section: Channel row, Title & Hashtags, and Sound Ticker
        // Dynamically and safely positioned above the bottom navigation bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.74f)
                .padding(start = 12.dp.scaled(), end = 4.dp, bottom = 12.dp)
        ) {
            // Channel info row: Circular avatar, @username, white capsule Subscribe button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp.scaled())
                        .clip(CircleShape)
                        .background(YtAvatarTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = video.displayChannelName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp.scaled()
                    )
                }

                Spacer(modifier = Modifier.width(7.dp.scaled()))

                Text(
                    text = "@${video.displayChannelName.replace(" ", "").lowercase()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp.scaled(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp.scaled()))

                // White capsule Subscribe pill button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (video.isSubscribed) Color(0x44FFFFFF) else Color.White)
                        .clickable { onToggleSubscribe() }
                        .padding(horizontal = 10.dp.scaled(), vertical = 4.dp.scaled())
                        .testTag("shorts_subscribe_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (video.isSubscribed) "Subscribed" else "Subscribe",
                        color = if (video.isSubscribed) Color.White else Color.Black,
                        fontSize = 11.sp.scaled(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp.scaled()))

            // Video Description / Title (Compact, max 2 lines)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = video.displayTitle,
                    color = Color.White,
                    fontSize = 13.sp.scaled(),
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp.scaled(),
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(5.dp.scaled()))

                // Edit Title Icon
                Box(
                    modifier = Modifier
                        .size(22.dp.scaled())
                        .clip(CircleShape)
                        .background(Color(0x44FFFFFF))
                        .clickable { onEditTitle() }
                        .testTag("shorts_edit_title_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Title",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp.scaled())
                    )
                }
            }

            // Hashtags (if available)
            if (video.hashtags.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp.scaled()))
                Text(
                    text = video.hashtags,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp.scaled(),
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(3.dp.scaled()))

            // Audio ticker bar: Dark semi-transparent pill with music note and sound name
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x66000000))
                    .padding(horizontal = 7.dp.scaled(), vertical = 3.dp.scaled())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp.scaled())
                    )
                    Spacer(modifier = Modifier.width(4.dp.scaled()))
                    Text(
                        text = if (video.isPhotoSlideshow) "Original sound - Photo Showcase Beat" else "Original sound - ${video.displayChannelName}",
                        color = Color.White,
                        fontSize = 10.sp.scaled(),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Right Action Buttons Bar (Safely positioned above bottom navigation bar, aligned with info section)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp.scaled(), bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp.scaled())
        ) {
            // 1. Heart / Like Icon with live real-time count from database
            ShortsActionButton(
                icon = if (video.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                label = video.formattedLikesCount,
                tint = if (video.isLiked) YtRed else Color.White,
                testTag = "shorts_like_button",
                onClick = onToggleLike
            )

            // 2. Comment Icon with live real-time count from database
            ShortsActionButton(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = video.formattedCommentsCount,
                tint = Color.White,
                testTag = "shorts_comment_button",
                onClick = onOpenComments
            )

            // 3. Bookmark / Save Icon with live watch later persistence
            ShortsActionButton(
                icon = if (video.isWatchLater) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                label = "Save",
                tint = if (video.isWatchLater) Color(0xFF3EA6FF) else Color.White,
                testTag = "shorts_save_button",
                onClick = onToggleSave
            )

            // 4. Share Icon (curved arrow)
            ShortsActionButton(
                icon = Icons.Outlined.Share,
                label = "Share",
                tint = Color.White,
                testTag = "shorts_share_button",
                onClick = onShare
            )

            // 5. Remix Icon
            ShortsActionButton(
                icon = Icons.Default.Repeat,
                label = "Remix",
                tint = Color.White,
                testTag = "shorts_remix_button",
                onClick = onRemixClick
            )

            // 6. Audio / Sound Thumbnail Card at the very bottom right
            Box(
                modifier = Modifier
                    .size(32.dp.scaled())
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF222222))
                    .border(1.2.dp.scaled(), Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                    .rotate(rotation)
                    .clickable { onRemixClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp.scaled())
                        .clip(CircleShape)
                        .background(YtRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Sound Album",
                        tint = Color.White,
                        modifier = Modifier.size(9.dp.scaled())
                    )
                }
            }
        }
    }

    DisposableEffect(pageIndex, video.id) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }
}

/**
 * PHOTO ANIMATOR / SLIDESHOW ENGINE:
 * Automatically groups local photos and animates them with smooth Ken Burns zoom & pan transitions.
 */
@Composable
private fun KenBurnsPhotoSlideshow(
    video: VideoItem,
    isActive: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photoList = remember(video.photoUris, video.uriString) {
        if (video.photoUris.isNotEmpty()) video.photoUris else listOf(video.uriString)
    }

    var currentPhotoIndex by remember { mutableIntStateOf(0) }

    // Cycle photos every 2.5 seconds when active and playing
    LaunchedEffect(isActive, isPlaying, photoList.size) {
        if (photoList.size > 1 && isActive && isPlaying) {
            while (true) {
                delay(2500)
                currentPhotoIndex = (currentPhotoIndex + 1) % photoList.size
            }
        }
    }

    // Ken Burns Zoom/Pan Animation
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns_zoom")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_scale"
    )

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // Story progress indicators at top
        if (photoList.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                photoList.forEachIndexed { idx, _ ->
                    val progress = when {
                        idx < currentPhotoIndex -> 1f
                        idx == currentPhotoIndex -> 0.7f
                        else -> 0f
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color(0x55FFFFFF)
                    )
                }
            }
        }

        // Active Photo with Ken Burns Effect
        val currentPhotoUri = photoList.getOrNull(currentPhotoIndex) ?: video.uriString

        AnimatedContent(
            targetState = currentPhotoUri,
            transitionSpec = { fadeIn(tween(600)) togetherWith fadeOut(tween(600)) },
            label = "photo_crossfade"
        ) { photoUri ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }
}

/**
 * Compact, sleek action button for Shorts:
 * - Icon: 24dp sleek pure white vector
 * - Small compact text label underneath (11sp, SemiBold)
 */
@Composable
private fun ShortsActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp.scaled())
                .clip(CircleShape)
                .background(Color(0x22000000)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp.scaled())
            )
        }
        Spacer(modifier = Modifier.height(2.dp.scaled()))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp.scaled(),
            fontWeight = FontWeight.SemiBold
        )
    }
}
