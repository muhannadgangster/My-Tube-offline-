package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.liquidGlassSurface
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import com.example.data.SettingsManager
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

import com.example.data.InterfaceBuilderManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    interfaceBuilderManager: InterfaceBuilderManager? = null,
    onBack: () -> Unit,
    onClearWatchHistory: () -> Unit,
    onClearSearchHistory: () -> Unit,
    onDeleteAllDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    // State collections
    val remindBreak by settingsManager.remindBreak.collectAsStateWithLifecycle()
    val breakIntervalMinutes by settingsManager.breakIntervalMinutes.collectAsStateWithLifecycle()
    val remindBedtime by settingsManager.remindBedtime.collectAsStateWithLifecycle()
    val bedtimeHour by settingsManager.bedtimeHour.collectAsStateWithLifecycle()
    val darkTheme by settingsManager.darkTheme.collectAsStateWithLifecycle()
    val uiTheme by settingsManager.uiTheme.collectAsStateWithLifecycle()
    val doubleTapSeekSeconds by settingsManager.doubleTapSeekSeconds.collectAsStateWithLifecycle()
    val zoomToFill by settingsManager.zoomToFill.collectAsStateWithLifecycle()
    val playbackInFeeds by settingsManager.playbackInFeeds.collectAsStateWithLifecycle()
    val uploadQuality by settingsManager.uploadQuality.collectAsStateWithLifecycle()
    val appLanguage by settingsManager.appLanguage.collectAsStateWithLifecycle()

    val autoplayNext by settingsManager.autoplayNext.collectAsStateWithLifecycle()
    val defaultSpeed by settingsManager.defaultSpeed.collectAsStateWithLifecycle()
    val videoQuality by settingsManager.videoQuality.collectAsStateWithLifecycle()
    val captionsEnabled by settingsManager.captionsEnabled.collectAsStateWithLifecycle()
    val captionSize by settingsManager.captionSize.collectAsStateWithLifecycle()

    val downloadQuality by settingsManager.downloadQuality.collectAsStateWithLifecycle()
    val downloadWifiOnly by settingsManager.downloadWifiOnly.collectAsStateWithLifecycle()
    val smartDownloads by settingsManager.smartDownloads.collectAsStateWithLifecycle()

    val pauseHistory by settingsManager.pauseHistory.collectAsStateWithLifecycle()
    val pauseSearchHistory by settingsManager.pauseSearchHistory.collectAsStateWithLifecycle()

    val notificationsEnabled by settingsManager.notificationsEnabled.collectAsStateWithLifecycle()
    val scheduledDigest by settingsManager.scheduledDigest.collectAsStateWithLifecycle()
    val subscriptionsNotifications by settingsManager.subscriptionsNotifications.collectAsStateWithLifecycle()
    val recommendedNotifications by settingsManager.recommendedNotifications.collectAsStateWithLifecycle()

    val experimentalAiClips by settingsManager.experimentalAiClips.collectAsStateWithLifecycle()
    val experimentalAudioBooster by settingsManager.experimentalAudioBooster.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val glassConfig = LocalLiquidGlassConfig.current
    val customAccentColorHex by settingsManager.customAccentColorHex.collectAsStateWithLifecycle()
    val customBackgroundUri by settingsManager.customBackgroundUri.collectAsStateWithLifecycle()
    val customBackgroundDim by settingsManager.customBackgroundDim.collectAsStateWithLifecycle()
    val ambientNeonLighting by settingsManager.ambientNeonLighting.collectAsStateWithLifecycle()

    // Liquid Glass state collections
    val lgMaster by settingsManager.liquidGlassMaster.collectAsStateWithLifecycle()
    val lgIntensity by settingsManager.liquidGlassIntensity.collectAsStateWithLifecycle()
    val lgBlur by settingsManager.liquidGlassBlur.collectAsStateWithLifecycle()
    val lgTransparency by settingsManager.liquidGlassTransparency.collectAsStateWithLifecycle()
    val lgRefraction by settingsManager.liquidGlassRefraction.collectAsStateWithLifecycle()
    val lgSpecular by settingsManager.liquidGlassSpecular.collectAsStateWithLifecycle()
    val lgBorderThickness by settingsManager.liquidGlassBorderThickness.collectAsStateWithLifecycle()

    // Neon Lights state collections
    val neonMaster by settingsManager.neonLightsMaster.collectAsStateWithLifecycle()
    val neonColorHex by settingsManager.neonColorHex.collectAsStateWithLifecycle()
    val neonIntensity by settingsManager.neonIntensity.collectAsStateWithLifecycle()
    val neonAnimated by settingsManager.neonAnimated.collectAsStateWithLifecycle()
    val neonSpeed by settingsManager.neonAnimationSpeed.collectAsStateWithLifecycle()

    // Layout states
    val showHomeTopShorts by settingsManager.showHomeTopShorts.collectAsStateWithLifecycle()
    val showShortsActionRemix by settingsManager.showShortsActionRemix.collectAsStateWithLifecycle()
    val showShortsActionSave by settingsManager.showShortsActionSave.collectAsStateWithLifecycle()
    val playerControlsSize by settingsManager.playerControlsSize.collectAsStateWithLifecycle()
    val iconStyleMode by settingsManager.iconStyleMode.collectAsStateWithLifecycle()

    var showRestartDialog by remember { mutableStateOf(false) }

    // Photo Picker Launcher with persistent file copying (Guaranteed to persist across app closes)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val targetFile = File(context.filesDir, "custom_user_background.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                val localSavedUri = Uri.fromFile(targetFile).toString()
                settingsManager.setCustomBackgroundUri(localSavedUri)
            } catch (e: Exception) {
                settingsManager.setCustomBackgroundUri(uri.toString())
            }
        }
    }

    // Dialog control states
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSeekDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showBreakDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadQualityDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showDeleteDownloadsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showNoticeMessage by remember { mutableStateOf<String?>(null) }
    var showCustomizeInterfaceScreen by remember { mutableStateOf(false) }

    if (showCustomizeInterfaceScreen) {
        BackHandler {
            showCustomizeInterfaceScreen = false
        }
        val actualBuilderManager = interfaceBuilderManager ?: remember { InterfaceBuilderManager(context) }
        CustomizeInterfaceScreen(
            settingsManager = settingsManager,
            interfaceBuilderManager = actualBuilderManager,
            onBack = { showCustomizeInterfaceScreen = false },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YtDarkBackground)
    ) {
        // YouTube Settings Header
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    color = YtTextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = YtDarkBackground
            ),
            modifier = Modifier.statusBarsPadding()
        )

        HorizontalDivider(color = YtBorder, thickness = 0.5.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Prominent Customize Interface Studio Entry Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showCustomizeInterfaceScreen = true }
                        .then(
                            if (lgMaster) {
                                Modifier.liquidGlassSurface(
                                    shape = RoundedCornerShape(14.dp),
                                    config = glassConfig,
                                    accentColor = Color(customAccentColorHex),
                                    elevation = 6.dp
                                )
                            } else {
                                Modifier
                                    .background(YtSurfaceVariant)
                                    .border(1.dp, YtBorder, RoundedCornerShape(14.dp))
                            }
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(customAccentColorHex).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(customAccentColorHex),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Customize Interface Studio",
                                    color = YtTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE ✨",
                                    color = Color(customAccentColorHex),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Customize Home feed, Shorts, Nav bar, Profile, Notifications, GPU Shaders & Physics",
                                color = YtTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = YtTextSecondary
                        )
                    }
                }
            }

            // Account Header Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(YtSurfaceDark)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(YtAvatarPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Muhannad Murtaza",
                            color = YtTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "@MuhannadMurtaza-t3v",
                            color = YtTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Google Account / Offline Profile",
                            color = YtBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Section 0: Appearance, Colors & Wallpapers
            item { SettingsSectionHeader("Appearance & Customization") }

            // 1. Change App Red Color (Subscribe, Like, Progress, Badges)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "App Accent Color (Change Red Color)",
                        color = YtTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Changes Subscribe button, Like button, Progress bars, and Badges",
                        color = YtTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsManager.ACCENT_COLOR_OPTIONS.forEach { (name, hex) ->
                            val isSelected = (customAccentColorHex == hex)
                            val color = Color(hex)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { settingsManager.setCustomAccentColorHex(hex) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                            else Modifier
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
                                    text = name.split(" ").first(),
                                    color = if (isSelected) YtTextPrimary else YtTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 2. Custom Background Wallpaper (Persists across app restart)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Custom Background Wallpaper",
                        color = YtTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Set your own photo as app wallpaper. Persists even after closing and reopening the app.",
                        color = YtTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!customBackgroundUri.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(Uri.parse(customBackgroundUri))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Current wallpaper",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, YtBorder, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = YtSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = YtTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (customBackgroundUri.isNullOrBlank()) "Choose Photo 🖼️" else "Change Photo 🖼️",
                                color = YtTextPrimary,
                                fontSize = 13.sp
                            )
                        }

                        if (!customBackgroundUri.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    try {
                                        File(context.filesDir, "custom_user_background.jpg").delete()
                                    } catch (_: Exception) {}
                                    settingsManager.setCustomBackgroundUri(null)
                                }
                            ) {
                                Text("Remove", color = YtRed, fontSize = 12.sp)
                            }
                        }
                    }

                    if (!customBackgroundUri.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Wallpaper Dimming (Text Contrast)",
                                color = YtTextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${(customBackgroundDim * 100).toInt()}%",
                                color = YtTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = customBackgroundDim,
                            onValueChange = { settingsManager.setCustomBackgroundDim(it) },
                            valueRange = 0.25f..0.95f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(customAccentColorHex),
                                activeTrackColor = Color(customAccentColorHex)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ==========================================
            // 3. FULL-APP LIQUID GLASS ENGINE CONTROLS
            // ==========================================
            item { SettingsSectionHeader("Liquid Glass Engine (iOS Glassmorphism)") }

            item {
                SettingsSwitchRow(
                    title = "Master Liquid Glass Engine",
                    subtitle = if (lgMaster) "Translucent glass surfaces, specular bevels & reflections (Active)" else "Standard YouTube flat theme (Off)",
                    checked = lgMaster,
                    onCheckedChange = { isEnabled ->
                        settingsManager.setLiquidGlassMaster(isEnabled)
                        Toast.makeText(
                            context,
                            if (isEnabled) "Liquid Glass Mode Enabled ✨" else "Liquid Glass Mode Disabled (Conventional UI)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }

            if (lgMaster) {
                // Glass Intensity Slider
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Glass Depth & Opacity", color = YtTextPrimary, fontSize = 14.sp)
                            Text("${(lgIntensity * 100).toInt()}%", color = Color(customAccentColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = lgIntensity,
                            onValueChange = { settingsManager.setLiquidGlassIntensity(it) },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(customAccentColorHex),
                                activeTrackColor = Color(customAccentColorHex)
                            )
                        )
                    }
                }

                // Refraction / Optical Caustics Slider
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Refraction & Edge Dispersion", color = YtTextPrimary, fontSize = 14.sp)
                            Text("${(lgRefraction * 100).toInt()}%", color = Color(customAccentColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = lgRefraction,
                            onValueChange = { settingsManager.setLiquidGlassRefraction(it) },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(customAccentColorHex),
                                activeTrackColor = Color(customAccentColorHex)
                            )
                        )
                    }
                }

                // Transparency Slider
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Transparency Level", color = YtTextPrimary, fontSize = 14.sp)
                            Text("${(lgTransparency * 100).toInt()}%", color = Color(customAccentColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = lgTransparency,
                            onValueChange = { settingsManager.setLiquidGlassTransparency(it) },
                            valueRange = 0.3f..0.95f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(customAccentColorHex),
                                activeTrackColor = Color(customAccentColorHex)
                            )
                        )
                    }
                }

                // Specular highlight toggle
                item {
                    SettingsSwitchRow(
                        title = "Specular Bevel Highlights",
                        subtitle = "Curved optical lens reflections on top and left borders",
                        checked = lgSpecular,
                        onCheckedChange = { settingsManager.setLiquidGlassSpecular(it) }
                    )
                }

                // Border Thickness Slider
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Luminous Border Thickness", color = YtTextPrimary, fontSize = 14.sp)
                            Text("${String.format("%.1f", lgBorderThickness)} dp", color = Color(customAccentColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = lgBorderThickness,
                            onValueChange = { settingsManager.setLiquidGlassBorderThickness(it) },
                            valueRange = 0.6f..3.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(customAccentColorHex),
                                activeTrackColor = Color(customAccentColorHex)
                            )
                        )
                    }
                }
            }

            // ==========================================
            // 4. NEON LIGHTS SYSTEM CONTROLS
            // ==========================================
            item { SettingsSectionHeader("Neon Lights & Glowing Accents") }

            item {
                SettingsSwitchRow(
                    title = "Master Neon Lights System",
                    subtitle = if (neonMaster) "Radiant neon outlines, button halos & ambient edge lighting (Active)" else "Standard subdued lighting (Off)",
                    checked = neonMaster,
                    onCheckedChange = { settingsManager.setNeonLightsMaster(it) }
                )
            }

            if (neonMaster) {
                // Neon Color Palette Selection
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Neon Light Color", color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        val neonColors = listOf(
                            "Cyan" to 0xFF00E5FFL,
                            "Magenta" to 0xFFFF007FL,
                            "Lime" to 0xFF00E676L,
                            "Purple" to 0xFFD500F9L,
                            "Amber" to 0xFFFFD600L,
                            "Flame" to 0xFFFF3D00L,
                            "Blue" to 0xFF2979FFL
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            neonColors.forEach { (name, hex) ->
                                val isSelected = neonColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(hex))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { settingsManager.setNeonColorHex(hex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Neon Intensity
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Neon Glow Intensity", color = YtTextPrimary, fontSize = 14.sp)
                            Text("${(neonIntensity * 100).toInt()}%", color = Color(neonColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = neonIntensity,
                            onValueChange = { settingsManager.setNeonIntensity(it) },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(neonColorHex),
                                activeTrackColor = Color(neonColorHex)
                            )
                        )
                    }
                }

                // Neon Animated Mode
                item {
                    SettingsSwitchRow(
                        title = "Animated Pulsing Neon",
                        subtitle = "Dynamic luminous light sweeps and pulsing edge halos",
                        checked = neonAnimated,
                        onCheckedChange = { settingsManager.setNeonAnimated(it) }
                    )
                }
            }

            // ==========================================
            // 5. CUSTOMIZE APP & INTERFACE STUDIO
            // ==========================================
            item { SettingsSectionHeader("Customize Interface & Layout Studio") }

            item {
                SettingsClickableRow(
                    title = "Customize Interface → Interface Builder",
                    value = "Dashboard & Pages ✨",
                    onClick = { showCustomizeInterfaceScreen = true }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Home Top Shorts Shelf",
                    subtitle = if (showHomeTopShorts) "Display 6-Shorts shelf at top of home feed" else "Start immediately with regular video cards",
                    checked = showHomeTopShorts,
                    onCheckedChange = { settingsManager.setShowHomeTopShorts(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Shorts Action: Remix Button",
                    subtitle = "Show Remix / Audio creation button in vertical Shorts toolbar",
                    checked = showShortsActionRemix,
                    onCheckedChange = { settingsManager.setShowShortsActionRemix(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Shorts Action: Save / Bookmark Button",
                    subtitle = "Show Bookmark / Save button in vertical Shorts toolbar",
                    checked = showShortsActionSave,
                    onCheckedChange = { settingsManager.setShowShortsActionSave(it) }
                )
            }

            // Player controls size scaling
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Video Player Controls Scale", color = YtTextPrimary, fontSize = 14.sp)
                        Text("${(playerControlsSize * 100).toInt()}%", color = Color(customAccentColorHex), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = playerControlsSize,
                        onValueChange = { settingsManager.setPlayerControlsSize(it) },
                        valueRange = 0.8f..1.4f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(customAccentColorHex),
                            activeTrackColor = Color(customAccentColorHex)
                        )
                    )
                }
            }

            // Reset All Appearance Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            settingsManager.resetAllAppearance()
                            showNoticeMessage = "Appearance restored to factory defaults!"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset All Appearance to Default", fontSize = 13.sp)
                    }
                }
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                SettingsClickableRow(
                    title = "UI Theme",
                    value = uiTheme,
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Dark theme (DataStore)",
                    subtitle = if (darkTheme) "Pure black #0F0F0F dark mode (Active)" else "Light theme (Active)",
                    checked = darkTheme,
                    onCheckedChange = { settingsManager.setDarkTheme(it) }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Double-tap to seek",
                    value = "$doubleTapSeekSeconds seconds",
                    onClick = { showSeekDialog = true }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Zoom to fill screen",
                    subtitle = "Always stretch and fill video viewport",
                    checked = zoomToFill,
                    onCheckedChange = { settingsManager.setZoomToFill(it) }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Playback in feeds",
                    value = playbackInFeeds,
                    onClick = {
                        val next = if (playbackInFeeds == "Always on") "Wi-Fi only" else if (playbackInFeeds == "Wi-Fi only") "Off" else "Always on"
                        settingsManager.setPlaybackInFeeds(next)
                    }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Upload quality",
                    value = uploadQuality,
                    onClick = {
                        val next = if (uploadQuality.contains("1080p")) "Original" else if (uploadQuality == "Original") "720p" else "Full HD (1080p)"
                        settingsManager.setUploadQuality(next)
                    }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 2: Autoplay & Playback
            item { SettingsSectionHeader("Autoplay & Playback") }

            item {
                SettingsSwitchRow(
                    title = "Autoplay next video",
                    subtitle = "When you finish a video, another plays automatically",
                    checked = autoplayNext,
                    onCheckedChange = { settingsManager.setAutoplayNext(it) }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Playback speed (DataStore)",
                    value = "${defaultSpeed}x ${if (defaultSpeed == 1.0f) "(Normal)" else ""}",
                    onClick = { showSpeedDialog = true }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 3: Video Quality Preferences
            item { SettingsSectionHeader("Video quality preferences") }

            item {
                SettingsClickableRow(
                    title = "Video quality on Wi-Fi and mobile",
                    value = videoQuality,
                    onClick = { showQualityDialog = true }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Closed Captions (CC)",
                    subtitle = if (captionsEnabled) "Size: $captionSize" else "Off",
                    checked = captionsEnabled,
                    onCheckedChange = { settingsManager.setCaptionsEnabled(it) }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 4: Downloads
            item { SettingsSectionHeader("Downloads") }

            item {
                SettingsClickableRow(
                    title = "Download quality",
                    value = downloadQuality,
                    onClick = { showDownloadQualityDialog = true }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Download over Wi-Fi only",
                    subtitle = "Save mobile cellular data",
                    checked = downloadWifiOnly,
                    onCheckedChange = { settingsManager.setDownloadWifiOnly(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Smart downloads",
                    subtitle = "Auto-download videos based on your recommendations",
                    checked = smartDownloads,
                    onCheckedChange = { settingsManager.setSmartDownloads(it) }
                )
            }

            // Available storage indicator bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Available device storage",
                        color = YtTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 0.42f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = YtBlue,
                        trackColor = YtSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "MyTube: 1.8 GB", color = YtTextSecondary, fontSize = 11.sp)
                        Text(text = "48.2 GB free of 128 GB", color = YtTextSecondary, fontSize = 11.sp)
                    }
                }
            }

            item {
                SettingsActionRow(
                    icon = Icons.Default.DeleteSweep,
                    title = "Delete all downloads",
                    color = YtRed,
                    onClick = { showDeleteDownloadsDialog = true }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 5: Privacy & History
            item { SettingsSectionHeader("Privacy & History") }

            item {
                SettingsActionRow(
                    icon = Icons.Default.History,
                    title = "Clear watch history",
                    color = YtTextPrimary,
                    onClick = { showClearHistoryDialog = true }
                )
            }

            item {
                SettingsActionRow(
                    icon = Icons.Default.Tune,
                    title = "Clear search history",
                    color = YtTextPrimary,
                    onClick = {
                        onClearSearchHistory()
                        showNoticeMessage = "Search history cleared"
                    }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Pause watch history",
                    subtitle = "Don't save watched videos to History feed",
                    checked = pauseHistory,
                    onCheckedChange = { settingsManager.setPauseHistory(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Pause search history",
                    subtitle = "Don't record future search queries",
                    checked = pauseSearchHistory,
                    onCheckedChange = { settingsManager.setPauseSearchHistory(it) }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 6: Experimental Features
            item { SettingsSectionHeader("Try experimental new features") }

            item {
                SettingsSwitchRow(
                    title = "Smart AI 5-10s Auto-Clipping",
                    subtitle = "Auto-highlight best video moments into Shorts feed",
                    checked = experimentalAiClips,
                    onCheckedChange = { settingsManager.setExperimentalAiClips(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Ken Burns Photo Slideshows",
                    subtitle = "Animate local gallery photos into vertical video stories",
                    checked = true,
                    enabled = false,
                    onCheckedChange = {}
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Enhanced Audio Booster",
                    subtitle = "Boost dialogue clarity for offline videos",
                    checked = experimentalAudioBooster,
                    onCheckedChange = { settingsManager.setExperimentalAudioBooster(it) }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 7: Notifications
            item { SettingsSectionHeader("Notifications") }

            item {
                SettingsSwitchRow(
                    title = "Notifications (DataStore)",
                    subtitle = if (notificationsEnabled) "Master notifications enabled" else "All notifications muted",
                    checked = notificationsEnabled,
                    onCheckedChange = { settingsManager.setNotificationsEnabled(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Subscriptions activity (DataStore)",
                    subtitle = "Updates from subscribed channels",
                    checked = subscriptionsNotifications && notificationsEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { settingsManager.setSubscriptionsNotifications(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Recommended videos (DataStore)",
                    subtitle = "Notify about videos you might like",
                    checked = recommendedNotifications && notificationsEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { settingsManager.setRecommendedNotifications(it) }
                )
            }

            item {
                SettingsSwitchRow(
                    title = "Scheduled digest (DataStore)",
                    subtitle = "Daily digest at 7:00 PM",
                    checked = scheduledDigest && notificationsEnabled,
                    enabled = notificationsEnabled,
                    onCheckedChange = { settingsManager.setScheduledDigest(it) }
                )
            }

            item { HorizontalDivider(color = YtBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp)) }

            // Section 8: About
            item { SettingsSectionHeader("About") }

            item {
                SettingsClickableRow(
                    title = "App version",
                    value = "MyTube 2026.09 (v2.4.0)",
                    onClick = { showAboutDialog = true }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Terms of Service",
                    value = "",
                    onClick = { showTermsDialog = true }
                )
            }

            item {
                SettingsClickableRow(
                    title = "Open source licenses",
                    value = "",
                    onClick = {
                        showNoticeMessage = "Licensed under Apache 2.0 & Android Open Source Project"
                    }
                )
            }
        }
    }

    // --- POPUP DIALOGS ---

    // Seek Duration Dialog
    if (showSeekDialog) {
        val options = listOf(5, 10, 15, 20, 30, 60)
        RadioSelectionDialog(
            title = "Double-tap to seek",
            options = options.map { "$it seconds" },
            selectedIndex = options.indexOf(doubleTapSeekSeconds).coerceAtLeast(0),
            onDismiss = { showSeekDialog = false },
            onSelect = { index ->
                settingsManager.setDoubleTapSeekSeconds(options[index])
                showSeekDialog = false
            }
        )
    }

    // Playback Speed Dialog (DataStore)
    if (showSpeedDialog) {
        val speedValues = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
        val speedLabels = listOf("0.25x", "0.5x", "0.75x", "Normal (1.0x)", "1.25x", "1.5x", "1.75x", "2.0x")
        val currentIdx = speedValues.indexOfFirst { kotlin.math.abs(it - defaultSpeed) < 0.05f }.let { if (it >= 0) it else 3 }

        RadioSelectionDialog(
            title = "Playback Speed (DataStore)",
            options = speedLabels,
            selectedIndex = currentIdx,
            onDismiss = { showSpeedDialog = false },
            onSelect = { index ->
                settingsManager.setDefaultSpeed(speedValues[index])
                showSpeedDialog = false
            }
        )
    }

    // UI Theme Selection Dialog
    if (showThemeDialog) {
        val themeOptions = SettingsManager.THEME_OPTIONS
        RadioSelectionDialog(
            title = "UI Theme",
            options = themeOptions,
            selectedIndex = themeOptions.indexOf(uiTheme).coerceAtLeast(0),
            onDismiss = { showThemeDialog = false },
            onSelect = { index ->
                settingsManager.setUiTheme(themeOptions[index])
                showThemeDialog = false
            }
        )
    }

    // Break Reminder Dialog
    if (showBreakDialog) {
        val options = listOf(15, 30, 60, 90, 120)
        RadioSelectionDialog(
            title = "Reminder frequency",
            options = options.map { "Every $it minutes" },
            selectedIndex = options.indexOf(breakIntervalMinutes).coerceAtLeast(0),
            onDismiss = { showBreakDialog = false },
            onSelect = { index ->
                settingsManager.setBreakInterval(options[index])
                showBreakDialog = false
            }
        )
    }

    // Video Quality Dialog
    if (showQualityDialog) {
        val options = listOf("Auto (recommended)", "Higher picture quality", "Data saver")
        RadioSelectionDialog(
            title = "Video Quality Preference",
            options = options,
            selectedIndex = options.indexOf(videoQuality).coerceAtLeast(0),
            onDismiss = { showQualityDialog = false },
            onSelect = { index ->
                settingsManager.setVideoQuality(options[index])
                showQualityDialog = false
            }
        )
    }

    // Download Quality Dialog
    if (showDownloadQualityDialog) {
        val options = listOf("Full HD (1080p)", "High (720p)", "Medium (360p)", "Ask each time")
        RadioSelectionDialog(
            title = "Download quality",
            options = options,
            selectedIndex = options.indexOf(downloadQuality).coerceAtLeast(0),
            onDismiss = { showDownloadQualityDialog = false },
            onSelect = { index ->
                settingsManager.setDownloadQuality(options[index])
                showDownloadQualityDialog = false
            }
        )
    }

    // Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("Clear watch history?", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Your watch history will be cleared from this device offline. You will lose your current watch progress on all videos.",
                    color = YtTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onClearWatchHistory()
                    showClearHistoryDialog = false
                    showNoticeMessage = "Watch history cleared"
                }) {
                    Text("Clear Watch History", color = YtRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = YtTextSecondary)
                }
            }
        )
    }

    // Delete All Downloads Confirmation Dialog
    if (showDeleteDownloadsDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDownloadsDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("Delete all downloads?", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will remove all downloaded videos from your offline library list.",
                    color = YtTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteAllDownloads()
                    showDeleteDownloadsDialog = false
                    showNoticeMessage = "All downloads deleted"
                }) {
                    Text("Delete All", color = YtRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDownloadsDialog = false }) {
                    Text("Cancel", color = YtTextSecondary)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("About MyTube", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("MyTube 2026.09 (v2.4.0-universal)", color = YtTextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "100% Offline YouTube Media Player & Shorts Studio for Android. Plays local gallery videos and animates photos with Ken Burns effect.",
                        color = YtTextSecondary,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK", color = YtBlue)
                }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("Terms of Service", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "MyTube operates entirely on-device offline. Your local media files, custom titles, playlists, and viewing history remain strictly private on your phone.",
                    color = YtTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close", color = YtBlue)
                }
            }
        )
    }

    // Safe Restart App Confirmation Dialog
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("Restart MyTube?", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Your appearance settings have been saved persistently. Would you like to restart MyTube now to apply the complete Liquid Glass interface across all screens?",
                    color = YtTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartDialog = false
                        try {
                            val packageManager = context.packageManager
                            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                            val componentName = intent?.component
                            val restartIntent = Intent.makeRestartActivityTask(componentName)
                            context.startActivity(restartIntent)
                            Runtime.getRuntime().exit(0)
                        } catch (e: Exception) {
                            showNoticeMessage = "Settings saved! Liquid Glass will be fully applied on your next startup."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = YtBlue)
                ) {
                    Text("Restart Now", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) {
                    Text("Later", color = YtTextSecondary)
                }
            }
        )
    }

    // Notice Toast / Snackbar dialog
    if (showNoticeMessage != null) {
        AlertDialog(
            onDismissRequest = { showNoticeMessage = null },
            containerColor = YtSurfaceDark,
            title = { Text("Settings", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(showNoticeMessage!!, color = YtTextSecondary, fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { showNoticeMessage = null }) {
                    Text("OK", color = YtBlue)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = YtBlue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) YtTextPrimary else YtTextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = YtTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = YtBlue,
                uncheckedThumbColor = YtTextSecondary,
                uncheckedTrackColor = YtSurfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsClickableRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = YtTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (value.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = YtTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = YtTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RadioSelectionDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = YtSurfaceDark,
        shape = RoundedCornerShape(16.dp),
        title = { Text(text = title, color = YtTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (index == selectedIndex),
                                onClick = { onSelect(index) }
                            )
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedIndex),
                            onClick = { onSelect(index) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = YtBlue,
                                unselectedColor = YtTextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = option,
                            color = YtTextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = YtTextSecondary)
            }
        }
    )
}
