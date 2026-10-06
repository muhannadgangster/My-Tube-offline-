package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val commentId: Long = 0L,
    val videoId: String,
    val authorName: String = "Muhannad Murtaza",
    val authorHandle: String = "@muhannad",
    val authorAvatarLetter: String = "M",
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false
) {
    val formattedTime: String
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
}
