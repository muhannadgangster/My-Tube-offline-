package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.data.VideoItem
import com.example.data.local.AppDatabase
import com.example.data.local.CommentEntity
import com.example.data.local.VideoEntity
import com.example.util.MediaScannerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

class VideoRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.videoDao()

    val allVideosFlow: Flow<List<VideoItem>> = combine(
        dao.getAllMetadata(),
        dao.getHistory()
    ) { allEntities, historyEntities ->
        val historyMap = historyEntities.associateBy { it.id }
        allEntities.map { entity ->
            val hist = historyMap[entity.id]
            entity.toVideoItem(
                watchPositionMs = hist?.watchPositionMs ?: entity.watchPositionMs,
                lastWatchedTimestamp = hist?.lastWatchedTimestamp ?: entity.lastWatchedTimestamp
            )
        }.deduplicate()
    }.flowOn(Dispatchers.IO)

    val shortsVideosFlow: Flow<List<VideoItem>> = dao.getAllMetadata().combine(dao.getHistory()) { list, _ ->
        val shorts = list.filter { entity ->
            entity.isPhotoSlideshow ||
                    entity.id.startsWith("short_clip_") ||
                    (entity.height > entity.width && entity.height > 0) ||
                    (entity.durationMs in 1..95000) ||
                    entity.originalTitle.contains("short", ignoreCase = true) ||
                    entity.hashtags.contains("#Shorts", ignoreCase = true)
        }
        val mapped = if (shorts.isNotEmpty()) {
            shorts.map { it.toVideoItem() }
        } else {
            list.map { it.toVideoItem().copy(isShort = true) }
        }
        mapped.deduplicate()
    }.flowOn(Dispatchers.IO)

    private fun List<VideoItem>.deduplicate(): List<VideoItem> {
        val seenSignatures = HashSet<String>()
        val result = ArrayList<VideoItem>()
        for (item in this) {
            val normTitle = item.displayTitle.lowercase().replace(Regex("[^a-z0-9]"), "").trim()
            val sig = if (normTitle.length >= 3) {
                "$normTitle-${item.durationMs / 2000}"
            } else {
                item.filePath.ifEmpty { item.uriString }
            }
            if (seenSignatures.add(sig)) {
                result.add(item)
            }
        }
        return result
    }

    suspend fun saveRecordedShort(
        title: String,
        hashtags: String,
        durationMs: Long,
        videoUri: String = "",
        filePath: String = "",
        audioTitle: String = ""
    ): VideoItem = withContext(Dispatchers.IO) {
        val id = "short_clip_${System.currentTimeMillis()}"
        val entity = VideoEntity(
            id = id,
            uriString = videoUri.ifEmpty { "content://media/recorded/$id" },
            filePath = filePath,
            originalTitle = title,
            customTitle = title,
            hashtags = hashtags,
            channelName = "My Shorts Channel",
            customChannelName = "My Shorts Channel",
            durationMs = durationMs,
            dateAdded = System.currentTimeMillis(),
            width = 1080,
            height = 1920,
            audioUriString = audioTitle
        )
        dao.insertOrUpdate(entity)
        entity.toVideoItem()
    }

    val subscribedVideosFlow: Flow<List<VideoItem>> = dao.getSubscribed().combine(dao.getHistory()) { list, _ ->
        list.map { it.toVideoItem() }
    }.flowOn(Dispatchers.IO)

    val likedVideosFlow: Flow<List<VideoItem>> = dao.getLiked().combine(dao.getHistory()) { list, _ ->
        list.map { it.toVideoItem() }
    }.flowOn(Dispatchers.IO)

    val watchLaterVideosFlow: Flow<List<VideoItem>> = dao.getWatchLater().combine(dao.getHistory()) { list, _ ->
        list.map { it.toVideoItem() }
    }.flowOn(Dispatchers.IO)

    val downloadedVideosFlow: Flow<List<VideoItem>> = dao.getDownloaded().combine(dao.getHistory()) { list, _ ->
        list.map { it.toVideoItem() }
    }.flowOn(Dispatchers.IO)

    val historyVideosFlow: Flow<List<VideoItem>> = dao.getHistory().combine(dao.getAllMetadata()) { list, _ ->
        list.map { it.toVideoItem() }
    }.flowOn(Dispatchers.IO)

    suspend fun scanStorage() = withContext(Dispatchers.IO) {
        val scanned = MediaScannerHelper.scanLocalVideosAndPhotos(context)
        if (scanned.isNotEmpty()) {
            // Preserve existing user customizations
            val merged = scanned.map { item ->
                val existing = dao.getById(item.id)
                if (existing != null) {
                    item.copy(
                        customTitle = existing.customTitle,
                        customChannelName = existing.customChannelName,
                        hashtags = if (existing.hashtags.isNotBlank()) existing.hashtags else item.hashtags,
                        isLiked = existing.isLiked,
                        isDisliked = existing.isDisliked,
                        isSubscribed = existing.isSubscribed,
                        isWatchLater = existing.isWatchLater,
                        isDownloaded = existing.isDownloaded,
                        watchPositionMs = existing.watchPositionMs,
                        lastWatchedTimestamp = existing.lastWatchedTimestamp,
                        userNotes = existing.userNotes
                    )
                } else {
                    item
                }
            }
            dao.insertAll(merged)
        } else {
            // Bundled starter entries
            val starterVideos = ensureStarterVideos()
            dao.insertAll(starterVideos)
        }
    }

    private suspend fun ensureStarterVideos(): List<VideoEntity> = withContext(Dispatchers.IO) {
        val sampleDir = File(context.filesDir, "sample_media")
        if (!sampleDir.exists()) sampleDir.mkdirs()

        val sampleConfigs = listOf(
            Triple("Extreme Rude Prank on Neha | 125/200", "Soneha Sisters", 1435000L),
            Triple("Exploring the Hidden Wonders of Patagonia | Cinematic 4K", "TechWorld", 605000L),
            Triple("Epic Gaming Moments: Top Plays of the Week #42", "PixelGaming", 1095000L),
            Triple("Trying 90's Snacks Today! Super Nostalgic Tasting", "Sejal Gaba Vlogs", 584000L),
            Triple("How to Root & Flash Custom ROM on Android 2026", "Tech Jar", 894000L),
            Triple("E-Bike Scheme 2026 How To Apply Online Full Guide", "World Say Online", 584000L),
            Triple("Sunset Drone Flight Over Turquoise Beach Coast (Shorts)", "AeroCinematics", 34000L),
            Triple("Crazy Street Food in Old Market Night Tour (Shorts)", "FoodieWalks", 28000L)
        )

        val list = mutableListOf<VideoEntity>()
        sampleConfigs.forEachIndexed { index, (title, channel, duration) ->
            val sampleFile = File(sampleDir, "sample_video_${index + 1}.mp4")
            if (!sampleFile.exists()) {
                try {
                    sampleFile.writeBytes(createSyntheticMp4Placeholder())
                } catch (_: Exception) {}
            }

            val isShort = index >= 6
            val width = if (isShort) 1080 else 1920
            val height = if (isShort) 1920 else 1080

            val entity = VideoEntity(
                id = "sample_$index",
                uriString = Uri.fromFile(sampleFile).toString(),
                filePath = sampleFile.absolutePath,
                originalTitle = title,
                customTitle = null,
                hashtags = if (isShort) "#Shorts #Vlog #Trending #Viral" else "#FullHD #Tutorial #Trending",
                channelName = channel,
                customChannelName = null,
                durationMs = duration,
                sizeBytes = (duration * 128).coerceAtLeast(1024 * 1024),
                dateAdded = System.currentTimeMillis() - (index * 86400000L),
                width = width,
                height = height,
                isLiked = index == 0 || index == 3,
                isDisliked = false,
                isSubscribed = index == 0 || index == 1,
                isWatchLater = index == 2,
                isDownloaded = index == 0,
                watchPositionMs = if (index == 0) 340000L else if (index == 1) 120000L else 0L,
                lastWatchedTimestamp = if (index <= 1) System.currentTimeMillis() - (index * 3600000L) else 0L,
                userNotes = if (index == 0) "Hilarious prank ending at 18:20!" else "",
                isPhotoSlideshow = false,
                photoUrisString = "",
                clipStartMs = 0L,
                clipEndMs = duration,
                audioUriString = ""
            )
            list.add(entity)
        }
        list
    }

    private fun createSyntheticMp4Placeholder(): ByteArray {
        val ftyp = byteArrayOf(
            0x00, 0x00, 0x00, 0x20,
            0x66, 0x74, 0x79, 0x70,
            0x69, 0x73, 0x6F, 0x6D,
            0x00, 0x00, 0x02, 0x00,
            0x69, 0x73, 0x6F, 0x6D,
            0x69, 0x73, 0x6F, 0x32,
            0x6D, 0x70, 0x34, 0x31
        )
        val data = ByteArray(4096)
        System.arraycopy(ftyp, 0, data, 0, ftyp.size)
        return data
    }

    suspend fun importVideo(uri: Uri, customTitle: String? = null, hashtags: String? = null): VideoItem? = withContext(Dispatchers.IO) {
        try {
            var fileName = "Imported_Video_${System.currentTimeMillis()}"
            var duration = 0L
            var size = 0L
            var width = 1920
            var height = 1080

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
                }
            }

            try {
                val retriever = android.media.MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 60000L
                val w = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
                val h = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
                if (w != null && w > 0) width = w
                if (h != null && h > 0) height = h
                retriever.release()
            } catch (_: Exception) {}

            val id = "imported_${System.currentTimeMillis()}"
            val defaultHashtags = if (height > width || duration <= 60000) "#Shorts #Trending #Gallery" else "#Vlog #LocalMedia"

            val entity = VideoEntity(
                id = id,
                uriString = uri.toString(),
                filePath = uri.toString(),
                originalTitle = fileName.substringBeforeLast("."),
                customTitle = customTitle?.takeIf { it.isNotBlank() },
                hashtags = hashtags?.takeIf { it.isNotBlank() } ?: defaultHashtags,
                channelName = "My Uploads",
                customChannelName = null,
                durationMs = duration,
                sizeBytes = size,
                dateAdded = System.currentTimeMillis(),
                width = width,
                height = height,
                isLiked = false,
                isDisliked = false,
                isSubscribed = false,
                isWatchLater = false,
                isDownloaded = true,
                watchPositionMs = 0L,
                lastWatchedTimestamp = 0L,
                userNotes = "",
                isPhotoSlideshow = false,
                photoUrisString = "",
                clipStartMs = 0L,
                clipEndMs = duration,
                audioUriString = ""
            )
            dao.insertOrUpdate(entity)
            entity.toVideoItem()
        } catch (e: Exception) {
            Log.e("VideoRepository", "Failed to import video", e)
            null
        }
    }

    suspend fun createPhotoSlideshow(
        photoUris: List<Uri>,
        title: String,
        hashtags: String,
        audioUri: Uri? = null,
        durationSeconds: Int = 8
    ): VideoItem = withContext(Dispatchers.IO) {
        val id = "slideshow_${System.currentTimeMillis()}"
        val photoUrisString = photoUris.joinToString(",") { it.toString() }
        val durationMs = (durationSeconds * 1000L).coerceIn(5000L, 15000L)

        val entity = VideoEntity(
            id = id,
            uriString = photoUris.firstOrNull()?.toString() ?: "",
            filePath = photoUris.firstOrNull()?.toString() ?: "",
            originalTitle = title,
            customTitle = null,
            hashtags = hashtags.ifBlank { "#Shorts #PhotoSlideshow #Memories #Gallery" },
            channelName = "Photo Creator",
            customChannelName = null,
            durationMs = durationMs,
            sizeBytes = 2 * 1024 * 1024L,
            dateAdded = System.currentTimeMillis(),
            width = 1080,
            height = 1920,
            isLiked = false,
            isDisliked = false,
            isSubscribed = false,
            isWatchLater = false,
            isDownloaded = true,
            watchPositionMs = 0L,
            lastWatchedTimestamp = 0L,
            userNotes = "",
            isPhotoSlideshow = true,
            photoUrisString = photoUrisString,
            clipStartMs = 0L,
            clipEndMs = durationMs,
            audioUriString = audioUri?.toString() ?: ""
        )
        dao.insertOrUpdate(entity)
        entity.toVideoItem()
    }

    suspend fun setCustomTitleAndHashtags(id: String, newTitle: String, newHashtags: String) = withContext(Dispatchers.IO) {
        dao.updateCustomTitleAndHashtags(id, newTitle, newHashtags)
    }

    suspend fun setCustomTitle(id: String, newTitle: String) = withContext(Dispatchers.IO) {
        dao.updateCustomTitle(id, newTitle)
    }

    suspend fun toggleLike(id: String) = withContext(Dispatchers.IO) {
        val item = dao.getById(id) ?: return@withContext
        val newLiked = !item.isLiked
        val defaultBaseLikes = ((kotlin.math.abs(id.hashCode()) % 800 + 50) * 1000L)
        val baseCount = if (item.likesCount > 0L) item.likesCount else defaultBaseLikes
        val newCount = if (newLiked) {
            if (!item.isLiked) baseCount + 1L else baseCount
        } else {
            if (item.isLiked) (baseCount - 1L).coerceAtLeast(0L) else (baseCount - 1L).coerceAtLeast(0L)
        }
        dao.updateLikeStatusWithCount(id = id, isLiked = newLiked, isDisliked = false, likesCount = newCount)
    }

    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> =
        db.commentDao().getCommentsForVideo(videoId).flowOn(Dispatchers.IO)

    fun getCommentCountForVideo(videoId: String): Flow<Int> =
        db.commentDao().getCommentCountForVideo(videoId).flowOn(Dispatchers.IO)

    suspend fun addComment(
        videoId: String,
        text: String,
        authorName: String = "Muhannad Murtaza",
        authorHandle: String = "@muhannad",
        authorAvatarLetter: String = "M"
    ): CommentEntity = withContext(Dispatchers.IO) {
        val comment = CommentEntity(
            videoId = videoId,
            authorName = authorName,
            authorHandle = authorHandle,
            authorAvatarLetter = authorAvatarLetter,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        db.commentDao().insertComment(comment)
        comment
    }

    suspend fun toggleDislike(id: String) = withContext(Dispatchers.IO) {
        val item = dao.getById(id) ?: return@withContext
        val newDisliked = !item.isDisliked
        dao.updateLikeStatus(id, isLiked = false, isDisliked = newDisliked)
    }

    suspend fun toggleSubscription(id: String) = withContext(Dispatchers.IO) {
        val item = dao.getById(id) ?: return@withContext
        dao.updateSubscription(id, !item.isSubscribed)
    }

    suspend fun toggleWatchLater(id: String) = withContext(Dispatchers.IO) {
        val item = dao.getById(id) ?: return@withContext
        dao.updateWatchLater(id, !item.isWatchLater)
    }

    suspend fun toggleDownloaded(id: String) = withContext(Dispatchers.IO) {
        val item = dao.getById(id) ?: return@withContext
        dao.updateDownloaded(id, !item.isDownloaded)
    }

    suspend fun recordWatchProgress(id: String, positionMs: Long) = withContext(Dispatchers.IO) {
        dao.updateWatchProgress(id, positionMs, System.currentTimeMillis())
    }

    suspend fun saveNotes(id: String, notes: String) = withContext(Dispatchers.IO) {
        dao.updateNotes(id, notes)
    }

    suspend fun clearWatchHistory() = withContext(Dispatchers.IO) {
        dao.clearWatchHistory()
    }

    suspend fun deleteAllDownloads() = withContext(Dispatchers.IO) {
        dao.deleteAllDownloads()
    }
}

