package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.VideoItem

/**
 * Dedicated Native Android Picture-in-Picture Video Surface:
 * - Clean full-bleed video output matching standard Android PiP specifications
 * - Seamless playback continuity (seeks to current watch position)
 * - Observes play/pause state from remote PiP system actions
 * - Reports playback progress back to ViewModel
 */
@Composable
fun PipVideoPlayer(
    video: VideoItem,
    isPaused: Boolean,
    onProgressUpdate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var isPrepared by remember { mutableStateOf(false) }

    DisposableEffect(video.id) {
        onDispose {
            try {
                videoViewRef?.currentPosition?.toLong()?.let { pos ->
                    if (pos > 0) onProgressUpdate(pos)
                }
                videoViewRef?.stopPlayback()
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    tag = video.id
                    try {
                        setOnErrorListener { _, _, _ -> true }
                        if (!video.id.startsWith("sample_")) {
                            setVideoURI(Uri.parse(video.uriString))
                        }
                        setOnPreparedListener { mp ->
                            isPrepared = true
                            mp.isLooping = true
                            if (video.watchPositionMs > 500L) {
                                seekTo(video.watchPositionMs.toInt())
                            }
                            if (!isPaused) {
                                start()
                            }
                        }
                    } catch (_: Exception) {}
                    videoViewRef = this
                }
            },
            update = { vv ->
                videoViewRef = vv
                val currentTag = vv.tag as? String
                if (currentTag != video.id) {
                    vv.tag = video.id
                    isPrepared = false
                    try {
                        vv.stopPlayback()
                        if (!video.id.startsWith("sample_")) {
                            vv.setVideoURI(Uri.parse(video.uriString))
                        }
                    } catch (_: Exception) {}
                }
                try {
                    if (isPaused) {
                        if (vv.isPlaying) vv.pause()
                    } else {
                        if (!vv.isPlaying && isPrepared) vv.start()
                    }
                    val pos = vv.currentPosition.toLong()
                    if (pos > 0) {
                        onProgressUpdate(pos)
                    }
                } catch (_: Exception) {}
            },
            modifier = Modifier.fillMaxSize()
        )

        // Fallback poster while video initializes
        if (!isPrepared || video.id.startsWith("sample_")) {
            VideoThumbnailView(
                video = video,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
