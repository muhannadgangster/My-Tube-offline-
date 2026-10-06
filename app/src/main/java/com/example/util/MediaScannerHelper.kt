package com.example.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.data.local.VideoEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object MediaScannerHelper {

    fun generateCatchyTitleAndHashtags(
        rawName: String,
        isPhotoSlideshow: Boolean,
        isShort: Boolean,
        dateAddedMs: Long
    ): Pair<String, String> {
        val dateStr = try {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(dateAddedMs))
        } catch (_: Exception) {
            "Today"
        }

        val cleanName = rawName.substringBeforeLast(".")
            .replace("_", " ")
            .replace("-", " ")
            .trim()

        val lowerName = cleanName.lowercase()
        val hash = kotlin.math.abs(rawName.hashCode())

        // 1. Gaming Category Detection
        val isGaming = lowerName.contains("free fire") || lowerName.contains("ff") ||
                lowerName.contains("pubg") || lowerName.contains("bgmi") ||
                lowerName.contains("cod") || lowerName.contains("call of duty") ||
                lowerName.contains("gta") || lowerName.contains("roblox") ||
                lowerName.contains("minecraft") || lowerName.contains("game") ||
                lowerName.contains("gameplay") || lowerName.contains("clutch") ||
                lowerName.contains("headshot") || lowerName.contains("montage") ||
                lowerName.contains("valorant") || lowerName.contains("fifa")

        // 2. Song / Music Edition Detection
        val isMusic = lowerName.contains("song") || lowerName.contains("music") ||
                lowerName.contains("beat") || lowerName.contains("audio") ||
                lowerName.contains("remix") || lowerName.contains("acoustic") ||
                lowerName.contains("melody") || lowerName.contains("lofi") ||
                lowerName.contains("slowed") || lowerName.contains("reverb") ||
                lowerName.contains("track") || lowerName.contains("bass")

        // 3. Vlog / Travel / Nature Detection
        val isVlog = lowerName.contains("vlog") || lowerName.contains("travel") ||
                lowerName.contains("trip") || lowerName.contains("tour") ||
                lowerName.contains("city") || lowerName.contains("walk") ||
                lowerName.contains("routine") || lowerName.contains("day") ||
                lowerName.contains("nature") || lowerName.contains("beach")

        // Check if filename is an unreadable hash or default camera filename (e.g. "d3191df3975b6cc0bf..." or "VID_2026...")
        val isRawOrHash = cleanName.startsWith("VID", ignoreCase = true) ||
                cleanName.startsWith("PXL", ignoreCase = true) ||
                cleanName.startsWith("IMG", ignoreCase = true) ||
                cleanName.startsWith("REC", ignoreCase = true) ||
                cleanName.matches(Regex("\\d+.*")) ||
                (cleanName.length >= 14 && cleanName.matches(Regex("[0-9a-fA-F_-]+")))

        val catchyTitle: String
        val hashtags: String

        when {
            isPhotoSlideshow -> {
                val slideshowTitles = listOf(
                    "Memories & Captured Moments | $dateStr",
                    "Photo Story & Beautiful Highlights | $dateStr",
                    "Special Moments Showcase | $dateStr",
                    "Album Collection & Daily Highlights | $dateStr"
                )
                catchyTitle = slideshowTitles[hash % slideshowTitles.size]
                hashtags = "#Shorts #PhotoSlideshow #Memories #Gallery #Vlog"
            }

            isGaming -> {
                val gamingTitles = listOf(
                    "Free Fire Gameplay Highlights | Song Edition",
                    "Epic Pro Gaming Clutch & Top Highlights",
                    "Best Ranked Gameplay & Headshot Montage",
                    "Insane Moments & Fast Reflexes | Gameplay Edition",
                    "Pro Tier Gaming Clutch | Free Fire Song Edition",
                    "Top 1% Ranked Gameplay Highlights"
                )
                catchyTitle = gamingTitles[hash % gamingTitles.size]
                hashtags = "#Gaming #FreeFire #Gameplay #Highlights #SongEdition #Montage"
            }

            isMusic -> {
                val musicTitles = listOf(
                    "Acoustic Melody & Chill Vibes | Song Edition",
                    "Trending Music & Visual Beat Showcase",
                    "Slowed & Reverb Melodies | Night Vibes",
                    "Ultimate Bass & Aesthetic Beat Showcase",
                    "Relaxing Melodies & Lo-Fi Beats | Song Edition"
                )
                catchyTitle = musicTitles[hash % musicTitles.size]
                hashtags = "#Music #TrendingSong #Acoustic #Lofi #SlowedReverb #SongEdition"
            }

            isVlog -> {
                val vlogTitles = listOf(
                    "Daily Life & City Walkthrough | Vlog Edition",
                    "Exploring Scenic Spots & Local Highlights",
                    "Weekend Getaway & Cinematic Road Trip",
                    "A Day in My Life & Behind The Scenes | Vlog",
                    "Street Vibes & Unfiltered Daily Highlights"
                )
                catchyTitle = vlogTitles[hash % vlogTitles.size]
                hashtags = "#DailyVlog #Travel #Cinematic #CityWalk #VlogEdition"
            }

            isRawOrHash && isShort -> {
                val shortsTitles = listOf(
                    "Quick Highlight & Behind The Scenes | #Shorts",
                    "Viral Trend Challenge & Reel | $dateStr",
                    "Top 10 Seconds You Can't Miss | Quick Clip",
                    "Satisfying Visuals & Daily Snippet",
                    "POV: When Everything Goes Perfectly | Reel",
                    "Quick Hacks & Smart Tips | $dateStr",
                    "Street Beats & Spontaneous Moments",
                    "Mini Vlog: 60 Seconds Daily Life",
                    "Behind The Camera & Crazy Moments",
                    "Top Trending Moment | Song Edition",
                    "Best Quick Reaction & Highlight Clip"
                )
                catchyTitle = shortsTitles[hash % shortsTitles.size]
                hashtags = "#Shorts #Trending #Viral #QuickClips #Reels"
            }

            isRawOrHash && !isShort -> {
                val longTitles = listOf(
                    "New Daily Vlog & Local Showcase | $dateStr",
                    "Full Cinematic 4K Reel & Atmosphere Showcase",
                    "Complete Gameplay Walkthrough & Highlights | Song Edition",
                    "Masterclass & In-Depth Walkthrough | Full HD",
                    "Relaxing Ambience & Peaceful Showcase | $dateStr",
                    "Deep Dive & Unfiltered Video Showcase",
                    "Special Feature & Creative Production Highlights"
                )
                catchyTitle = longTitles[hash % longTitles.size]
                hashtags = "#LocalMedia #FullHD #Vlog #OfflinePlayer #Trending"
            }

            else -> {
                val formatted = cleanName.split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
                catchyTitle = if (formatted.length > 50) formatted.take(50) + "..." else formatted
                hashtags = if (isShort) "#Shorts #Trending #Viral" else "#LocalMedia #FullHD #Trending"
            }
        }

        return Pair(catchyTitle, hashtags)
    }

    suspend fun scanLocalVideosAndPhotos(context: Context): List<VideoEntity> {
        val resultList = mutableListOf<VideoEntity>()

        // 1. Scan Local Videos
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT
        )

        try {
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )

            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val dataCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val durCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val widthCol = c.getColumnIndex(MediaStore.Video.Media.WIDTH)
                val heightCol = c.getColumnIndex(MediaStore.Video.Media.HEIGHT)

                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val name = c.getString(nameCol) ?: "Video_$id"
                    val filePath = c.getString(dataCol) ?: contentUri.toString()
                    val duration = c.getLong(durCol)
                    val size = c.getLong(sizeCol)
                    val dateAdded = c.getLong(dateCol) * 1000L
                    val width = if (widthCol >= 0) c.getInt(widthCol) else 1920
                    val height = if (heightCol >= 0) c.getInt(heightCol) else 1080

                    val orientCol = c.getColumnIndex("orientation")
                    val orientation = if (orientCol >= 0) c.getInt(orientCol) else 0
                    val effectiveWidth = if (orientation == 90 || orientation == 270) height else width
                    val effectiveHeight = if (orientation == 90 || orientation == 270) width else height

                    val parentFolder = try {
                        File(filePath).parentFile?.name ?: "Gallery Media"
                    } catch (_: Exception) {
                        "Gallery Media"
                    }

                    val isVertical = effectiveHeight > effectiveWidth && effectiveHeight > 0
                    val (generatedTitle, generatedHashtags) = generateCatchyTitleAndHashtags(
                        rawName = name,
                        isPhotoSlideshow = false,
                        isShort = isVertical || duration in 1..60000,
                        dateAddedMs = if (dateAdded > 0) dateAdded else System.currentTimeMillis()
                    )

                    val entity = VideoEntity(
                        id = id.toString(),
                        uriString = contentUri.toString(),
                        filePath = filePath,
                        originalTitle = generatedTitle,
                        customTitle = null,
                        hashtags = generatedHashtags,
                        channelName = parentFolder,
                        customChannelName = null,
                        durationMs = if (duration > 0) duration else 60000L,
                        sizeBytes = size,
                        dateAdded = if (dateAdded > 0) dateAdded else System.currentTimeMillis(),
                        width = if (effectiveWidth > 0) effectiveWidth else 1920,
                        height = if (effectiveHeight > 0) effectiveHeight else 1080,
                        isLiked = false,
                        isDisliked = false,
                        isSubscribed = false,
                        isWatchLater = false,
                        isDownloaded = false,
                        watchPositionMs = 0L,
                        lastWatchedTimestamp = 0L,
                        userNotes = "",
                        isPhotoSlideshow = false,
                        photoUrisString = "",
                        clipStartMs = 0L,
                        clipEndMs = if (duration > 0) duration else 60000L,
                        audioUriString = ""
                    )
                    resultList.add(entity)

                    // AUTOMATED 5-10s SHORTS AUTO-CROPPING
                    // For longer videos (> 15s), also generate an automated 5-10s Shorts Highlight
                    if (duration > 15000L) {
                        val clipDuration = (8000L).coerceAtMost(duration)
                        val shortId = "short_clip_$id"
                        val (shortTitle, shortHashtags) = generateCatchyTitleAndHashtags(
                            rawName = "${entity.originalTitle} [5-10s Auto-Short]",
                            isPhotoSlideshow = false,
                            isShort = true,
                            dateAddedMs = entity.dateAdded
                        )
                        resultList.add(
                            entity.copy(
                                id = shortId,
                                originalTitle = shortTitle,
                                hashtags = "#Shorts #AutoClip #Highlights #Trending",
                                durationMs = clipDuration,
                                width = 1080,
                                height = 1920,
                                clipStartMs = 0L,
                                clipEndMs = clipDuration
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaScannerHelper", "Error scanning videos", e)
        }

        // 2. Scan Local Photos (.jpg, .png) & Group into Photo-to-Video Ken Burns Slideshows
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        try {
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )

            val photoList = mutableListOf<Triple<String, String, Long>>() // uri, album, date
            cursor?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val bucketCol = c.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val dateCol = c.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)

                var count = 0
                while (c.moveToNext() && count < 50) {
                    val id = c.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    val bucket = if (bucketCol >= 0) c.getString(bucketCol) ?: "Gallery" else "Gallery"
                    val dateAdded = if (dateCol >= 0) c.getLong(dateCol) * 1000L else System.currentTimeMillis()
                    photoList.add(Triple(contentUri.toString(), bucket, dateAdded))
                    count++
                }
            }

            // Group into clusters of 3-5 photos for animated Ken Burns 5-10s slideshows
            if (photoList.isNotEmpty()) {
                val chunks = photoList.chunked(4)
                chunks.forEachIndexed { index, chunkPhotos ->
                    val firstPhoto = chunkPhotos.first()
                    val photoUrisString = chunkPhotos.joinToString(",") { it.first }
                    val albumName = firstPhoto.second
                    val slideshowId = "photo_slideshow_$index"

                    val (slideTitle, slideHashtags) = generateCatchyTitleAndHashtags(
                        rawName = "$albumName Photo Journey #Shorts",
                        isPhotoSlideshow = true,
                        isShort = true,
                        dateAddedMs = firstPhoto.third
                    )

                    val slideshowEntity = VideoEntity(
                        id = slideshowId,
                        uriString = firstPhoto.first,
                        filePath = firstPhoto.first,
                        originalTitle = slideTitle,
                        customTitle = null,
                        hashtags = slideHashtags,
                        channelName = "$albumName Showcase",
                        customChannelName = null,
                        durationMs = (chunkPhotos.size * 2500L).coerceIn(5000L, 10000L), // 5-10 seconds
                        sizeBytes = 2 * 1024 * 1024L,
                        dateAdded = firstPhoto.third,
                        width = 1080,
                        height = 1920,
                        isLiked = false,
                        isDisliked = false,
                        isSubscribed = false,
                        isWatchLater = false,
                        isDownloaded = false,
                        watchPositionMs = 0L,
                        lastWatchedTimestamp = 0L,
                        userNotes = "",
                        isPhotoSlideshow = true,
                        photoUrisString = photoUrisString,
                        clipStartMs = 0L,
                        clipEndMs = 8000L,
                        audioUriString = ""
                    )
                    resultList.add(slideshowEntity)
                }
            }
        } catch (e: Exception) {
            Log.e("MediaScannerHelper", "Error scanning images", e)
        }

        return resultList
    }
}
