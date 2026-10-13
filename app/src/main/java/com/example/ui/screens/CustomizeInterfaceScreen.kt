package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SettingsManager
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

/**
 * Settings -> Customize Interface Screen:
 * Full A-to-Z UI Customization Studio with Live Previews for:
 * A. Home Feed (layout, card spacing, corner radius, padding, glass cards)
 * B. Shorts Screen (button styles, icon scale, overlay contrast, title size)
 * C. Navigation Bar (height, sliding indicator, glass pill, labels, icons)
 * D. Profile UI (avatar radius, glass card header, stat pills)
 * E. Notifications (card radius, glass cards, badges, spacing)
 * F. GPU Shaders & Spring Physics (real-time optical refraction, chromatic dispersion)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeInterfaceScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current
    val currentTheme = LocalUiTheme.current
    val isGlassTheme = currentTheme.contains("Liquid Glass") || glassConfig.isEnabled

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

    val categories = listOf(
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
                        text = "Customize Interface",
                        color = YtTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-Time Live UI Studio & Customization",
                        color = accentColor,
                        fontSize = 11.sp
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("btn_customize_back")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { settingsManager.resetAllAppearance() },
                    modifier = Modifier.testTag("btn_reset_appearance")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset all",
                        tint = YtTextSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.statusBarsPadding()
        )

        // Horizontal Category Tab Pills
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Live Interactive Preview Section
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
                    profileHeaderGlass = profileHeaderGlassStyle,
                    notifCardRadius = notifCardCornerRadius,
                    notifGlassCards = notifGlassCards,
                    opticalCurvature = opticalCurvaturePower
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Category Controls
            when (selectedCategory) {
                0 -> {
                    // Home Feed Customization
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
                            valueRange = 0f..16f,
                            unit = "dp",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setHomeCardPadding(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Liquid Glass Feed Cards",
                            subtitle = "Translucent background refraction on video cards",
                            checked = homeGlassCards,
                            onCheckedChange = { settingsManager.setHomeGlassCards(it) }
                        )
                    }
                }

                1 -> {
                    // Shorts Screen Customization
                    item { SectionHeader("Shorts Player & Action Buttons") }

                    item {
                        SegmentedOptionSelector(
                            title = "Action Button Style",
                            options = listOf("Glass Pill", "Floating Circle", "Glow", "Minimal"),
                            selected = shortsActionButtonStyle,
                            accentColor = accentColor,
                            onSelect = { settingsManager.setShortsActionButtonStyle(it) }
                        )
                    }

                    item {
                        SliderControl(
                            label = "Action Icon Scale",
                            value = shortsActionIconScale,
                            valueRange = 0.8f..1.3f,
                            unit = "x",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setShortsActionIconScale(it) }
                        )
                    }

                    item {
                        SliderControl(
                            label = "Overlay Gradient Dimming",
                            value = shortsOverlayAlpha,
                            valueRange = 0.4f..1.0f,
                            unit = "%",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setShortsOverlayAlpha(it) }
                        )
                    }
                }

                2 -> {
                    // Navigation Bar Customization
                    item { SectionHeader("Shorts Bottom Navigation & Tabs") }

                    item {
                        SliderControl(
                            label = "Bottom Bar Height",
                            value = navBarHeightDp,
                            valueRange = 46f..68f,
                            unit = "dp",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setNavBarHeightDp(it) }
                        )
                    }

                    item {
                        SegmentedOptionSelector(
                            title = "Sliding Active Indicator",
                            options = listOf("Liquid Glass Pill", "Dot Indicator", "Glow Bar"),
                            selected = navBarIndicatorStyle,
                            accentColor = accentColor,
                            onSelect = { settingsManager.setNavBarIndicatorStyle(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Show Tab Text Labels",
                            subtitle = "Display Home, Shorts, Subscriptions, You labels",
                            checked = navBarShowLabels,
                            onCheckedChange = { settingsManager.setNavBarShowLabels(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Liquid Glass Navigation Bar",
                            subtitle = "Translucent frosted background for bottom bar",
                            checked = navBarGlassEnabled,
                            onCheckedChange = { settingsManager.setNavBarGlassEnabled(it) }
                        )
                    }
                }

                3 -> {
                    // Profile UI Customization
                    item { SectionHeader("Profile Screen & Header") }

                    item {
                        SegmentedOptionSelector(
                            title = "Profile Header Style",
                            options = listOf("Liquid Glass Card", "Floating Pill", "Minimalist Border"),
                            selected = profileHeaderGlassStyle,
                            accentColor = accentColor,
                            onSelect = { settingsManager.setProfileHeaderGlassStyle(it) }
                        )
                    }

                    item {
                        SliderControl(
                            label = "Avatar Corner Curvature (50 = Circle)",
                            value = profileAvatarCornerRadius,
                            valueRange = 0f..50f,
                            unit = "%",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setProfileAvatarCornerRadius(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Liquid Glass Action Pills",
                            subtitle = "Apply translucent glass to Switch account, Google Account pills",
                            checked = profileActionButtonsGlass,
                            onCheckedChange = { settingsManager.setProfileActionButtonsGlass(it) }
                        )
                    }
                }

                4 -> {
                    // Notification UI Customization
                    item { SectionHeader("Notification Feed & Cards") }

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
                        SliderControl(
                            label = "Notification Card Spacing",
                            value = notifCardSpacing,
                            valueRange = 4f..16f,
                            unit = "dp",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setNotificationCardSpacing(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Liquid Glass Notification Cards",
                            subtitle = "Translucent cards with specular rim highlights in feed",
                            checked = notifGlassCards,
                            onCheckedChange = { settingsManager.setNotificationGlassCards(it) }
                        )
                    }
                }

                5 -> {
                    // GPU Shaders & Spring Physics
                    item { SectionHeader("GPU RuntimeShader & Spring Physics") }

                    item {
                        ToggleRow(
                            title = "Real-Time GPU Optical Shader (Android 13+)",
                            subtitle = "AGSL GPU shader with chromatic dispersion & UV refraction",
                            checked = opticalShaderEnabled,
                            onCheckedChange = { settingsManager.setOpticalShaderEnabled(it) }
                        )
                    }

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
                            label = "Chromatic Dispersion (RGB Offset)",
                            value = opticalChromaticDispersion,
                            valueRange = 0.0f..1.0f,
                            unit = "%",
                            accentColor = accentColor,
                            onValueChange = { settingsManager.setOpticalChromaticDispersion(it) }
                        )
                    }

                    item {
                        ToggleRow(
                            title = "Spring Physics Navigation & Panels",
                            subtitle = "Fluid iOS-like spring animations for tabs and bottom sheets",
                            checked = springPhysicsEnabled,
                            onCheckedChange = { settingsManager.setSpringPhysicsEnabled(it) }
                        )
                    }
                }
            }

            // Quick Restore Defaults
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { settingsManager.resetAllAppearance() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, YtBorder)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Interface Customizations", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = YtBlue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = YtTextPrimary, fontSize = 14.sp)
            val displayValue = if (unit == "%") "${(value * 100).toInt()}%" else if (unit == "x") String.format("%.1fx", value) else "${value.toInt()} $unit"
            Text(displayValue, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = YtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = YtTextSecondary, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
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
private fun SegmentedOptionSelector(
    title: String,
    options: listOfString,
    selected: String,
    accentColor: Color,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(title, color = YtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { opt ->
                val isSelected = opt == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(opt) }
                        .background(if (isSelected) accentColor else YtSurfaceVariant)
                        .border(1.dp, if (isSelected) Color.White.copy(alpha = 0.5f) else YtBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = opt,
                        color = if (isSelected) Color.White else YtTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

private typealias listOfString = List<String>

/**
 * Live interactive preview card displaying instant feedback for selected adjustments
 */
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
    profileHeaderGlass: String,
    notifCardRadius: Float,
    notifGlassCards: Boolean,
    opticalCurvature: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(YtSurfaceDark)
            .border(1.dp, if (isGlassTheme) YtGlassDarkBorder else YtBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        when (categoryIndex) {
            0 -> {
                // Home Card Preview
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
                                    .size(60.dp, 38.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accentColor.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sample Video Card Title", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Channel Name • 24K views", color = YtTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Shorts Action Preview
                Column {
                    Text("Live Shorts Action Button Preview ($shortsActionButtonStyle)", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PreviewShortsButton(Icons.Default.ThumbUp, "3.4K", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                        PreviewShortsButton(Icons.Default.Favorite, "Like", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                        PreviewShortsButton(Icons.Default.Share, "Share", shortsActionButtonStyle, shortsActionIconScale, accentColor, glassConfig)
                    }
                }
            }

            2 -> {
                // Navigation Bar Preview
                Column {
                    Text("Live Bottom Navigation Preview ($navBarIndicatorStyle)", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(navBarHeightDp.dp.coerceAtLeast(46.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isGlassTheme) {
                                    Modifier.liquidGlassSurface(
                                        shape = RoundedCornerShape(12.dp),
                                        config = glassConfig,
                                        accentColor = accentColor
                                    )
                                } else {
                                    Modifier.background(YtSurfaceVariant)
                                }
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                                if (navBarShowLabels) Text("Home", color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Whatshot, contentDescription = null, tint = YtTextSecondary, modifier = Modifier.size(20.dp))
                                if (navBarShowLabels) Text("Shorts", color = YtTextSecondary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = YtTextSecondary, modifier = Modifier.size(20.dp))
                                if (navBarShowLabels) Text("Subscriptions", color = YtTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            3 -> {
                // Profile Preview
                Column {
                    Text("Live Profile Header Preview ($profileHeaderGlass)", color = YtTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .then(
                                if (isGlassTheme) {
                                    Modifier.liquidGlassSurface(
                                        shape = RoundedCornerShape(14.dp),
                                        config = glassConfig,
                                        accentColor = accentColor
                                    )
                                } else {
                                    Modifier.background(YtSurfaceVariant)
                                }
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val avatarShape = if (profileAvatarRadius >= 45f) CircleShape else RoundedCornerShape((profileAvatarRadius * 0.4f).dp)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(avatarShape)
                                .background(YtAvatarPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Muhannad Murtaza", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("@MuhannadMurtaza • 34 clips", color = YtTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            4 -> {
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
