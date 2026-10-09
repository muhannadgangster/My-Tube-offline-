package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.YtAvatarOrange
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtAvatarTeal

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val videoId: String? = null,
    val type: String = "general" // e.g. "scan", "import", "create", "ready"
) {
    val timeAgo: String
        get() {
            val diffMs = System.currentTimeMillis() - timestamp
            val seconds = diffMs / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            return when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> "${days / 7}w ago"
            }
        }

    val avatarColor: Color
        get() {
            val hash = kotlin.math.abs(id.hashCode())
            return when (hash % 3) {
                0 -> YtAvatarPurple
                1 -> YtAvatarTeal
                else -> YtAvatarOrange
            }
        }
}
