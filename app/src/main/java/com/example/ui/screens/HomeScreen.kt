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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoItem
import com.example.ui.components.HomeShortsShelf
import com.example.ui.components.VideoFeedCard
import com.example.ui.components.VideoThumbnailView
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.liquidGlassSurface
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtPillActive
import com.example.ui.theme.YtPillActiveText
import com.example.ui.theme.YtPillBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    videos: List<VideoItem>,
    shortsList: List<VideoItem> = emptyList(),
    selectedFilter: String,
    isRefreshing: Boolean = false,
    isScanningMedia: Boolean = false,
    onRefresh: () -> Unit = {},
    onFilterSelected: (String) -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onShortClick: (VideoItem) -> Unit = onVideoClick,
    onEditTitleClick: (VideoItem) -> Unit,
    onToggleWatchLater: (VideoItem) -> Unit,
    onToggleSubscribe: (VideoItem) -> Unit,
    onToggleDownload: (VideoItem) -> Unit,
    onShareClick: (VideoItem) -> Unit,
    onScanStorageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterChips = listOf("All", "Videos", "Shorts", "Downloaded", "Favorites")

    val appAccentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Filter Chips Row (Screenshot 6 style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Explore Compass Pill
            Box(
                modifier = Modifier
                    .then(
                        if (glassConfig.isEnabled || glassConfig.neonEnabled) {
                            Modifier.liquidGlassSurface(
                                shape = RoundedCornerShape(12.dp),
                                config = glassConfig,
                                elevation = 3.dp
                            )
                        } else {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(YtSurfaceVariant)
                        }
                    )
                    .clickable { onFilterSelected("All") }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("filter_chip_explore"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Explore",
                    tint = YtTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Quick Mix / Shuffle Chip (Har refresh / mix par videos nayi tarteeb se mix hon)
            Box(
                modifier = Modifier
                    .then(
                        if (glassConfig.isEnabled || glassConfig.neonEnabled) {
                            Modifier.liquidGlassSurface(
                                shape = RoundedCornerShape(12.dp),
                                config = glassConfig,
                                accentColor = appAccentColor,
                                elevation = 3.dp
                            )
                        } else {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(YtSurfaceVariant)
                        }
                    )
                    .clickable { onRefresh() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("filter_chip_mix_shuffle"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Mix & Shuffle",
                        tint = appAccentColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mix 🔀",
                        color = YtTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            filterChips.forEach { chip ->
                val isSelected = selectedFilter == chip
                Box(
                    modifier = Modifier
                        .then(
                            if (glassConfig.isEnabled || glassConfig.neonEnabled) {
                                Modifier.liquidGlassSurface(
                                    shape = RoundedCornerShape(12.dp),
                                    config = glassConfig,
                                    accentColor = if (isSelected) appAccentColor else Color.White,
                                    elevation = if (isSelected) 5.dp else 2.dp
                                )
                            } else {
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) YtPillActive else YtPillBackground)
                            }
                        )
                        .clickable { onFilterSelected(chip) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("filter_chip_$chip"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chip,
                        color = if (isSelected && !glassConfig.isEnabled) YtPillActiveText else YtTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // Pull to Refresh Box (Auto-shuffles and rescans videos on every pull-down refresh)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isScanningMedia && videos.isEmpty()) {
                // Scanning state: Scanning your gallery...
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = YtRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Scanning your gallery...",
                            color = YtTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Discovering videos from device storage",
                            color = YtTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (videos.isEmpty()) {
                // Scan completed + no videos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = YtTextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No videos found",
                            color = YtTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pull down to scan your gallery or refresh.",
                            color = YtTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(containerColor = YtSurfaceVariant)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = YtTextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rescan Gallery 🔄", color = YtTextPrimary)
                        }
                    }
                }
            } else {
                val effectiveShorts = if (shortsList.isNotEmpty()) shortsList else videos.filter { it.isShort }
                val regularVideos = if (videos.any { !it.isShort }) videos.filter { !it.isShort } else videos

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("home_videos_list"),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    if (selectedFilter == "Shorts") {
                        // User selected "Shorts" filter chip: Show continuous shorts grid
                        item(key = "shorts_only_grid") {
                            HomeShortsShelf(
                                shortsList = effectiveShorts,
                                onShortClick = onShortClick
                            )
                        }
                    } else if (selectedFilter == "Videos") {
                        // User selected "Videos" filter chip: Show long videos with continuous cycle
                        val repeatCycles = 200
                        items(count = regularVideos.size * repeatCycles, key = { index -> "video_${index}_${regularVideos[index % regularVideos.size].id}" }) { index ->
                            val video = regularVideos[index % regularVideos.size]
                            VideoFeedCard(
                                video = video,
                                onClick = { onVideoClick(video) },
                                onEditTitleClick = { onEditTitleClick(video) },
                                onToggleWatchLater = { onToggleWatchLater(video) },
                                onToggleSubscribe = { onToggleSubscribe(video) },
                                onToggleDownload = { onToggleDownload(video) },
                                onShareClick = { onShareClick(video) }
                            )
                        }
                    } else {
                        // "All" / General feed (YouTube Layout requested by User):
                        // 1. TOP OF HOMEPAGE: Immediately show top Shorts shelf (6 shorts: 2 top, 2 middle, 2 bottom)
                        if (effectiveShorts.isNotEmpty()) {
                            item(key = "top_shorts_shelf_initial") {
                                val top6Shorts = generateSequence { effectiveShorts }.flatten().take(6).toList()
                                HomeShortsShelf(
                                    shortsList = top6Shorts,
                                    onShortClick = onShortClick
                                )
                            }
                        }

                        // 2. INFINITE SCROLL: 2 Videos, then 2 Shorts, then 2 Shorts (yani 6 shorts top, then 2 videos, then shorts, etc.), looping seamlessly without end
                        val virtualCycles = 500
                        val videoBlockSize = 2 // 2 videos per block as requested: "FIR do niche aur FIR do neeche... yani 6 shorts ho FIR uske niche Jo videos hoti hai FIR uske niche shorts"
                        val videoCount = if (regularVideos.isNotEmpty()) regularVideos.size else 1
                        val totalVirtualBlocks = ((videoCount * virtualCycles) / videoBlockSize).coerceAtLeast(50)

                        for (blockIndex in 0 until totalVirtualBlocks) {
                            // 2 Videos block
                            items(
                                count = videoBlockSize,
                                key = { itemIdx ->
                                    val actualIdx = (blockIndex * videoBlockSize + itemIdx) % regularVideos.size
                                    "inf_video_block_${blockIndex}_item_${itemIdx}_${regularVideos[actualIdx].id}"
                                }
                            ) { itemIdx ->
                                val actualIdx = (blockIndex * videoBlockSize + itemIdx) % regularVideos.size
                                val video = regularVideos[actualIdx]
                                VideoFeedCard(
                                    video = video,
                                    onClick = { onVideoClick(video) },
                                    onEditTitleClick = { onEditTitleClick(video) },
                                    onToggleWatchLater = { onToggleWatchLater(video) },
                                    onToggleSubscribe = { onToggleSubscribe(video) },
                                    onToggleDownload = { onToggleDownload(video) },
                                    onShareClick = { onShareClick(video) }
                                )
                            }

                            // Followed by Shorts Shelf (2-column pairs)
                            if (effectiveShorts.isNotEmpty()) {
                                item(key = "inf_shorts_shelf_after_block_$blockIndex") {
                                    val shelfShorts = generateSequence { effectiveShorts }
                                        .flatten()
                                        .drop((blockIndex + 1) * 6)
                                        .take(if (blockIndex % 2 == 0) 4 else 6)
                                        .toList()
                                    HomeShortsShelf(
                                        shortsList = shelfShorts,
                                        onShortClick = onShortClick
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
