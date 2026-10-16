package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionTypes
import com.example.data.model.ComponentTypes
import com.example.data.model.CustomPageComponent
import com.example.data.model.CustomPageDefinition
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.LocalUiTheme
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtDarkBackground
import com.example.ui.theme.YtGlassDarkBackground
import com.example.ui.theme.YtGlassDarkBorder
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtSurfaceVariant
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary
import com.example.ui.theme.liquidGlassSurface
import com.example.util.IconCatalog
import kotlinx.coroutines.launch

/**
 * Full-screen renderer for user-created Custom Pages
 * Executed when an icon or button configured with OPEN_CUSTOM_PAGE is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPageRendererScreen(
    page: CustomPageDefinition,
    onBack: () -> Unit,
    onNavigateToScreen: (Int) -> Unit = {},
    onOpenAnotherPage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current
    val currentTheme = LocalUiTheme.current
    val isGlass = glassConfig.isEnabled && (page.isGlassTheme || currentTheme.contains("Liquid Glass"))
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isGlass) YtGlassDarkBackground else YtDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = IconCatalog.getIconByName(page.iconName),
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = page.title,
                                color = YtTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (page.description.isNotBlank()) {
                                Text(
                                    text = page.description,
                                    color = YtTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("custom_page_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YtTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.statusBarsPadding()
            )

            HorizontalDivider(
                color = if (isGlass) YtGlassDarkBorder else YtBorder,
                thickness = 0.5.dp
            )

            if (page.components.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = IconCatalog.getIconByName(page.iconName),
                            contentDescription = null,
                            tint = YtTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This custom page is empty",
                            color = YtTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open Interface Builder in Settings to design components on this page.",
                            color = YtTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("custom_page_component_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(page.components, key = { it.id }) { component ->
                        RenderComponent(
                            component = component,
                            accentColor = accentColor,
                            glassConfig = glassConfig,
                            isGlassTheme = isGlass,
                            onAction = { actionType, actionTarget ->
                                when (actionType) {
                                    ActionTypes.BACK -> onBack()
                                    ActionTypes.OPEN_CUSTOM_PAGE -> onOpenAnotherPage(actionTarget)
                                    ActionTypes.OPEN_SCREEN -> {
                                        when (actionTarget.lowercase()) {
                                            "home" -> onNavigateToScreen(0)
                                            "shorts" -> onNavigateToScreen(1)
                                            "subscriptions" -> onNavigateToScreen(3)
                                            "library" -> onNavigateToScreen(4)
                                            else -> onNavigateToScreen(0)
                                        }
                                    }
                                    ActionTypes.SHOW_INFO -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (actionTarget.isNotBlank()) actionTarget else "Button tapped!"
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RenderComponent(
    component: CustomPageComponent,
    accentColor: Color,
    glassConfig: com.example.ui.theme.LiquidGlassStyleConfig,
    isGlassTheme: Boolean,
    onAction: (String, String) -> Unit
) {
    val textAlign = when (component.alignment) {
        "center" -> TextAlign.Center
        "right" -> TextAlign.Right
        else -> TextAlign.Left
    }
    val alignModifier = when (component.alignment) {
        "center" -> Alignment.CenterHorizontally
        "right" -> Alignment.End
        else -> Alignment.Start
    }

    when (component.type) {
        ComponentTypes.HEADING -> {
            Text(
                text = component.text,
                color = component.textColorHex?.let { Color(it) } ?: YtTextPrimary,
                fontSize = component.fontSizeSp.sp,
                fontWeight = FontWeight.Bold,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ComponentTypes.TEXT -> {
            Text(
                text = component.text,
                color = component.textColorHex?.let { Color(it) } ?: YtTextSecondary,
                fontSize = component.fontSizeSp.sp,
                lineHeight = (component.fontSizeSp * 1.35f).sp,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth()
            )
        }

        ComponentTypes.BUTTON -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth(component.widthPercent)
                    .clip(RoundedCornerShape(component.cornerRadiusDp.dp))
                    .then(
                        if (component.isGlass && isGlassTheme) {
                            Modifier.liquidGlassSurface(
                                shape = RoundedCornerShape(component.cornerRadiusDp.dp),
                                config = glassConfig,
                                accentColor = accentColor,
                                elevation = 4.dp
                            )
                        } else {
                            Modifier.background(
                                component.backgroundColorHex?.let { Color(it) } ?: accentColor
                            )
                        }
                    )
                    .clickable {
                        onAction(component.actionType, component.actionTarget)
                    }
                    .padding(horizontal = 16.dp, vertical = component.paddingDp.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (component.iconName.isNotBlank()) {
                        Icon(
                            imageVector = IconCatalog.getIconByName(component.iconName),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = component.text,
                        color = component.textColorHex?.let { Color(it) } ?: Color.White,
                        fontSize = component.fontSizeSp.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        ComponentTypes.ICON -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = alignModifier
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .then(
                            if (component.isGlass && isGlassTheme) {
                                Modifier.liquidGlassSurface(
                                    shape = CircleShape,
                                    config = glassConfig,
                                    accentColor = accentColor,
                                    elevation = 3.dp
                                )
                            } else {
                                Modifier.background(YtSurfaceVariant)
                            }
                        )
                        .clickable {
                            if (component.actionType != ActionTypes.NONE) {
                                onAction(component.actionType, component.actionTarget)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = IconCatalog.getIconByName(component.iconName),
                        contentDescription = component.text,
                        tint = component.textColorHex?.let { Color(it) } ?: accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        ComponentTypes.GLASS_PANEL, ComponentTypes.CARD -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth(component.widthPercent)
                    .clip(RoundedCornerShape(component.cornerRadiusDp.dp))
                    .then(
                        if (component.isGlass && isGlassTheme) {
                            Modifier.liquidGlassSurface(
                                shape = RoundedCornerShape(component.cornerRadiusDp.dp),
                                config = glassConfig,
                                accentColor = accentColor,
                                elevation = 6.dp
                            )
                        } else {
                            Modifier
                                .background(
                                    component.backgroundColorHex?.let { Color(it) } ?: YtSurfaceDark
                                )
                                .border(1.dp, YtBorder, RoundedCornerShape(component.cornerRadiusDp.dp))
                        }
                    )
                    .clickable {
                        if (component.actionType != ActionTypes.NONE) {
                            onAction(component.actionType, component.actionTarget)
                        }
                    }
                    .padding(component.paddingDp.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (component.iconName.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = IconCatalog.getIconByName(component.iconName),
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = component.text,
                        color = component.textColorHex?.let { Color(it) } ?: YtTextPrimary,
                        fontSize = component.fontSizeSp.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        ComponentTypes.DIVIDER -> {
            HorizontalDivider(
                color = if (isGlassTheme) YtGlassDarkBorder else YtBorder,
                thickness = 0.8.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        ComponentTypes.SPACER -> {
            Spacer(modifier = Modifier.height(component.paddingDp.dp))
        }

        else -> {
            Text(
                text = component.text,
                color = YtTextPrimary,
                fontSize = 14.sp
            )
        }
    }
}
