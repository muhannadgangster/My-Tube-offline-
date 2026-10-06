package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.VideoItem
import com.example.ui.components.VideoMetadataEditDialog
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import com.example.ui.viewmodel.YouTubeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

data class SoundTrack(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String
)

data class ColorFilterOption(
    val name: String,
    val overlayColor: Color = Color.Transparent,
    val blendMode: BlendMode = BlendMode.Color
)

/**
 * YouTube Shorts Creation Camera Interface matching the uploaded reference UI:
 * - Top Bar: Close (X), Add sound capsule, Glowing Magic Effects star button
 * - Right Toolbar: Flip Camera, Timer (3s/10s), Duration (15s/60s), Filters, Speed (1x), Expand chevron
 * - Bottom Controls: Add (Gallery), Prominent Red Recording Button with morphing animation, Drafts button
 * - Mode Selector: Video, Short, Live, Post
 * - Adaptive layout scaling cleanly to phone DPI and font scale
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortsCameraScreen(
    onClose: () -> Unit,
    onShortCreated: (VideoItem) -> Unit,
    onAddFromGallery: (Uri) -> Unit,
    viewModel: YouTubeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Camera permission check
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Media Picker for Gallery "Add" button
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onAddFromGallery(uri)
        }
    }

    // Camera settings state
    var isFrontCamera by remember { mutableStateOf(false) }
    var timerOption by remember { mutableIntStateOf(0) } // 0: Off, 3: 3s, 10: 10s
    var maxDurationSec by remember { mutableIntStateOf(15) } // 15s or 60s
    var speedMultiplier by remember { mutableFloatStateOf(1.0f) } // 0.5x, 1x, 2x, 3x
    var isExpandedTools by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isRetouchActive by remember { mutableStateOf(false) }
    var isGridActive by remember { mutableStateOf(false) }
    var isMagicFxActive by remember { mutableStateOf(false) }

    // Audio / Sound state
    var selectedSound by remember { mutableStateOf<SoundTrack?>(null) }
    var showSoundSheet by remember { mutableStateOf(false) }

    // Filters state
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    // Drafts state
    var showDraftsSheet by remember { mutableStateOf(false) }

    // Mode Selector state
    var selectedMode by remember { mutableStateOf("Short") }

    // Recording state
    var isRecording by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }
    var recordedSeconds by remember { mutableIntStateOf(0) }
    var isCountingDown by remember { mutableStateOf(false) }
    var countdownValue by remember { mutableIntStateOf(0) }

    // Post-recording Save Dialog
    var showSaveDialog by remember { mutableStateOf(false) }
    var pendingVideoDurationMs by remember { mutableLongStateOf(0L) }
    var shortTitleInput by remember { mutableStateOf("My YouTube Short #${Random.nextInt(100, 999)}") }
    var shortHashtagsInput by remember { mutableStateOf("#Shorts #Trending #Viral #Creator") }

    // Color Filters library
    val filterOptions = remember {
        listOf(
            ColorFilterOption("Normal", Color.Transparent),
            ColorFilterOption("Glam", Color(0x33FF69B4)),
            ColorFilterOption("Cyber", Color(0x3300FFFF)),
            ColorFilterOption("Mono", Color(0x77000000), BlendMode.Saturation),
            ColorFilterOption("Warm", Color(0x33FFA500)),
            ColorFilterOption("Sepia", Color(0x3D704214)),
            ColorFilterOption("Sunset", Color(0x40FF4500))
        )
    }

    // Built-in sound library
    val popularSounds = remember {
        listOf(
            SoundTrack("s1", "Trending Phonk Beat", "Kavinsky Beats", "0:15"),
            SoundTrack("s2", "Lofi Sunset Glow", "ChillWave Lab", "0:30"),
            SoundTrack("s3", "Upbeat Dance Viral", "Hyper Beats", "0:15"),
            SoundTrack("s4", "Cinematic Strings Rise", "Epic Sounds", "0:20"),
            SoundTrack("s5", "Acoustic Morning Vibes", "Sun Indie", "0:45")
        )
    }

    // Timer logic during countdown
    LaunchedEffect(isCountingDown) {
        if (isCountingDown) {
            countdownValue = timerOption
            while (countdownValue > 0) {
                delay(1000L)
                countdownValue -= 1
            }
            isCountingDown = false
            isRecording = true
        }
    }

    // Recording progress timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = SystemClock.elapsedRealtime()
            val totalDurationMs = (maxDurationSec * 1000L / speedMultiplier).toLong()
            while (isRecording) {
                val elapsed = SystemClock.elapsedRealtime() - startTime
                recordingProgress = (elapsed.toFloat() / totalDurationMs).coerceIn(0f, 1f)
                recordedSeconds = (elapsed / 1000L).toInt()

                if (recordingProgress >= 1f) {
                    // Max duration reached
                    isRecording = false
                    pendingVideoDurationMs = (maxDurationSec * 1000L)
                    showSaveDialog = true
                    break
                }
                delay(50L)
            }
        } else {
            recordingProgress = 0f
            recordedSeconds = 0
        }
    }

    // Stop recording action
    fun stopRecording() {
        if (isRecording) {
            isRecording = false
            pendingVideoDurationMs = (recordedSeconds.coerceAtLeast(2) * 1000L)
            showSaveDialog = true
        }
    }

    // Start recording trigger
    fun startRecordingFlow() {
        if (timerOption > 0) {
            isCountingDown = true
        } else {
            isRecording = true
        }
    }

    // Magic FX pulsing transition
    val infiniteTransition = rememberInfiniteTransition(label = "magic_fx")
    val magicPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_camera_root")
    ) {
        // 1. Camera Viewfinder or Fallback Preview
        Box(modifier = Modifier.fillMaxSize()) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = if (isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (_: Exception) {
                                // Fallback handled gracefully
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Interactive Camera Fallback View with simulated live feed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1A1A1A),
                                    Color(0xFF0F0F14),
                                    Color(0xFF050508)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cameraswitch,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Camera Access Ready",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap below to enable live hardware viewfinder",
                            color = YtTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YtRed
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Enable Camera", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Applied Color Matrix Filter Shader Overlay
            val currentFilter = filterOptions[selectedFilterIndex]
            if (currentFilter.name != "Normal") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = currentFilter.overlayColor,
                                blendMode = currentFilter.blendMode
                            )
                        }
                )
            }

            // 3x3 Grid Overlay if active
            if (isGridActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val colW = w / 3f
                    val rowH = h / 3f
                    val gridColor = Color.White.copy(alpha = 0.35f)
                    val stroke = 1.2.dp.toPx()

                    drawLine(gridColor, Offset(colW, 0f), Offset(colW, h), stroke)
                    drawLine(gridColor, Offset(colW * 2, 0f), Offset(colW * 2, h), stroke)
                    drawLine(gridColor, Offset(0f, rowH), Offset(w, rowH), stroke)
                    drawLine(gridColor, Offset(0f, rowH * 2), Offset(w, rowH * 2), stroke)
                }
            }

            // Magic AI Effects Visual Particle Overlay
            if (isMagicFxActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width * 0.5f, size.height * 0.45f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0x33BA55D3),
                                Color(0x15FF1493),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.width * 0.6f * magicPulse
                        ),
                        radius = size.width * 0.6f * magicPulse,
                        center = center
                    )
                }
            }

            // Countdown Overlay
            if (isCountingDown) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$countdownValue",
                        fontSize = 110.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // 2. Top Bar & Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Recording Progress Bar (at top edge)
            if (isRecording || recordingProgress > 0f) {
                LinearProgressIndicator(
                    progress = { recordingProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = YtRed,
                    trackColor = Color(0x66FFFFFF)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(YtRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("00:%02d / 00:%02d", recordedSeconds, maxDurationSec),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Top Bar Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close (X) Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClose() }
                        .testTag("camera_close_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center "Add sound" Capsule Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x99222222))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showSoundSheet = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("camera_add_sound_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Add Sound",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedSound?.title ?: "Add sound",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Magic Effects (چمکتا ستارہ) Glowing Capsule/Circle
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFBA55D3),
                                    Color(0xFFFF1493),
                                    Color(0xFF8A2BE2),
                                    Color(0xFFBA55D3)
                                )
                            )
                        )
                        .border(
                            width = if (isMagicFxActive) 2.5.dp else 1.2.dp,
                            color = if (isMagicFxActive) Color.White else Color(0x88FFFFFF),
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isMagicFxActive = !isMagicFxActive
                        }
                        .testTag("camera_magic_fx_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Magic AI Effects",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 3. Right Vertical Toolbar (Flip, Timer, Duration, Filters, Speed, Expand)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Flip Camera
            RightToolItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                testTag = "camera_flip_tool",
                onClick = { isFrontCamera = !isFrontCamera }
            )

            // Timer (Off, 3s, 10s)
            RightToolItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "Timer",
                            tint = if (timerOption > 0) YtRed else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        if (timerOption > 0) {
                            Text(
                                text = "${timerOption}s",
                                fontSize = 10.sp,
                                color = YtRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                testTag = "camera_timer_tool",
                onClick = {
                    timerOption = when (timerOption) {
                        0 -> 3
                        3 -> 10
                        else -> 0
                    }
                }
            )

            // Duration (15s / 60s)
            RightToolItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${maxDurationSec}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                testTag = "camera_duration_tool",
                onClick = {
                    maxDurationSec = if (maxDurationSec == 15) 60 else 15
                }
            )

            // Filters / Effects
            RightToolItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Filters",
                        tint = if (selectedFilterIndex > 0) Color(0xFFFFD700) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                testTag = "camera_filters_tool",
                onClick = { showFilterSheet = !showFilterSheet }
            )

            // Speed (0.5x, 1x, 2x, 3x)
            RightToolItem(
                icon = {
                    Text(
                        text = "${speedMultiplier.toInt()}x",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                testTag = "camera_speed_tool",
                onClick = {
                    speedMultiplier = when (speedMultiplier) {
                        1.0f -> 2.0f
                        2.0f -> 3.0f
                        3.0f -> 0.5f
                        else -> 1.0f
                    }
                }
            )

            // Expand Chevron (Green Screen, Flash, Retouch, Align)
            RightToolItem(
                icon = {
                    Icon(
                        imageVector = if (isExpandedTools) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand Tools",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                testTag = "camera_expand_tool",
                onClick = { isExpandedTools = !isExpandedTools }
            )

            // Expanded Sub-tools
            AnimatedVisibility(visible = isExpandedTools) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Flash / Torch
                    RightToolItem(
                        icon = {
                            Icon(
                                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash",
                                tint = if (isFlashOn) Color(0xFFFFD700) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        testTag = "tool_flash",
                        onClick = { isFlashOn = !isFlashOn }
                    )

                    // Retouch / Beauty
                    RightToolItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Retouch",
                                tint = if (isRetouchActive) Color(0xFFFF69B4) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        testTag = "tool_retouch",
                        onClick = { isRetouchActive = !isRetouchActive }
                    )

                    // Align / Grid
                    RightToolItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Grid Align",
                                tint = if (isGridActive) YtRed else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        testTag = "tool_grid",
                        onClick = { isGridActive = !isGridActive }
                    )
                }
            }
        }

        // 4. Horizontal Filter Carousel (if filter sheet opened)
        AnimatedVisibility(
            visible = showFilterSheet,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x99000000))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filterOptions.indices.toList()) { index ->
                    val filter = filterOptions[index]
                    val isSelected = selectedFilterIndex == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedFilterIndex = index }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) YtRed else Color.White,
                                    shape = CircleShape
                                )
                                .background(
                                    when (filter.name) {
                                        "Glam" -> Color(0xFFFF69B4)
                                        "Cyber" -> Color(0xFF00CED1)
                                        "Mono" -> Color(0xFF888888)
                                        "Warm" -> Color(0xFFFFA500)
                                        "Sepia" -> Color(0xFFD2B48C)
                                        "Sunset" -> Color(0xFFFF4500)
                                        else -> Color(0xFF333333)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = filter.name,
                            color = if (isSelected) YtRed else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // 5. Bottom Controls & Mode Selector
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Shutter / Add / Drafts Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: "Add" (Gallery thumbnail / picker)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        .testTag("camera_add_gallery")
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                            .background(Color(0xFF222222)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Add Gallery Media",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Center: Big Red Record Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (isRecording) {
                                stopRecording()
                            } else {
                                startRecordingFlow()
                            }
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        // Morphing Red Square for recording
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(YtRed)
                        )
                    } else {
                        // Solid Red Circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(YtRed)
                        )
                    }
                }

                // Right: "Drafts" Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showDraftsSheet = true
                        }
                        .testTag("camera_drafts_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                            .background(Color(0xFF222222)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = "Drafts",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Drafts",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mode Selector: Video, Short, Live, Post
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val modes = listOf("Video", "Short", "Live", "Post")
                modes.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .then(
                                if (isSelected) {
                                    Modifier.background(Color(0xFF333333))
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                selectedMode = mode
                                if (mode == "Video") {
                                    maxDurationSec = 60
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("mode_${mode.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode,
                            color = if (isSelected) Color.White else Color(0x99FFFFFF),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        // 6. Sound Selection Modal Bottom Sheet
        if (showSoundSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSoundSheet = false },
                containerColor = YtSurfaceDark,
                contentColor = YtTextPrimary,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Add sound",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(popularSounds) { track ->
                            val isChosen = selectedSound?.id == track.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isChosen) YtSurfaceVariant else Color.Transparent)
                                    .clickable {
                                        selectedSound = track
                                        showSoundSheet = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF333333)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = YtRed
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = track.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${track.artist} • ${track.duration}",
                                            color = YtTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = YtRed
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (selectedSound != null) {
                        TextButton(
                            onClick = {
                                selectedSound = null
                                showSoundSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove Sound", color = YtRed)
                        }
                    }
                }
            }
        }

        // 7. Drafts Bottom Sheet
        if (showDraftsSheet) {
            val allVideos = viewModel.allVideos.value
            val drafts = allVideos.filter { it.isShort || it.id.startsWith("short_") }

            ModalBottomSheet(
                onDismissRequest = { showDraftsSheet = false },
                containerColor = YtSurfaceDark,
                contentColor = YtTextPrimary,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Saved Drafts (${drafts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (drafts.isEmpty()) {
                        Text(
                            text = "No recorded drafts yet. Record your first short to save drafts!",
                            color = YtTextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(drafts) { draft ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(YtSurfaceVariant)
                                        .clickable {
                                            showDraftsSheet = false
                                            viewModel.playVideo(draft)
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp, 70.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF222222)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = draft.displayTitle,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = draft.hashtags,
                                            color = YtTextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = draft.formattedDuration,
                                            color = YtRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Save & Publish Recorded Short Dialog (binds directly to Room metadata)
        if (showSaveDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = YtSurfaceDark,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "New Short Created! 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Duration: ${pendingVideoDurationMs / 1000}s • ${selectedSound?.title ?: "Original Audio"}",
                            fontSize = 12.sp,
                            color = YtTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = shortTitleInput,
                            onValueChange = { shortTitleInput = it },
                            label = { Text("Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YtRed,
                                unfocusedBorderColor = YtBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = shortHashtagsInput,
                            onValueChange = { shortHashtagsInput = it },
                            label = { Text("Hashtags") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = YtRed,
                                unfocusedBorderColor = YtBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showSaveDialog = false }) {
                                Text("Discard", color = YtTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showSaveDialog = false
                                    viewModel.saveRecordedShort(
                                        title = shortTitleInput.trim().ifEmpty { "My New Short" },
                                        hashtags = shortHashtagsInput.trim(),
                                        durationMs = pendingVideoDurationMs,
                                        audioTitle = selectedSound?.title ?: ""
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = YtRed),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Upload & Publish", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RightToolItem(
    icon: @Composable () -> Unit,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0x66000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
