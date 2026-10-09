package com.example.util

import com.example.data.local.CommentEntity
import kotlin.math.abs

object LocalCommentGenerator {

    /**
     * Generates 3-4 natural, video-specific suggestions locally based entirely on offline metadata.
     * No network or cloud APIs used.
     */
    fun generateLocalComments(
        videoId: String,
        title: String,
        channelName: String,
        durationMs: Long,
        isShort: Boolean
    ): List<CommentEntity> {
        val hash = abs(videoId.hashCode() + title.hashCode())
        val cleanTitle = title.take(40)
        val durationMinutes = durationMs / 60000

        val commentPool = mutableListOf<String>()

        if (isShort) {
            commentPool.add("Awesome Short! Loved the quick pacing and edits.")
            commentPool.add("This clip is so satisfying to watch on loop 🔥")
            commentPool.add("Perfect length for this, brilliant creation!")
            commentPool.add("Great moment captured in this Short 👍")
            commentPool.add("Short and punchy! Keep these coming.")
        } else {
            if (durationMinutes > 5) {
                commentPool.add("Really enjoyed watching all ${durationMinutes} minutes of this video.")
                commentPool.add("Great full-length local watch, high quality detail throughout!")
                commentPool.add("Very engaging video from start to finish.")
            } else {
                commentPool.add("Really well produced video, crisp and clear.")
                commentPool.add("Enjoyed this video! Glad I saved it to my local library.")
                commentPool.add("Nice video! Looks very interesting.")
            }
            commentPool.add("Great presentation by $channelName!")
            commentPool.add("Definitely saving this one in my offline favorites.")
        }

        // Title-specific comment
        commentPool.add("Great work on \"$cleanTitle\"! Really stands out.")

        // Deterministically pick 3 to 4 comments for this specific video
        val selected = mutableListOf<String>()
        val count = 3 + (hash % 2) // 3 or 4 comments
        for (i in 0 until count) {
            val idx = (hash + i * 7) % commentPool.size
            val text = commentPool[idx]
            if (!selected.contains(text)) {
                selected.add(text)
            }
        }
        if (selected.size < 3) {
            selected.add("Enjoyed watching this offline!")
        }

        val baseTime = System.currentTimeMillis() - (hash % 86400000L) // staggered timestamps
        return selected.mapIndexed { index, text ->
            CommentEntity(
                videoId = videoId,
                authorName = "Auto-generated local comment",
                authorHandle = "@offline_suggested",
                authorAvatarLetter = "C",
                text = text,
                timestamp = baseTime + (index * 60000L),
                likesCount = ((hash + index * 3) % 15).toInt(),
                isLiked = false
            )
        }
    }
}
