package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InterfaceBuilderManager
import com.example.data.SettingsManager
import com.example.data.model.ActionTypes
import com.example.data.model.ComponentTypes
import com.example.data.model.CustomIconItem
import com.example.data.model.CustomPageComponent
import com.example.data.model.CustomPageDefinition
import com.example.data.model.IconLocations
import com.example.ui.theme.LiquidPhysics
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.LocalUiTheme
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtGlassDarkBackground
import com.example.ui.theme.YtGlassDarkBorder
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import com.example.ui.theme.liquidGlassSurface
import com.example.ui.components.LiquidGlassSubscribeButton
import com.example.ui.components.GlassSurface
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassVideoReflection
import com.example.ui.components.GlassSlider
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Speed
import com.example.util.IconCatalog
import java.util.UUID

/**
 * Settings -> Customize Interface Screen:
 * Full Offline Interface Builder Dashboard + A-to-Z UI Customization Studio.
 *
 * 1. SETTINGS-BASED DASHBOARD:
 *    - Edit Application Layout
 *    - Manage Icons and Buttons
 *    - Customize Top Bars
 *    - Customize Navigation
 *    - Manage Pages
 *    - Create Custom Page
 *    - Add Custom Icon
 *    - Manage UI Elements
 *    - Customize Video Player
 *    - Customize Profile Interface
 *    - Manage Themes
 *    - Save and Apply Changes
 *    - Preview Changes
 *    - Reset Layout
 *
 * 2. LIVE SECTIONAL CONTROLS:
 *    - Home Feed, Shorts, Navigation, Profile, Notifications, Shaders & Spring Physics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeInterfaceScreen(
    settingsManager: SettingsManager,
    interfaceBuilderManager: InterfaceBuilderManager,
    onBack: () -> Unit,
    initialCategory: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableIntStateOf(initialCategory) }
    var activeBuilderSection by remember { mutableStateOf<String?>(null) } // null = dashboard

    // Visual Custom Page Editor states
    var editingPageId by remember { mutableStateOf<String?>(null) }
    var isCreatingPage by remember { mutableStateOf(false) }
    var previewingPage by remember { mutableStateOf<CustomPageDefinition?>(null) }

    // Dialog states
    var showAddIconDialog by remember { mutableStateOf(false) }
    var editingIconItem by remember { mutableStateOf<CustomIconItem?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSaveToastMessage by remember { mutableStateOf<String?>(null) }

    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current
    val currentTheme = LocalUiTheme.current
    val lgMaster by settingsManager.liquidGlassMaster.collectAsStateWithLifecycle()
    val isGlassTheme = lgMaster

    // State collections from InterfaceBuilderManager
    val customIcons by interfaceBuilderManager.customIcons.collectAsStateWithLifecycle()
    val customPages by interfaceBuilderManager.customPages.collectAsStateWithLifecycle()

    // State collections from SettingsManager
    val homeCardCornerRadius by settingsManager.homeCardCornerRadius.collectAsStateWithLifecycle()
    val homeCardSpacing by settingsManager.homeCardSpacing.collectAsStateWithLifecycle()
    val homeCardPadding by settingsManager.homeCardPadding.collectAsStateWithLifecycle()
    val homeGlassCards by settingsManager.homeGlassCards.collectAsStateWithLifecycle()

    val shortsActionButtonStyle by settingsManager.shortsActionButtonStyle.collectAsStateWithLifecycle()
    val shortsActionIconScale by settingsManager.shortsActionIconScale.collectAsStateWithLifecycle()
    val shortsOverlayAlpha by settingsManager.shortsOverlayAlpha.collectAsStateWithLifecycle()

    val navBarHeightDp by settingsManager.navBarHeightDp.collectAsStateWithLifecycle()
    val navBarGlassEnabled by settingsManager.navBarGlassEnabled.collectAsStateWithLifecycle()
    val navBarIndicatorStyle by settingsManager.navBarIndicatorStyle.collectAsStateWithLifecycle()
    val navBarShowLabels by settingsManager.navBarShowLabels.collectAsStateWithLifecycle()

    val profileHeaderGlassStyle by settingsManager.profileHeaderGlassStyle.collectAsStateWithLifecycle()
    val profileAvatarCornerRadius by settingsManager.profileAvatarCornerRadius.collectAsStateWithLifecycle()
    val profileActionButtonsGlass by settingsManager.profileActionButtonsGlass.collectAsStateWithLifecycle()

    val notifCardCornerRadius by settingsManager.notificationCardCornerRadius.collectAsStateWithLifecycle()
    val notifGlassCards by settingsManager.notificationGlassCards.collectAsStateWithLifecycle()
    val notifCardSpacing by settingsManager.notificationCardSpacing.collectAsStateWithLifecycle()

    val opticalShaderEnabled by settingsManager.opticalShaderEnabled.collectAsStateWithLifecycle()
    val opticalCurvaturePower by settingsManager.opticalCurvaturePower.collectAsStateWithLifecycle()
    val opticalChromaticDispersion by settingsManager.opticalChromaticDispersion.collectAsStateWithLifecycle()
    val springPhysicsEnabled by settingsManager.springPhysicsEnabled.collectAsStateWithLifecycle()
    val playerControlsSize by settingsManager.playerControlsSize.collectAsStateWithLifecycle()

    val lgTransparency by settingsManager.liquidGlassTransparency.collectAsStateWithLifecycle()
    val lgBlur by settingsManager.liquidGlassBlur.collectAsStateWithLifecycle()
    val lgCornerRadius by settingsManager.liquidGlassCornerRadius.collectAsStateWithLifecycle()
    val lgRefractionHeight by settingsManager.liquidGlassRefractionHeight.collectAsStateWithLifecycle()
    val lgRefraction by settingsManager.liquidGlassRefraction.collectAsStateWithLifecycle()
    val lgLayerRefraction by settingsManager.liquidGlassLayerRefraction.collectAsStateWithLifecycle()
    val lgDepthEnabled by settingsManager.liquidGlassDepthEnabled.collectAsStateWithLifecycle()
    val lgVibrancy by settingsManager.liquidGlassVibrancy.collectAsStateWithLifecycle()
    val lgEdgeHighlights by settingsManager.liquidGlassEdgeHighlights.collectAsStateWithLifecycle()
    val lgBorderThickness by settingsManager.liquidGlassBorderThickness.collectAsStateWithLifecycle()
    val lgReflection by settingsManager.liquidGlassReflection.collectAsStateWithLifecycle()
    val lgTintColorHex by settingsManager.liquidGlassTintColorHex.collectAsStateWithLifecycle()
    val lgShadowElevation by settingsManager.liquidGlassShadowElevation.collectAsStateWithLifecycle()
    val lgAnimIntensity by settingsManager.liquidGlassAnimationIntensity.collectAsStateWithLifecycle()
    val lgPerfMode by settingsManager.liquidGlassPerformanceMode.collectAsStateWithLifecycle()

    val subGlassEnabled by settingsManager.subscribeGlassEnabled.collectAsStateWithLifecycle()
    val subGlassTintHex by settingsManager.subscribeGlassTintHex.collectAsStateWithLifecycle()
    val subTransparency by settingsManager.subscribeTransparency.collectAsStateWithLifecycle()
    val subCornerRadius by settingsManager.subscribeCornerRadius.collectAsStateWithLifecycle()
    val subDepth by settingsManager.subscribeDepth.collectAsStateWithLifecycle()
    val subRefraction by settingsManager.subscribeRefraction.collectAsStateWithLifecycle()
    val subEdgeHighlight by settingsManager.subscribeEdgeHighlight.collectAsStateWithLifecycle()

    var isGlassAdvancedMode by remember { mutableStateOf(false) }
    var previewIsSubscribed by remember { mutableStateOf(false) }

    // If currently rendering a custom page editor
    if (editingPageId != null || isCreatingPage) {
        CustomPageEditorScreen(
            pageId = editingPageId,
            interfaceBuilderManager = interfaceBuilderManager,
            onBack = {
                editingPageId = null
                isCreatingPage = false
            },
            modifier = modifier
        )
        return
    }

    // If currently previewing a custom page
    if (previewingPage != null) {
        CustomPageRendererScreen(
            page = previewingPage!!,
            onBack = { previewingPage = null },
            modifier = modifier
        )
        return
    }

    // Intercept back key when in a sub-section
    BackHandler {
        if (activeBuilderSection != null) {
            activeBuilderSection = null
        } else {
            onBack()
        }
    }

    val categories = listOf(
        "🛠️ Interface Builder",
        "💎 Liquid Glass",
        "🏠 Home Feed",
        "⚡ Shorts",
        "🧭 Navigation",
        "👤 Profile",
        "🔔 Notifications",
        "✨ Shaders & Physics"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isGlassTheme) YtGlassDarkBackground else YtDarkBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = if (activeBuilderSection != null) "Builder: $activeBuilderSection" else "Customize Interface",
                        color = YtTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeBuilderSection != null) "Tap back to return to dashboard" else "Offline UI Builder & Liquid Glass Studio",
                        color = accentColor,
                        fontSize = 11.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (activeBuilderSection != null) {
                            activeBuilderSection = null
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.testTag("btn_customize_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.testTag("btn_reset_appearance")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = YtTextSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.statusBarsPadding()
        )

        // Clearly Visible Setting: Settings -> Customize Interface -> Liquid Glass Mode
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .then(
                    if (lgMaster) {
                        Modifier.liquidGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            config = glassConfig,
                            accentColor = accentColor,
                            elevation = 6.dp
                        )
                    } else {
                        Modifier
                            .background(YtSurfaceDark)
                            .border(1.dp, YtBorder, RoundedCornerShape(16.dp))
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (lgMaster) accentColor.copy(alpha = 0.22f) else YtSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Liquid Glass Mode",
                        tint = if (lgMaster) accentColor else YtTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Liquid Glass Mode",
                            color = YtTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (lgMaster) accentColor.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (lgMaster) "ON ✨" else "OFF",
                                color = if (lgMaster) accentColor else YtTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (lgMaster) "iOS optical glass & reflections active" else "Conventional interface active",
                        color = YtTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = lgMaster,
                    onCheckedChange = { isChecked ->
                        settingsManager.setLiquidGlassMaster(isChecked)
                        Toast.makeText(
                            context,
                            if (isChecked) "Liquid Glass Mode Enabled ✨" else "Liquid Glass Mode Disabled (Conventional UI)",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = YtTextSecondary,
                        uncheckedTrackColor = YtSurfaceVariant
                    ),
                    modifier = Modifier.testTag("switch_liquid_glass_mode")
                )
            }
        }

        // Category Tabs (Only shown when on the main menu, not in drill-down)
        if (activeBuilderSection == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, label ->
                    val isSelected = selectedCategory == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = index }
                            .then(
                                if (isSelected) {
                                    if (isGlassTheme) {
                                        Modifier.liquidGlassSurface(
                                            shape = RoundedCornerShape(20.dp),
                                            config = glassConfig,
                                            accentColor = accentColor,
                                            elevation = 4.dp
                                        )
                                    } else {
                                        Modifier.background(accentColor)
                                    }
                                } else {
                                    Modifier
                                        .background(YtSurfaceVariant.copy(alpha = 0.6f))
                                        .border(0.6.dp, YtBorder, RoundedCornerShape(20.dp))
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else YtTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
            HorizontalDivider(color = YtBorder, thickness = 0.5.dp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // ==============================================================
            // CATEGORY 0: INTERFACE BUILDER DASHBOARD & DRILL-DOWNS
            // ==============================================================
            if (selectedCategory == 0) {
                when (activeBuilderSection) {
                    null -> {
                        // MAIN INTERFACE BUILDER DASHBOARD (14 Core Sections)
                        item {
                            SectionHeader("Interface Builder Dashboard")
                        }

                        // Hero Banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .then(
                                        if (isGlassTheme) {
                                            Modifier.liquidGlassSurface(
                                                shape = RoundedCornerShape(16.dp),
                                                config = glassConfig,
                                                accentColor = accentColor,
                                                elevation = 6.dp
                                            )
                                        } else {
                                            Modifier
                                                .background(YtSurfaceDark)
                                                .border(1.dp, YtBorder, RoundedCornerShape(16.dp))
                                        }
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(accentColor.copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Tune, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text("Offline Interface Customization", color = YtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Text("Total control over icons, layouts & custom pages", color = YtTextSecondary, fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { isCreatingPage = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Create Page", fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { showAddIconDialog = true },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = accentColor)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add Icon", fontSize = 12.sp, color = YtTextPrimary)
                                        }
                                    }
                                }
                            }
                        }

                        // Dashboard Items Grid / List
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DashboardActionRow(
                                    icon = Icons.Default.Diamond,
                                    title = "Liquid Glass Settings",
                                    subtitle = "Transparency, blur, refraction, depth, bevels & reflection studio",
                                    badge = if (lgMaster) "Active ✨" else "Off",
                                    onClick = { activeBuilderSection = "Liquid Glass" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.ViewAgenda,
                                    title = "Edit Application Layout",
                                    subtitle = "Reorder home sections, cards spacing, and feeds",
                                    badge = null,
                                    onClick = { activeBuilderSection = "Layout" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Widgets,
                                    title = "Manage Icons and Buttons",
                                    subtitle = "Configure, move, reorder or hide custom toolbar buttons",
                                    badge = "${customIcons.size} active",
                                    onClick = { activeBuilderSection = "Icons" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Web,
                                    title = "Customize Top Bars",
                                    subtitle = "Main and Profile toolbar icon alignment and layout",
                                    badge = null,
                                    onClick = { activeBuilderSection = "Top Bars" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Navigation,
                                    title = "Customize Navigation",
                                    subtitle = "Bottom navigation bar height, glass pills, and indicators",
                                    badge = null,
                                    onClick = { activeBuilderSection = "Navigation" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Layers,
                                    title = "Manage Pages",
                                    subtitle = "Edit, duplicate, delete or preview custom created pages",
                                    badge = "${customPages.size} pages",
                                    onClick = { activeBuilderSection = "Pages" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Add,
                                    title = "Create Custom Page",
                                    subtitle = "Design new screens visually with text, cards, and buttons",
                                    badge = "Builder",
                                    onClick = { isCreatingPage = true }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Star,
                                    title = "Add Custom Icon",
                                    subtitle = "Pick from icon catalog, choose location, and link action",
                                    badge = "New",
                                    onClick = { showAddIconDialog = true }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Tune,
                                    title = "Manage UI Elements",
                                    subtitle = "Toggle visibility and sizes of buttons, toolbars, and chips",
                                    badge = null,
                                    onClick = { activeBuilderSection = "Elements" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.PlayCircle,
                                    title = "Customize Video Player",
                                    subtitle = "Adjust controls scale, double-tap seek, and overlays",
                                    badge = "${(playerControlsSize * 100).toInt()}%",
                                    onClick = { activeBuilderSection = "Player" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Person,
                                    title = "Customize Profile Interface",
                                    subtitle = "Header glass style, avatar shape, and stats pills",
                                    badge = null,
                                    onClick = { activeBuilderSection = "Profile" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Palette,
                                    title = "Manage Themes",
                                    subtitle = "Switch between Liquid Glass, AMOLED, Neon, and Light",
                                    badge = currentTheme,
                                    onClick = { activeBuilderSection = "Themes" }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Save,
                                    title = "Save and Apply Changes",
                                    subtitle = "Persist all interface changes immediately to offline storage",
                                    badge = "Offline",
                                    onClick = {
                                        interfaceBuilderManager.saveConfiguration()
                                        Toast.makeText(context, "All layout & interface changes saved offline!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Preview,
                                    title = "Preview Changes",
                                    subtitle = "Test-drive custom pages and current layout setup",
                                    badge = null,
                                    onClick = {
                                        val firstPage = customPages.firstOrNull()
                                        if (firstPage != null) {
                                            previewingPage = firstPage
                                        } else {
                                            Toast.makeText(context, "No custom pages available to preview yet.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                DashboardActionRow(
                                    icon = Icons.Default.Refresh,
                                    title = "Reset Layout",
                                    subtitle = "Restore all layout and interface elements to factory defaults",
                                    badge = "Default",
                                    onClick = { showResetDialog = true }
                                )
                            }
                        }
                    }

                    "Icons" -> {
                        // MANAGE ICONS & BUTTONS SECTION
                        item {
                            SectionHeader("Custom Icons & Buttons Manager (${customIcons.size})")
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Active toolbar icons", color = YtTextSecondary, fontSize = 13.sp)
                                Button(
                                    onClick = { showAddIconDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Icon", fontSize = 12.sp)
                                }
                            }
                        }

                        if (customIcons.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No custom icons created yet. Tap \"Add Icon\" above.", color = YtTextSecondary, fontSize = 13.sp)
                                }
                            }
                        } else {
                            itemsIndexed(customIcons, key = { _, icon -> icon.id }) { index, icon ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(YtSurfaceDark)
                                        .border(1.dp, YtBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(accentColor.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = IconCatalog.getIconByName(icon.iconName),
                                                contentDescription = null,
                                                tint = icon.tintColorHex?.let { Color(it) } ?: accentColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(icon.label, color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                "Target: ${icon.targetLocation.replace('_', ' ')} • Action: ${icon.actionType.replace('_', ' ')}",
                                                color = YtTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                        // Visibility Toggle
                                        IconButton(onClick = { interfaceBuilderManager.toggleIconVisibility(icon.id) }) {
                                            Icon(
                                                imageVector = if (icon.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle Visibility",
                                                tint = if (icon.visible) accentColor else YtTextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        // Move Up / Down
                                        IconButton(
                                            onClick = { interfaceBuilderManager.reorderIcon(icon.id, moveUp = true) },
                                            enabled = index > 0
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) YtTextPrimary else YtTextSecondary.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
                                        }
                                        IconButton(
                                            onClick = { interfaceBuilderManager.reorderIcon(icon.id, moveUp = false) },
                                            enabled = index < customIcons.size - 1
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < customIcons.size - 1) YtTextPrimary else YtTextSecondary.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
                                        }
                                        // Duplicate
                                        IconButton(onClick = { interfaceBuilderManager.duplicateIcon(icon.id) }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = YtTextSecondary, modifier = Modifier.size(18.dp))
                                        }
                                        // Delete
                                        IconButton(onClick = { interfaceBuilderManager.removeCustomIcon(icon.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = YtRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Pages" -> {
                        // MANAGE CUSTOM PAGES SECTION
                        item {
                            SectionHeader("Custom Application Pages (${customPages.size})")
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Offline User-Created Pages", color = YtTextSecondary, fontSize = 13.sp)
                                Button(
                                    onClick = { isCreatingPage = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Page", fontSize = 12.sp)
                                }
                            }
                        }

                        if (customPages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No custom pages found. Tap \"New Page\" to build one visually.", color = YtTextSecondary, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(customPages, key = { it.id }) { page ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(YtSurfaceDark)
                                        .border(1.dp, YtBorder, RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(accentColor.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = IconCatalog.getIconByName(page.iconName),
                                                    contentDescription = null,
                                                    tint = accentColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(page.title, color = YtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                                Text(
                                                    if (page.description.isNotBlank()) page.description else "${page.components.size} components",
                                                    color = YtTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { editingPageId = page.id },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = accentColor)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Edit", fontSize = 11.sp, color = YtTextPrimary)
                                            }
                                            OutlinedButton(
                                                onClick = { previewingPage = page },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(14.dp), tint = YtBlue)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Preview", fontSize = 11.sp, color = YtTextPrimary)
                                            }
                                            OutlinedButton(
                                                onClick = { interfaceBuilderManager.duplicateCustomPage(page.id) },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = YtTextSecondary)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy", fontSize = 11.sp, color = YtTextPrimary)
                                            }
                                            OutlinedButton(
                                                onClick = { interfaceBuilderManager.deleteCustomPage(page.id) },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = YtRed)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Delete", fontSize = 11.sp, color = YtRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Layout" -> {
                        // EDIT APPLICATION LAYOUT
                        item { SectionHeader("Application Layout Configuration") }
                        item {
                            SliderControl(
                                label = "Home Feed Card Separation",
                                value = homeCardSpacing,
                                valueRange = 4f..28f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setHomeCardSpacing(it) }
                            )
                        }
                        item {
                            SliderControl(
                                label = "Card Inner Padding",
                                value = homeCardPadding,
                                valueRange = 6f..24f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setHomeCardPadding(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Liquid Glass Feed Cards",
                                subtitle = "Refractive glass cards for video thumbnails",
                                checked = homeGlassCards,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setHomeGlassCards(it) }
                            )
                        }
                    }

                    "Top Bars" -> {
                        // CUSTOMIZE TOP BARS
                        item { SectionHeader("Top Bars Customization") }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(YtSurfaceDark)
                                    .border(1.dp, YtBorder, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text("Top Bar Active Icons Order", color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Custom icons linked to MAIN_TOP_BAR appear right next to Shuffle and Search in real time.", color = YtTextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { showAddIconDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Top Bar Icon")
                                    }
                                }
                            }
                        }
                    }

                    "Navigation" -> {
                        // CUSTOMIZE NAVIGATION
                        item { SectionHeader("Navigation Bar Customization") }
                        item {
                            SliderControl(
                                label = "Navigation Bar Height",
                                value = navBarHeightDp,
                                valueRange = 48f..76f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setNavBarHeightDp(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Show Navigation Labels",
                                subtitle = "Show text titles under Home, Shorts, Subscriptions, and You",
                                checked = navBarShowLabels,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setNavBarShowLabels(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Liquid Glass Navigation Pill",
                                subtitle = "Floating blurred glass bottom dock",
                                checked = navBarGlassEnabled,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setNavBarGlassEnabled(it) }
                            )
                        }
                    }

                    "Elements" -> {
                        // MANAGE UI ELEMENTS
                        item { SectionHeader("Inventory of Configurable UI Elements") }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "Search Bar Control" to "Configurable search bar & history chips",
                                    "Shuffle Videos Button" to "Randomizes video feed with one tap",
                                    "Notification Bell & Badges" to "Shows unread notification counts",
                                    "Floating Mini Player" to "Picture-in-picture draggable dock",
                                    "Subscribe / Like Buttons" to "Action pills with liquid glass feedback",
                                    "Shorts Vertical Actions" to "Like, Dislike, Comments, Remix, and Save"
                                ).forEach { (title, subtitle) ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(YtSurfaceDark)
                                            .border(1.dp, YtBorder, RoundedCornerShape(10.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(title, color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Text(subtitle, color = YtTextSecondary, fontSize = 11.sp)
                                            }
                                            Text("Active", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "Player" -> {
                        // CUSTOMIZE VIDEO PLAYER
                        item { SectionHeader("Video Player Customization") }
                        item {
                            SliderControl(
                                label = "Player Controls Scale",
                                value = playerControlsSize * 100f,
                                valueRange = 80f..140f,
                                unit = "%",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setPlayerControlsSize(it / 100f) }
                            )
                        }
                    }

                    "Profile" -> {
                        // CUSTOMIZE PROFILE
                        item { SectionHeader("Profile Interface Customization") }
                        item {
                            SliderControl(
                                label = "Profile Avatar Corner Radius",
                                value = profileAvatarCornerRadius,
                                valueRange = 4f..28f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setProfileAvatarCornerRadius(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Glass Action Buttons",
                                subtitle = "Profile action pills rendered with Liquid Glass",
                                checked = profileActionButtonsGlass,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setProfileActionButtonsGlass(it) }
                            )
                        }
                    }

                    "Themes" -> {
                        // MANAGE THEMES
                        item { SectionHeader("Select Interface Theme") }
                        item {
                            val themes = listOf(
                                "Liquid Glass (Dark)" to "Real-time GPU refraction with dark glass finish",
                                "Liquid Glass (Light)" to "Frosted water-lens glass with bright theme",
                                "Cyberpunk Neon" to "Glowing neon edges & futuristic highlights",
                                "AMOLED Black" to "Pure OLED deep-black battery saver",
                                "Dark Mode" to "Standard YouTube dark mode"
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                themes.forEach { (name, desc) ->
                                    val isCurrent = currentTheme == name
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { settingsManager.setUiTheme(name) }
                                            .background(if (isCurrent) accentColor.copy(alpha = 0.15f) else YtSurfaceDark)
                                            .border(if (isCurrent) 1.5.dp else 1.dp, if (isCurrent) accentColor else YtBorder, RoundedCornerShape(12.dp))
                                            .padding(14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(name, color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                                Text(desc, color = YtTextSecondary, fontSize = 11.sp)
                                            }
                                            if (isCurrent) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==============================================================
                // CATEGORIES 1-6: LIVE PREVIEWS & SPECIALIZED CONTROLS
                // ==============================================================
                item {
                    SectionHeader("Live Interactive Preview")
                    LivePreviewCard(
                        categoryIndex = selectedCategory,
                        accentColor = accentColor,
                        glassConfig = glassConfig,
                        isGlassTheme = isGlassTheme,
                        homeCardCornerRadius = homeCardCornerRadius,
                        homeGlassCards = homeGlassCards,
                        shortsActionButtonStyle = shortsActionButtonStyle,
                        shortsActionIconScale = shortsActionIconScale,
                        navBarHeightDp = navBarHeightDp,
                        navBarIndicatorStyle = navBarIndicatorStyle,
                        navBarShowLabels = navBarShowLabels,
                        profileAvatarRadius = profileAvatarCornerRadius,
                        profileHeaderGlass = profileHeaderGlassStyle != "Solid",
                        notifCardRadius = notifCardCornerRadius,
                        notifGlassCards = notifGlassCards,
                        opticalCurvature = opticalCurvaturePower
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                when (selectedCategory) {
                    1 -> {
                        // Home Feed
                        item { SectionHeader("Home Feed Layout & Cards") }
                        item {
                            SliderControl(
                                label = "Card Corner Radius",
                                value = homeCardCornerRadius,
                                valueRange = 0f..28f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setHomeCardCornerRadius(it) }
                            )
                        }
                        item {
                            SliderControl(
                                label = "Card Spacing / Separation",
                                value = homeCardSpacing,
                                valueRange = 4f..24f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setHomeCardSpacing(it) }
                            )
                        }
                        item {
                            SliderControl(
                                label = "Card Inner Padding",
                                value = homeCardPadding,
                                valueRange = 6f..24f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setHomeCardPadding(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Refractive Glass Feed Cards",
                                subtitle = "Enables real-time optical glass on video thumbnails",
                                checked = homeGlassCards,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setHomeGlassCards(it) }
                            )
                        }
                    }

                    2 -> {
                        // Shorts Screen
                        item { SectionHeader("Shorts Screen Controls") }
                        item {
                            SliderControl(
                                label = "Action Icons Scale",
                                value = shortsActionIconScale * 100f,
                                valueRange = 70f..140f,
                                unit = "%",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setShortsActionIconScale(it / 100f) }
                            )
                        }
                        item {
                            SliderControl(
                                label = "Video Overlay Contrast",
                                value = shortsOverlayAlpha * 100f,
                                valueRange = 0f..80f,
                                unit = "%",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setShortsOverlayAlpha(it / 100f) }
                            )
                        }
                    }

                    3 -> {
                        // Navigation Bar
                        item { SectionHeader("Bottom Navigation Bar") }
                        item {
                            SliderControl(
                                label = "Navigation Bar Height",
                                value = navBarHeightDp,
                                valueRange = 48f..76f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setNavBarHeightDp(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Liquid Glass Navigation Dock",
                                subtitle = "Curved translucent glass bar with optical refraction",
                                checked = navBarGlassEnabled,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setNavBarGlassEnabled(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Show Navigation Labels",
                                subtitle = "Display text captions under tab icons",
                                checked = navBarShowLabels,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setNavBarShowLabels(it) }
                            )
                        }
                    }

                    4 -> {
                        // Profile UI
                        item { SectionHeader("Profile & Library Styling") }
                        item {
                            SliderControl(
                                label = "Avatar Corner Radius",
                                value = profileAvatarCornerRadius,
                                valueRange = 4f..28f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setProfileAvatarCornerRadius(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Glass Action Buttons",
                                subtitle = "Profile action pills rendered with Liquid Glass",
                                checked = profileActionButtonsGlass,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setProfileActionButtonsGlass(it) }
                            )
                        }
                    }

                    5 -> {
                        // Notifications
                        item { SectionHeader("Notifications Screen Styling") }
                        item {
                            SliderControl(
                                label = "Notification Card Corner Radius",
                                value = notifCardCornerRadius,
                                valueRange = 4f..24f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setNotificationCardCornerRadius(it) }
                            )
                        }
                        item {
                            SwitchControl(
                                title = "Glass Notification Cards",
                                subtitle = "Blurred translucent notification cards with colored icons",
                                checked = notifGlassCards,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setNotificationGlassCards(it) }
                            )
                        }
                        item {
                            SliderControl(
                                label = "Card Vertical Spacing",
                                value = notifCardSpacing,
                                valueRange = 2f..16f,
                                unit = "dp",
                                accentColor = accentColor,
                                onValueChange = { settingsManager.setNotificationCardSpacing(it) }
                            )
                        }
                    }

                    6 -> {
                        // GPU Shaders & Physics
                        item { SectionHeader("Real-Time GPU Optics & Spring Physics") }
                        item {
                            SwitchControl(
                                title = "AGSL Optical Refraction Shader",
                                subtitle = "Real-time UV bending and chromatic dispersion via GPU",
                                checked = opticalShaderEnabled,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setOpticalShaderEnabled(it) }
                            )
                        }
                        if (opticalShaderEnabled) {
                            item {
                                SliderControl(
                                    label = "Lens Curvature Power",
                                    value = opticalCurvaturePower,
                                    valueRange = 1.0f..4.0f,
                                    unit = "x",
                                    accentColor = accentColor,
                                    onValueChange = { settingsManager.setOpticalCurvaturePower(it) }
                                )
                            }
                            item {
                                SliderControl(
                                    label = "Chromatic Dispersion",
                                    value = opticalChromaticDispersion,
                                    valueRange = 0.0f..3.0f,
                                    unit = "x",
                                    accentColor = accentColor,
                                    onValueChange = { settingsManager.setOpticalChromaticDispersion(it) }
                                )
                            }
                        }
                        item {
                            SwitchControl(
                                title = "Fluid Spring-Physics Transitions",
                                subtitle = "Compose physics-based spring animations for navigation",
                                checked = springPhysicsEnabled,
                                accentColor = accentColor,
                                onCheckedChange = { settingsManager.setSpringPhysicsEnabled(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // ==============================================================
    // ADD CUSTOM ICON DIALOG
    // ==============================================================
    if (showAddIconDialog) {
        AddCustomIconDialog(
            customPages = customPages,
            accentColor = accentColor,
            onDismiss = { showAddIconDialog = false },
            onCreateNewPage = {
                showAddIconDialog = false
                isCreatingPage = true
            },
            onSaveIcon = { newIcon ->
                interfaceBuilderManager.addCustomIcon(newIcon)
                showAddIconDialog = false
                Toast.makeText(context, "Icon \"${newIcon.label}\" added to ${newIcon.targetLocation}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ==============================================================
    // RESET LAYOUT CONFIRMATION DIALOG
    // ==============================================================
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Interface Layout?", fontWeight = FontWeight.Bold) },
            text = { Text("This will restore default toolbars, icons, and layout parameters.") },
            confirmButton = {
                Button(
                    onClick = {
                        settingsManager.resetAllAppearance()
                        showResetDialog = false
                        Toast.makeText(context, "Layout restored to defaults", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = YtRed)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = YtTextPrimary)
                }
            }
        )
    }
}

@Composable
private fun DashboardActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String?,
    onClick: () -> Unit
) {
    val accentColor = LocalAppAccentColor.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(YtSurfaceDark)
            .border(1.dp, YtBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = YtTextSecondary, fontSize = 11.sp)
            }
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(badge, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = YtTextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomIconDialog(
    customPages: List<CustomPageDefinition>,
    accentColor: Color,
    onDismiss: () -> Unit,
    onCreateNewPage: () -> Unit,
    onSaveIcon: (CustomIconItem) -> Unit
) {
    var label by remember { mutableStateOf("My Shortcut") }
    var selectedIconName by remember { mutableStateOf("Star") }
    var targetLocation by remember { mutableStateOf(IconLocations.MAIN_TOP_BAR) }
    var actionType by remember { mutableStateOf(ActionTypes.OPEN_CUSTOM_PAGE) }
    var actionTarget by remember { mutableStateOf(customPages.firstOrNull()?.id ?: "") }
    var sizeDp by remember { mutableStateOf(22f) }

    val locations = listOf(
        IconLocations.MAIN_TOP_BAR to "Main Top Bar",
        IconLocations.PROFILE_TOP_BAR to "Profile Top Bar",
        IconLocations.PLAYER_CONTROLS to "Video Player",
        IconLocations.FLOATING_ACTIONS to "Floating Action Bar",
        IconLocations.HOME_ACTIONS to "Home Feed Header"
    )

    val actionTypesList = listOf(
        ActionTypes.OPEN_CUSTOM_PAGE to "Open Custom Page",
        ActionTypes.OPEN_SCREEN to "Open App Screen",
        ActionTypes.OPEN_SETTINGS to "Open Settings",
        ActionTypes.TOGGLE_FEATURE to "Toggle Feature (Neon/Glass)",
        ActionTypes.SHOW_INFO to "Show Info Notice"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Icon", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Icon Label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Pick Icon from Catalog
                Text("Select Icon Appearance", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconCatalog.AVAILABLE_ICONS.take(12).forEach { (name, iconVector) ->
                        val isSelected = selectedIconName == name
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedIconName = name }
                                .background(if (isSelected) accentColor.copy(alpha = 0.3f) else YtSurfaceVariant)
                                .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) accentColor else YtBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconVector, contentDescription = name, tint = if (isSelected) accentColor else YtTextPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Destination Location
                Text("Destination Location", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    locations.forEach { (locKey, locName) ->
                        val isSel = targetLocation == locKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { targetLocation = locKey }
                                .background(if (isSel) accentColor else YtSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(locName, color = if (isSel) Color.White else YtTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Action Selector
                Text("Action on Tap", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    actionTypesList.forEach { (typeKey, typeLabel) ->
                        val isSel = actionType == typeKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { actionType = typeKey }
                                .background(if (isSel) accentColor else YtSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(typeLabel, color = if (isSel) Color.White else YtTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Link to custom page options
                if (actionType == ActionTypes.OPEN_CUSTOM_PAGE) {
                    if (customPages.isNotEmpty()) {
                        Text("Select Target Page:", color = YtTextSecondary, fontSize = 12.sp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            customPages.forEach { page ->
                                val isPageSel = actionTarget == page.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { actionTarget = page.id }
                                    .background(if (isPageSel) accentColor.copy(alpha = 0.3f) else YtSurfaceVariant)
                                    .border(if (isPageSel) 1.dp else 0.5.dp, if (isPageSel) accentColor else YtBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(page.title, color = if (isPageSel) accentColor else YtTextPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = onCreateNewPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = accentColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Page for This Icon", fontSize = 12.sp, color = YtTextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalTarget = if (actionType == ActionTypes.OPEN_CUSTOM_PAGE && actionTarget.isBlank()) {
                        customPages.firstOrNull()?.id ?: ""
                    } else actionTarget

                    val newIcon = CustomIconItem(
                        id = UUID.randomUUID().toString(),
                        label = label.ifBlank { "Custom Icon" },
                        iconName = selectedIconName,
                        targetLocation = targetLocation,
                        alignment = "right",
                        order = 0,
                        visible = true,
                        sizeDp = sizeDp,
                        actionType = actionType,
                        actionTarget = finalTarget,
                        isUserCreated = true
                    )
                    onSaveIcon(newIcon)
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Save & Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = YtTextPrimary)
            }
        }
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = YtTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
private fun SliderControl(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = YtTextPrimary, fontSize = 14.sp)
            Text(
                text = "${value.toInt()} $unit",
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = YtBorder
            )
        )
    }
}

@Composable
private fun SwitchControl(
    title: String,
    subtitle: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = YtTextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun LivePreviewCard(
    categoryIndex: Int,
    accentColor: Color,
    glassConfig: com.example.ui.theme.LiquidGlassStyleConfig,
    isGlassTheme: Boolean,
    homeCardCornerRadius: Float,
    homeGlassCards: Boolean,
    shortsActionButtonStyle: String,
    shortsActionIconScale: Float,
    navBarHeightDp: Float,
    navBarIndicatorStyle: String,
    navBarShowLabels: Boolean,
    profileAvatarRadius: Float,
    profileHeaderGlass: Boolean,
    notifCardRadius: Float,
    notifGlassCards: Boolean,
    opticalCurvature: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isGlassTheme) {
                    Modifier.liquidGlassSurface(
                        shape = RoundedCornerShape(16.dp),
                        config = glassConfig,
                        accentColor = accentColor,
                        elevation = 6.dp
                    )
                } else {
                    Modifier
                        .background(YtSurfaceDark)
                        .border(1.dp, YtBorder, RoundedCornerShape(16.dp))
                }
            )
            .padding(14.dp)
    ) {
        when (categoryIndex) {
            1 -> {
                // Home Feed Preview
                Column {
                    Text("Live Home Feed Card Preview", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(homeCardCornerRadius.dp))
                            .then(
                                if (homeGlassCards && isGlassTheme) {
                                    Modifier.liquidGlassSurface(
                                        shape = RoundedCornerShape(homeCardCornerRadius.dp),
                                        config = glassConfig,
                                        accentColor = accentColor
                                    )
                                } else {
                                    Modifier.background(YtSurfaceVariant)
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp, 44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Building Liquid Glass UI in Android", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("MyTube Dev • 12K views • 2 hours ago", color = YtTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Shorts Preview
                Column {
                    Text("Live Shorts Vertical Toolbar Preview", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PreviewShortsButton(Icons.Default.ThumbUp, "8.4K", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                        PreviewShortsButton(Icons.Default.Share, "Share", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                        PreviewShortsButton(Icons.Default.Bookmark, "Save", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                    }
                }
            }

            3 -> {
                // Navigation Bar Preview
                Column {
                    Text("Live Navigation Dock Preview (${navBarHeightDp.toInt()}dp height)", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(navBarHeightDp.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .then(
                                if (isGlassTheme) {
                                    Modifier.liquidGlassSurface(
                                        shape = RoundedCornerShape(20.dp),
                                        config = glassConfig,
                                        accentColor = accentColor,
                                        elevation = 8.dp
                                    )
                                } else {
                                    Modifier
                                        .background(YtSurfaceVariant)
                                        .border(1.dp, YtBorder, RoundedCornerShape(20.dp))
                                }
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Home" to Icons.Default.Home, "Shorts" to Icons.Default.Whatshot, "You" to Icons.Default.Person).forEach { (lbl, ico) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(ico, contentDescription = null, tint = if (lbl == "Home") accentColor else YtTextSecondary, modifier = Modifier.size(20.dp))
                                    if (navBarShowLabels) {
                                        Text(lbl, color = if (lbl == "Home") accentColor else YtTextSecondary, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // Profile UI Preview
                Column {
                    Text("Live Profile Header Preview", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(profileAvatarRadius.dp))
                                .background(YtAvatarPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Muhannad Murtaza", color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("@muhannad • 1.2K subscribers", color = YtTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            5 -> {
                // Notifications Preview
                Column {
                    Text("Live Notification Card Preview", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(notifCardRadius.dp))
                            .then(
                                if (notifGlassCards && isGlassTheme) {
                                    Modifier.liquidGlassSurface(
                                        shape = RoundedCornerShape(notifCardRadius.dp),
                                        config = glassConfig,
                                        accentColor = accentColor
                                    )
                                } else {
                                    Modifier.background(YtSurfaceVariant)
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(accentColor.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("New video uploaded", color = YtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Tech Channel just uploaded a clip • 2h ago", color = YtTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            else -> {
                // Shaders & Physics Preview
                Column {
                    Text("Live GPU Lens Refraction & Optical Specular Preview", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(16.dp),
                                config = glassConfig,
                                accentColor = accentColor,
                                elevation = 8.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Optical Refraction (Curvature: ${String.format("%.1f", opticalCurvature)})",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewShortsButton(
    icon: ImageVector,
    label: String,
    style: String,
    scale: Float,
    accentColor: Color,
    glassConfig: com.example.ui.theme.LiquidGlassStyleConfig
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size((42 * scale).dp)
                .then(
                    when (style) {
                        "Glass Pill", "Floating Circle" -> Modifier
                            .clip(CircleShape)
                            .liquidGlassSurface(CircleShape, glassConfig, accentColor, 3.dp)
                        "Glow" -> Modifier
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.3f))
                            .border(1.5.dp, accentColor, CircleShape)
                        else -> Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((20 * scale).dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = Color.White, fontSize = 10.sp)
    }
}
