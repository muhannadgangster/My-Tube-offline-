package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.data.VideoItem
import com.example.ui.components.VideoThumbnailView
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtPillBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

@Composable
fun LibraryScreen(
    historyVideos: List<VideoItem>,
    watchLaterVideos: List<VideoItem>,
    downloadedVideos: List<VideoItem>,
    likedVideos: List<VideoItem>,
    allVideos: List<VideoItem>,
    userName: String = "Muhannad Murtaza",
    userHandle: String = "@muhannad",
    userAvatarUri: String? = null,
    onEditProfileClick: () -> Unit = {},
    onVideoClick: (VideoItem) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(YtDarkBackground)
            .testTag("library_screen_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Single Header Row - Top of 'You' Tab Screen (Matches YouTabHeader.js layout)
        item {
            YouTabHeader(
                onAccountClick = onEditProfileClick,
                onNotificationsClick = onNotificationsClick,
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick
            )
        }

        // Profile Section with Editable Profile (Screenshot 1 & Offline Profile Edit)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditProfileClick() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Custom Avatar Image or Letter
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(YtAvatarPurple)
                        .clickable { onEditProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (!userAvatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = userAvatarUri,
                            contentDescription = "Profile avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase().ifBlank { "M" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        color = YtTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userHandle,
                        color = YtTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Action Pills under profile: "Edit profile", "View channel", & "Offline Status"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Edit Profile Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(YtSurfaceVariant)
                        .clickable { onEditProfileClick() }
                        .testTag("edit_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Edit profile",
                        color = YtTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // View Channel Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(YtSurfaceVariant)
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View channel",
                        color = YtTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Offline Status / Premium
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(YtSurfaceVariant)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Offline Pro",
                        color = YtTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // History Shelf Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History",
                        color = YtTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View all history",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (historyVideos.isEmpty()) {
                    Text(
                        text = "Videos you watch will show up here",
                        color = YtTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        historyVideos.forEach { video ->
                            HistoryVideoCard(
                                video = video,
                                onClick = { onVideoClick(video) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Playlists / Library Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playlists",
                        color = YtTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 1. Watch Later
                LibraryItemRow(
                    icon = Icons.Default.WatchLater,
                    title = "Watch later",
                    subtitle = "${watchLaterVideos.size} videos • Private",
                    testTag = "library_watch_later",
                    onClick = {
                        if (watchLaterVideos.isNotEmpty()) {
                            onVideoClick(watchLaterVideos.first())
                        }
                    }
                )

                // 2. Downloads
                LibraryItemRow(
                    icon = Icons.Default.Download,
                    title = "Downloads",
                    subtitle = "${downloadedVideos.size} videos • Available offline",
                    testTag = "library_downloads",
                    onClick = {
                        if (downloadedVideos.isNotEmpty()) {
                            onVideoClick(downloadedVideos.first())
                        }
                    }
                )

                // 3. Liked videos
                LibraryItemRow(
                    icon = Icons.Default.ThumbUp,
                    title = "Liked videos",
                    subtitle = "${likedVideos.size} videos",
                    testTag = "library_liked_videos",
                    onClick = {
                        if (likedVideos.isNotEmpty()) {
                            onVideoClick(likedVideos.first())
                        }
                    }
                )

                // 4. Your videos
                LibraryItemRow(
                    icon = Icons.Default.VideoLibrary,
                    title = "Your videos",
                    subtitle = "${allVideos.size} local files loaded",
                    testTag = "library_all_videos",
                    onClick = {
                        if (allVideos.isNotEmpty()) {
                            onVideoClick(allVideos.first())
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryVideoCard(
    video: VideoItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("history_video_${video.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            VideoThumbnailView(
                video = video,
                modifier = Modifier.matchParentSize()
            )

            // Duration badge overlay at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = video.formattedDuration,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Red Progress Bar
            LinearProgressIndicator(
                progress = { video.watchProgressFraction.coerceAtLeast(0.15f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .align(Alignment.BottomCenter),
                color = YtRed,
                trackColor = Color(0x66000000)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = video.displayTitle,
            color = YtTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = video.displayChannelName,
            color = YtTextSecondary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LibraryItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(YtSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = YtTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = YtTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = YtTextSecondary,
                fontSize = 12.sp
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = YtTextSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Single Header Component for You Tab Screen (Matches YouTabHeader.js specification):
 * - Far-Left: Accounts Switcher Dropdown button
 * - Far-Right: Single Row of 3 Action Icons Only:
 *   1. Notification Bell
 *   2. Search Icon
 *   3. Settings Gear Icon
 * - Pure Dark theme background (#000000)
 */
@Composable
fun YouTabHeader(
    onAccountClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFF000000))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("you_tab_header"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // FAR-LEFT: Accounts Switcher Dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF272727))
                .clickable { onAccountClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("you_accounts_pill")
        ) {
            Text(
                text = "Accounts",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Switch accounts",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        // FAR-RIGHT: Single Row of Action Icons Only (1. Bell, 2. Search, 3. Settings)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("you_right_icons_group")
        ) {
            // 1. Notification Bell
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("you_notification_button")
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = YtRed,
                            contentColor = Color.White,
                            modifier = Modifier.size(14.dp)
                        ) {
                            Text("1", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 2. Search Icon
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("you_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            // 3. Settings Gear Icon
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("you_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
