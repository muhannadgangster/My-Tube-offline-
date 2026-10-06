package com.example.data

data class VideoItem(
    val id: String,
    val uriString: String,
    val filePath: String,
    val originalTitle: String,
    val customTitle: String? = null,
    val hashtags: String = "#Shorts #Trending #LocalMedia",
    val channelName: String = "Local Channel",
    val customChannelName: String? = null,
    val durationMs: Long = 0L,
    val sizeBytes: Long = 0L,
    val dateAdded: Long = System.currentTimeMillis(),
    val width: Int = 1920,
    val height: Int = 1080,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSubscribed: Boolean = false,
    val isWatchLater: Boolean = false,
    val isDownloaded: Boolean = false,
    val watchPositionMs: Long = 0L,
    val lastWatchedTimestamp: Long = 0L,
    val userNotes: String = "",
    val viewsCount: String = "1.2M views",
    val uploadedAgo: String = "2 days ago",
    val isShort: Boolean = false,
    val isPhotoSlideshow: Boolean = false,
    val photoUris: List<String> = emptyList(),
    val clipStartMs: Long = 0L,
    val clipEndMs: Long = 0L,
    val audioUriString: String = "",
    val likesCount: Long = 0L,
    val commentsCount: Int = 0
) {
    val formattedLikesCount: String
        get() {
            val count = likesCount
            return when {
                count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
                count >= 10_000 -> "${count / 1_000}K"
                count >= 1_000 -> String.format("%.1fK", count / 1_000.0).replace(".0K", "K")
                count > 0 -> count.toString()
                else -> "0"
            }
        }

    val formattedCommentsCount: String
        get() = when {
            commentsCount >= 1_000_000 -> String.format("%.1fM", commentsCount / 1_000_000.0)
            commentsCount >= 1_000 -> String.format("%.1fK", commentsCount / 1_000.0).replace(".0K", "K")
            commentsCount > 0 -> commentsCount.toString()
            else -> "0"
        }

    val displayTitle: String
        get() = if (!customTitle.isNullOrBlank()) customTitle else originalTitle

    val displayChannelName: String
        get() = if (!customChannelName.isNullOrBlank()) customChannelName else channelName

    val formattedDuration: String
        get() {
            if (durationMs <= 0) return "0:00"
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val hours = minutes / 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
            } else {
                String.format("%d:%02d", minutes, seconds)
            }
        }

    val watchProgressFraction: Float
        get() {
            if (durationMs <= 0 || watchPositionMs <= 0) return 0f
            return (watchPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        }
}
