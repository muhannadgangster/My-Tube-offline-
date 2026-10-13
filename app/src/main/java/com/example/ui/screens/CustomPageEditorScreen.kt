package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InterfaceBuilderManager
import com.example.data.model.ActionTypes
import com.example.data.model.ComponentTypes
import com.example.data.model.CustomPageComponent
import com.example.data.model.CustomPageDefinition
import com.example.ui.theme.LocalAppAccentColor
import com.example.ui.theme.LocalLiquidGlassConfig
import com.example.ui.theme.LocalUiTheme
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
import com.example.util.IconCatalog
import java.util.UUID

/**
 * Visual Page Editor screen that allows users to design custom pages
 * with real-time adding, editing, reordering, and property adjustments.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPageEditorScreen(
    pageId: String?,
    interfaceBuilderManager: InterfaceBuilderManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val accentColor = LocalAppAccentColor.current
    val glassConfig = LocalLiquidGlassConfig.current
    val currentTheme = LocalUiTheme.current
    val isGlass = currentTheme.contains("Liquid Glass") || glassConfig.isEnabled

    val existingPage = pageId?.let { interfaceBuilderManager.getCustomPage(it) }

    var title by remember { mutableStateOf(existingPage?.title ?: "New Page") }
    var description by remember { mutableStateOf(existingPage?.description ?: "") }
    var iconName by remember { mutableStateOf(existingPage?.iconName ?: "Folder") }
    var isGlassTheme by remember { mutableStateOf(existingPage?.isGlassTheme ?: true) }
    var components by remember { mutableStateOf(existingPage?.components ?: emptyList()) }

    var isPreviewMode by remember { mutableStateOf(false) }
    var editingComponent by remember { mutableStateOf<CustomPageComponent?>(null) }
    var showAddComponentDialog by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }

    if (isPreviewMode) {
        val previewPage = CustomPageDefinition(
            id = pageId ?: "preview_page",
            title = title,
            description = description,
            iconName = iconName,
            isGlassTheme = isGlassTheme,
            components = components
        )
        CustomPageRendererScreen(
            page = previewPage,
            onBack = { isPreviewMode = false },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isGlass) YtGlassDarkBackground else YtDarkBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (existingPage != null) "Edit Custom Page" else "Create Custom Page",
                    color = YtTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack, modifier = Modifier.testTag("page_editor_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = YtTextPrimary
                    )
                }
            },
            actions = {
                // Live Preview Button
                IconButton(onClick = { isPreviewMode = true }, modifier = Modifier.testTag("btn_preview_page")) {
                    Icon(
                        imageVector = Icons.Default.Preview,
                        contentDescription = "Preview",
                        tint = YtTextSecondary
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        val savedPage = CustomPageDefinition(
                            id = pageId ?: UUID.randomUUID().toString(),
                            title = title.ifBlank { "Untitled Page" },
                            description = description,
                            iconName = iconName,
                            isGlassTheme = isGlassTheme,
                            components = components
                        )
                        if (existingPage != null) {
                            interfaceBuilderManager.updateCustomPage(savedPage)
                        } else {
                            interfaceBuilderManager.addCustomPage(savedPage)
                        }
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("btn_save_custom_page")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.statusBarsPadding()
        )

        HorizontalDivider(color = if (isGlass) YtGlassDarkBorder else YtBorder, thickness = 0.5.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Page Metadata Settings Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .then(
                            if (isGlass) {
                                Modifier.liquidGlassSurface(
                                    shape = RoundedCornerShape(14.dp),
                                    config = glassConfig,
                                    accentColor = accentColor,
                                    elevation = 4.dp
                                )
                            } else {
                                Modifier
                                    .background(YtSurfaceDark)
                                    .border(1.dp, YtBorder, RoundedCornerShape(14.dp))
                            }
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "Page Settings",
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Page Title") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Subtitle / Description") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Page Icon", color = YtTextPrimary, fontSize = 14.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showIconPicker = true }
                                    .background(YtSurfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = IconCatalog.getIconByName(iconName),
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(iconName, color = YtTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Liquid Glass Background", color = YtTextPrimary, fontSize = 14.sp)
                            Switch(
                                checked = isGlassTheme,
                                onCheckedChange = { isGlassTheme = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = accentColor
                                )
                            )
                        }
                    }
                }
            }

            // Section Header + Add Component Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Page Components (${components.size})",
                        color = YtTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddComponentDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        modifier = Modifier.testTag("btn_add_component")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Element", fontSize = 12.sp)
                    }
                }
            }

            // Component List
            if (components.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No components yet. Tap \"Add Element\" to build your page.",
                            color = YtTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                itemsIndexed(components, key = { _, comp -> comp.id }) { index, comp ->
                    ComponentEditorCard(
                        component = comp,
                        index = index,
                        totalCount = components.size,
                        accentColor = accentColor,
                        onMoveUp = {
                            if (index > 0) {
                                val list = components.toMutableList()
                                val item = list.removeAt(index)
                                list.add(index - 1, item)
                                components = list
                            }
                        },
                        onMoveDown = {
                            if (index < components.size - 1) {
                                val list = components.toMutableList()
                                val item = list.removeAt(index)
                                list.add(index + 1, item)
                                components = list
                            }
                        },
                        onEdit = { editingComponent = comp },
                        onDuplicate = {
                            val list = components.toMutableList()
                            val duplicate = comp.copy(
                                id = UUID.randomUUID().toString(),
                                text = "${comp.text} (Copy)"
                            )
                            list.add(index + 1, duplicate)
                            components = list
                        },
                        onDelete = {
                            components = components.filterNot { it.id == comp.id }
                        }
                    )
                }
            }
        }
    }

    // Add Component Dialog
    if (showAddComponentDialog) {
        val availableTypes = listOf(
            ComponentTypes.HEADING to "Heading",
            ComponentTypes.TEXT to "Body Text",
            ComponentTypes.BUTTON to "Interactive Button",
            ComponentTypes.GLASS_PANEL to "Liquid Glass Panel",
            ComponentTypes.CARD to "Info Card",
            ComponentTypes.ICON to "Icon Widget",
            ComponentTypes.DIVIDER to "Horizontal Divider",
            ComponentTypes.SPACER to "Spacer (Gap)"
        )
        AlertDialog(
            onDismissRequest = { showAddComponentDialog = false },
            containerColor = YtSurfaceDark,
            title = { Text("Choose Component Type", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTypes.forEach { (type, label) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val defaultText = when (type) {
                                        ComponentTypes.HEADING -> "Heading Title"
                                        ComponentTypes.TEXT -> "Paragraph text with details..."
                                        ComponentTypes.BUTTON -> "Click Action Button"
                                        ComponentTypes.GLASS_PANEL -> "Liquid Glass Frosted Panel"
                                        ComponentTypes.CARD -> "Featured Information Card"
                                        else -> ""
                                    }
                                    val newComp = CustomPageComponent(
                                        type = type,
                                        text = defaultText,
                                        iconName = if (type == ComponentTypes.BUTTON || type == ComponentTypes.ICON) "Star" else "",
                                        fontSizeSp = if (type == ComponentTypes.HEADING) 18f else 14f,
                                        isGlass = type == ComponentTypes.GLASS_PANEL || type == ComponentTypes.BUTTON
                                    )
                                    components = components + newComp
                                    showAddComponentDialog = false
                                }
                                .background(YtSurfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(label, color = YtTextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddComponentDialog = false }) {
                    Text("Cancel", color = YtTextSecondary)
                }
            }
        )
    }

    // Edit Component Properties Dialog
    if (editingComponent != null) {
        EditComponentPropertiesDialog(
            component = editingComponent!!,
            accentColor = accentColor,
            onDismiss = { editingComponent = null },
            onSave = { updated ->
                components = components.map { if (it.id == updated.id) updated else it }
                editingComponent = null
            }
        )
    }

    // Icon Picker Dialog
    if (showIconPicker) {
        AlertDialog(
            onDismissRequest = { showIconPicker = false },
            containerColor = YtSurfaceDark,
            title = { Text("Select Page Icon", color = YtTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val chunked = IconCatalog.AVAILABLE_ICONS.chunked(4)
                    chunked.forEach { rowIcons ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            rowIcons.forEach { (name, vec) ->
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            iconName = name
                                            showIconPicker = false
                                        }
                                        .background(if (iconName == name) accentColor.copy(alpha = 0.3f) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(vec, contentDescription = name, tint = if (iconName == name) accentColor else YtTextPrimary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconPicker = false }) { Text("Close", color = YtBlue) }
            }
        )
    }
}

@Composable
private fun ComponentEditorCard(
    component: CustomPageComponent,
    index: Int,
    totalCount: Int,
    accentColor: Color,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(YtSurfaceDark)
            .border(1.dp, YtBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = component.type.uppercase(),
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (component.isGlass) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨ Glass", color = YtTextSecondary, fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (component.text.isNotBlank()) component.text else "[${component.type}]",
                    color = YtTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }

            // Move Up
            IconButton(onClick = onMoveUp, enabled = index > 0, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) YtTextPrimary else YtTextSecondary.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
            }

            // Move Down
            IconButton(onClick = onMoveDown, enabled = index < totalCount - 1, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < totalCount - 1) YtTextPrimary else YtTextSecondary.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
            }

            // Edit
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = YtBlue, modifier = Modifier.size(18.dp))
            }

            // Duplicate
            IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = YtTextSecondary, modifier = Modifier.size(18.dp))
            }

            // Delete
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = YtRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun EditComponentPropertiesDialog(
    component: CustomPageComponent,
    accentColor: Color,
    onDismiss: () -> Unit,
    onSave: (CustomPageComponent) -> Unit
) {
    var text by remember { mutableStateOf(component.text) }
    var actionType by remember { mutableStateOf(component.actionType) }
    var actionTarget by remember { mutableStateOf(component.actionTarget) }
    var fontSize by remember { mutableStateOf(component.fontSizeSp) }
    var cornerRadius by remember { mutableStateOf(component.cornerRadiusDp) }
    var isGlass by remember { mutableStateOf(component.isGlass) }
    var alignment by remember { mutableStateOf(component.alignment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = YtSurfaceDark,
        title = {
            Text("Edit ${component.type.replaceFirstChar { it.uppercase() }}", color = YtTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Display Text / Label") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (component.type == ComponentTypes.BUTTON || component.type == ComponentTypes.CARD) {
                    Text("Button Action", color = YtTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    val actionOptions = listOf(
                        ActionTypes.NONE to "None",
                        ActionTypes.OPEN_SCREEN to "Open Screen",
                        ActionTypes.BACK to "Go Back",
                        ActionTypes.SHOW_INFO to "Show Message"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        actionOptions.forEach { (type, label) ->
                            val isSel = actionType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { actionType = type }
                                    .background(if (isSel) accentColor else YtSurfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(label, color = if (isSel) Color.White else YtTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    if (actionType == ActionTypes.OPEN_SCREEN) {
                        OutlinedTextField(
                            value = actionTarget,
                            onValueChange = { actionTarget = it },
                            label = { Text("Screen (Home, Shorts, Subscriptions, Library)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (actionType == ActionTypes.SHOW_INFO) {
                        OutlinedTextField(
                            value = actionTarget,
                            onValueChange = { actionTarget = it },
                            label = { Text("Snackbar Message Text") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size: ${fontSize.toInt()}sp", color = YtTextPrimary, fontSize = 12.sp)
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 10f..32f,
                        modifier = Modifier.width(160.dp),
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Corner Radius: ${cornerRadius.toInt()}dp", color = YtTextPrimary, fontSize = 12.sp)
                    Slider(
                        value = cornerRadius,
                        onValueChange = { cornerRadius = it },
                        valueRange = 0f..28f,
                        modifier = Modifier.width(160.dp),
                        colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Liquid Glass Style", color = YtTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = isGlass,
                        onCheckedChange = { isGlass = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        component.copy(
                            text = text,
                            actionType = actionType,
                            actionTarget = actionTarget,
                            fontSizeSp = fontSize,
                            cornerRadiusDp = cornerRadius,
                            isGlass = isGlass,
                            alignment = alignment
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = YtTextSecondary) }
        }
    )
}
