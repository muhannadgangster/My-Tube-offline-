package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YtAvatarIndigo
import com.example.ui.theme.YtAvatarOrange
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtAvatarTeal
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtPillActive
import com.example.ui.theme.YtPillActiveText
import com.example.ui.theme.YtPillBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

data class NotificationModel(
    val id: String,
    val channelName: String,
    val actionText: String,
    val timeAgo: String,
    val isUnread: Boolean = true,
    val avatarBg: Color = YtAvatarPurple,
    val thumbnailPlaceholderColor: Color = Color(0xFF222222)
)

/**
 * Notifications screen matching Screenshot 3:
 * - Top header with Back arrow, "Notifications", Search, More (3-dots)
 * - Filter pills: "All" and "Mentions"
 * - "Important" section with blue unread dot, channel avatar, title, timestamp, and thumbnail on right
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedFilter by remember { mutableStateOf("All") }

    val notifications = remember {
        listOf(
            NotificationModel(
                id = "n1",
                channelName = "Soneha Sisters",
                actionText = "Uploaded: 7 Seconds Challenge With Sisters 😱 Day 130/200",
                timeAgo = "1 hour ago",
                avatarBg = YtAvatarPurple,
                thumbnailPlaceholderColor = Color(0xFF332211)
            ),
            NotificationModel(
                id = "n2",
                channelName = "MathHelper",
                actionText = "Uploaded: 2016 Ex 1 Qn 6 and 7 on rational...",
                timeAgo = "17 hours ago",
                avatarBg = YtAvatarTeal,
                thumbnailPlaceholderColor = Color(0xFF223322)
            ),
            NotificationModel(
                id = "n3",
                channelName = "D craft life style",
                actionText = "Uploaded: my biggest dream custom phone case",
                timeAgo = "15 hours ago",
                avatarBg = YtAvatarOrange,
                thumbnailPlaceholderColor = Color(0xFF222233)
            ),
            NotificationModel(
                id = "n4",
                channelName = "For you",
                actionText = "Build Android App in Google AI Studio • New Tools",
                timeAgo = "12 hours ago",
                avatarBg = YtAvatarIndigo,
                thumbnailPlaceholderColor = Color(0xFF113333)
            ),
            NotificationModel(
                id = "n5",
                channelName = "For you",
                actionText = "Bank manager 😂 galat fas gya 😂 #Shorts",
                timeAgo = "2 hours ago",
                avatarBg = YtAvatarTeal,
                thumbnailPlaceholderColor = Color(0xFF331122)
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = YtDarkBackground,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header (Screenshot 3 style)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary
                    )
                }

                Text(
                    text = "Notifications",
                    color = YtTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )

                IconButton(onClick = { /* Search notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = YtTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Filter Tabs: "All" and "Mentions" (Screenshot 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Mentions").forEach { tab ->
                    val isSelected = selectedFilter == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) YtPillActive else YtPillBackground)
                            .clickable { selectedFilter = tab }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) YtPillActiveText else YtTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section Header: "Important"
            Text(
                text = "Important",
                color = YtTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Notifications List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(item: NotificationModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Blue unread dot indicator (Screenshot 3)
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (item.isUnread) Color(0xFF3EA6FF) else Color.Transparent)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // Channel Avatar Circle
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(item.avatarBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.channelName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Notification Text (Channel Name, Title, Timestamp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.channelName,
                color = YtTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.actionText,
                color = YtTextPrimary,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.timeAgo,
                color = YtTextSecondary,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Video Thumbnail on the right side (Screenshot 3)
        Box(
            modifier = Modifier
                .size(64.dp, 36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(item.thumbnailPlaceholderColor)
                .border(0.5.dp, YtBorder, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▶",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // 3-dots Menu
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Options",
            tint = YtTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
