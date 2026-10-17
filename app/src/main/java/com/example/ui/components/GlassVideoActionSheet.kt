package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoItem
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import com.example.ui.theme.liquidGlassSurface

/**
 * Universal Liquid Glass Video Action Sheet:
 * Used whenever a user taps the 3-dots button on ANY video card in Home, Shorts, Player, or Library.
 * Displays translucent glass backdrop with real depth, beveled edge highlights, and optical reflection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassVideoActionSheet(
    video: VideoItem,
    onDismiss: () -> Unit,
    onPlayNext: (() -> Unit)? = null,
    onToggleWatchLater: () -> Unit,
    onToggleDownload: () -> Unit,
    onToggleSubscribe: () -> Unit,
    onShare: () -> Unit,
    onEditTitle: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val glassConfig = LocalLiquidGlassConfig.current
    val accentColor = LocalAppAccentColor.current
    var showDetailsDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (glassConfig.isEnabled) Color.Transparent else YtSurfaceDark,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = null
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (glassConfig.isEnabled) {
                        Modifier.liquidGlassSurface(
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            config = glassConfig,
                            accentColor = accentColor,
                            elevation = 12.dp
                        )
                    } else {
                        Modifier.background(YtSurfaceDark)
                    }
                )
                .navigationBarsPadding()
                .testTag("glass_video_action_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 12.dp)
            ) {
                // Header: Drag Handle Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Video Preview Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 68.dp, height = 40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        VideoThumbnailView(
                            video = video,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(width = 68.dp, height = 40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.displayTitle,
                            color = YtTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${video.displayChannelName} • ${video.formattedDuration}",
                            color = YtTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions List
                if (onPlayNext != null) {
                    GlassActionItem(
                        icon = Icons.Outlined.PlayArrow,
                        title = "Play next in queue",
                        onClick = {
                            onDismiss()
                            onPlayNext()
                        },
                        testTag = "action_play_next"
                    )
                }

                GlassActionItem(
                    icon = if (video.isWatchLater) Icons.Outlined.BookmarkRemove else Icons.Outlined.WatchLater,
                    title = if (video.isWatchLater) "Remove from Watch Later" else "Save to Watch Later",
                    onClick = {
                        onDismiss()
                        onToggleWatchLater()
                    },
                    testTag = "action_watch_later"
                )

                GlassActionItem(
                    icon = Icons.Outlined.PlaylistAdd,
                    title = "Add to playlist",
                    onClick = {
                        onDismiss()
                        onAddToPlaylist?.invoke() ?: onToggleWatchLater()
                    },
                    testTag = "action_add_playlist"
                )

                GlassActionItem(
                    icon = if (video.isDownloaded) Icons.Outlined.DownloadDone else Icons.Outlined.Download,
                    title = if (video.isDownloaded) "Remove from Downloads" else "Save / Download Offline",
                    iconTint = if (video.isDownloaded) accentColor else Color.White,
                    onClick = {
                        onDismiss()
                        onToggleDownload()
                    },
                    testTag = "action_download"
                )

                GlassActionItem(
                    icon = Icons.Outlined.Edit,
                    title = "Set Custom Title / Rename",
                    onClick = {
                        onDismiss()
                        onEditTitle()
                    },
                    testTag = "action_rename"
                )

                GlassActionItem(
                    icon = Icons.Outlined.Subscriptions,
                    title = if (video.isSubscribed) "Unsubscribe Channel" else "Subscribe to ${video.displayChannelName}",
                    iconTint = if (video.isSubscribed) accentColor else Color.White,
                    onClick = {
                        onDismiss()
                        onToggleSubscribe()
                    },
                    testTag = "action_subscribe"
                )

                GlassActionItem(
                    icon = Icons.Outlined.Share,
                    title = "Share Video",
                    onClick = {
                        onDismiss()
                        onShare()
                    },
                    testTag = "action_share"
                )

                GlassActionItem(
                    icon = Icons.Outlined.Info,
                    title = "View File & Video Details",
                    onClick = {
                        showDetailsDialog = true
                    },
                    testTag = "action_details"
                )
            }
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = {
                Text("Video Details", color = YtTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Title: ${video.displayTitle}", color = YtTextPrimary, fontSize = 13.sp)
                    Text("Channel: ${video.displayChannelName}", color = YtTextSecondary, fontSize = 12.sp)
                    Text("Duration: ${video.formattedDuration}", color = YtTextSecondary, fontSize = 12.sp)
                    Text("Downloaded: ${if (video.isDownloaded) "Yes (Offline Available)" else "No"}", color = YtTextSecondary, fontSize = 12.sp)
                    Text("Watch Progress: ${video.watchPositionMs / 1000}s / ${video.durationMs / 1000}s", color = YtTextSecondary, fontSize = 12.sp)
                    Text("Path: ${video.uriString.take(45)}...", color = YtTextSecondary, fontSize = 11.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Close", color = accentColor)
                }
            },
            containerColor = YtSurfaceDark
        )
    }
}

@Composable
private fun GlassActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
    testTag: String = "glass_action_item"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = YtTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
