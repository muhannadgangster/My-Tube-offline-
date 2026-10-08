package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoItem
import com.example.ui.components.VideoThumbnailView
import com.example.ui.theme.YtAvatarTeal
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

/**
 * Complete Dedicated Search Screen:
 * - Top Search Bar with back arrow, text input, clear action, and mic button
 * - Floating action Mic button in bottom-right corner for quick voice input
 * - Android Native Voice Search (RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
 * - Recent Search History with left clock, bold keyword, video preview thumbnail, and North-West arrow
 * - Instant local results filtering with YouTube-style video cards
 */
@Composable
fun SearchScreen(
    searchQuery: String,
    searchHistory: List<String>,
    allVideos: List<VideoItem>,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onRemoveHistoryItem: (String) -> Unit,
    onClearHistory: () -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    // Native Speech-to-Text Voice Recognition Launcher
    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenWords = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenWords.isNullOrBlank()) {
                onQueryChange(spokenWords)
                onSubmitSearch(spokenWords)
            }
        }
    }

    fun launchVoiceSearch() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search videos...")
            }
            voiceSearchLauncher.launch(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Voice recognition service not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-focus search input when opening
    LaunchedEffect(Unit) {
        if (searchQuery.isEmpty()) {
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    BackHandler {
        onBack()
    }

    // Filter local videos matching search query
    val matchingVideos = remember(searchQuery, allVideos) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allVideos.filter { video ->
                video.displayTitle.contains(searchQuery, ignoreCase = true) ||
                        video.displayChannelName.contains(searchQuery, ignoreCase = true) ||
                        video.hashtags.contains(searchQuery, ignoreCase = true) ||
                        video.userNotes.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(YtDarkBackground)
            .statusBarsPadding()
            .testTag("search_screen_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("search_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Search Input Field Box
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(YtSurfaceVariant)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .testTag("search_screen_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = YtTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(YtRed),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    onSubmitSearch(searchQuery)
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search videos...",
                                    color = YtTextSecondary,
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear button
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = YtTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Top Right Mic Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YtSurfaceVariant)
                        .clickable { launchVoiceSearch() }
                        .testTag("top_bar_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(color = YtBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

            // Content: Recent Search History OR Search Results
            if (searchQuery.isBlank()) {
                // 2. Recent Search History List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    if (searchHistory.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent searches",
                                    color = YtTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                TextButton(
                                    onClick = onClearHistory,
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(
                                        text = "Clear all",
                                        color = YtRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        items(searchHistory, key = { it }) { queryItem ->
                            // Find an optional preview video matching this query keyword
                            val previewVideo = remember(queryItem, allVideos) {
                                allVideos.firstOrNull {
                                    it.displayTitle.contains(queryItem, ignoreCase = true) ||
                                            it.displayChannelName.contains(queryItem, ignoreCase = true) ||
                                            it.hashtags.contains(queryItem, ignoreCase = true)
                                }
                            }

                            SearchHistoryItem(
                                query = queryItem,
                                previewVideo = previewVideo,
                                onSelect = {
                                    onQueryChange(queryItem)
                                    onSubmitSearch(queryItem)
                                },
                                onCopyQuery = {
                                    onQueryChange(queryItem)
                                },
                                onRemove = {
                                    onRemoveHistoryItem(queryItem)
                                }
                            )
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = YtTextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No recent searches",
                                        color = YtTextSecondary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try searching video titles, creators, or tags",
                                        color = YtTextSecondary.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 3. Search Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${matchingVideos.size} result${if (matchingVideos.size == 1) "" else "s"} for \"$searchQuery\"",
                                color = YtTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    if (matchingVideos.isNotEmpty()) {
                        items(matchingVideos, key = { it.id }) { video ->
                            SearchResultVideoCard(
                                video = video,
                                onClick = {
                                    onSubmitSearch(searchQuery)
                                    onVideoClick(video)
                                }
                            )
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 70.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = YtTextSecondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "No videos found for \"$searchQuery\"",
                                        color = YtTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Try searching for keywords like 'Shorts', 'Nature', 'Trending', or creator names",
                                        color = YtTextSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Floating Action Mic Button in Bottom-Right Corner
        FloatingActionButton(
            onClick = { launchVoiceSearch() },
            containerColor = YtRed,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 24.dp)
                .size(56.dp)
                .testTag("floating_mic_button")
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Search",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

/**
 * Recent Search History Item:
 * - Left clock / history icon
 * - Search keyword text in bold crisp font
 * - Right-side video preview thumbnail if available
 * - North-west arrow icon to easily copy the query into the search bar
 */
@Composable
private fun SearchHistoryItem(
    query: String,
    previewVideo: VideoItem?,
    onSelect: () -> Unit,
    onCopyQuery: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("search_history_row_$query"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Clock / History Icon
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = YtTextSecondary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Search Keyword in Bold Crisp Font
        Text(
            text = query,
            color = YtTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Right-side Video Preview Thumbnail if available
        if (previewVideo != null) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
            ) {
                VideoThumbnailView(
                    video = previewVideo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        // North-West Arrow Icon (Rotated CallMade) to copy query into search bar
        IconButton(
            onClick = onCopyQuery,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CallMade,
                contentDescription = "Insert search term",
                tint = YtTextSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(270f) // Rotates to North-West ↖
            )
        }

        // Remove item button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove from history",
                tint = YtTextSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * YouTube-style Search Result Video Card
 */
@Composable
private fun SearchResultVideoCard(
    video: VideoItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
            .testTag("search_result_card_${video.id}")
    ) {
        // 16:9 Thumbnail with duration pill
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

            // Duration badge in bottom-right corner
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

        // Video Details Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(YtAvatarTeal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.displayChannelName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.displayTitle,
                    color = YtTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${video.displayChannelName} • ${video.viewsCount} • ${video.uploadedAgo}",
                    color = YtTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (video.hashtags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = video.hashtags,
                        color = Color(0xFF3EA6FF),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(32.dp)
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