private fun VideoEntity.toVideoItem(
    watchPositionMs: Long = this.watchPositionMs,
    lastWatchedTimestamp: Long = this.lastWatchedTimestamp
): VideoItem {
    val isVertical = height > width && height > 0
    val isShort = isPhotoSlideshow || id.startsWith("short_clip_") || isVertical || (durationMs in 1..60000) || originalTitle.contains("short", ignoreCase = true) || hashtags.contains("#Shorts", ignoreCase = true)

    val hash = kotlin.math.abs(id.hashCode())
    val viewsNumber = (hash % 900 + 10)
    val viewsStr = if (viewsNumber > 500) "${viewsNumber / 10}k views" else "${viewsNumber}k views"

    val daysAgo = (hash % 14 + 1)
    val uploadStr = if (daysAgo == 1) "1 day ago" else "$daysAgo days ago"

    val photoList = if (photoUrisString.isNotBlank()) {
        photoUrisString.split(",").filter { it.isNotBlank() }
    } else {
        emptyList()
    }

    val defaultBaseLikes = ((kotlin.math.abs(id.hashCode()) % 800 + 50) * 1000L)
    val effectiveLikes = if (likesCount > 0L) likesCount else (if (isLiked) defaultBaseLikes + 1L else defaultBaseLikes)
    val defaultBaseComments = (kotlin.math.abs(id.hashCode()) % 150 + 12)

    return VideoItem(
        id = id,
        uriString = uriString,
        filePath = filePath,
        originalTitle = originalTitle,
        customTitle = customTitle,
        hashtags = hashtags,
        channelName = channelName,
        customChannelName = customChannelName,
        durationMs = durationMs,
        sizeBytes = sizeBytes,
        dateAdded = dateAdded,
        width = width,
        height = height,
        isLiked = isLiked,
        isDisliked = isDisliked,
        isSubscribed = isSubscribed,
        isWatchLater = isWatchLater,
        isDownloaded = isDownloaded,
        watchPositionMs = watchPositionMs,
        lastWatchedTimestamp = lastWatchedTimestamp,
        userNotes = userNotes,
        viewsCount = viewsStr,
        uploadedAgo = uploadStr,
        isShort = isShort,
        isPhotoSlideshow = isPhotoSlideshow,
        photoUris = photoList,
        clipStartMs = clipStartMs,
        clipEndMs = clipEndMs,
        audioUriString = audioUriString,
        likesCount = effectiveLikes,
        commentsCount = defaultBaseComments
    )
}
