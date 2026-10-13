package com.example.data

import android.content.Context
import com.example.data.model.ActionTypes
import com.example.data.model.ComponentTypes
import com.example.data.model.CustomIconItem
import com.example.data.model.CustomPageComponent
import com.example.data.model.CustomPageDefinition
import com.example.data.model.HomeFeedLayoutConfig
import com.example.data.model.IconLocations
import com.example.data.model.ProfileToolbarConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * 100% Offline Interface Builder Manager
 * Handles local persistence of custom pages, custom icons, toolbar layouts, and home feed arrangements.
 */
class InterfaceBuilderManager(private val context: Context) {

    private val configFile = File(context.filesDir, "interface_builder_config.json")

    private val _customIcons = MutableStateFlow<List<CustomIconItem>>(emptyList())
    val customIcons: StateFlow<List<CustomIconItem>> = _customIcons.asStateFlow()

    private val _customPages = MutableStateFlow<List<CustomPageDefinition>>(emptyList())
    val customPages: StateFlow<List<CustomPageDefinition>> = _customPages.asStateFlow()

    private val _profileToolbarConfig = MutableStateFlow(ProfileToolbarConfig())
    val profileToolbarConfig: StateFlow<ProfileToolbarConfig> = _profileToolbarConfig.asStateFlow()

    private val _homeFeedLayoutConfig = MutableStateFlow(HomeFeedLayoutConfig())
    val homeFeedLayoutConfig: StateFlow<HomeFeedLayoutConfig> = _homeFeedLayoutConfig.asStateFlow()

    private val _activePreset = MutableStateFlow("Default Layout")
    val activePreset: StateFlow<String> = _activePreset.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<String>()
    private val redoStack = mutableListOf<String>()

    init {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        try {
            if (configFile.exists()) {
                val jsonStr = configFile.readText()
                val root = JSONObject(jsonStr)

                // Icons
                val iconsList = mutableListOf<CustomIconItem>()
                val iconsArray = root.optJSONArray("icons")
                if (iconsArray != null) {
                    for (i in 0 until iconsArray.length()) {
                        iconsList.add(CustomIconItem.fromJson(iconsArray.getJSONObject(i)))
                    }
                }
                _customIcons.value = iconsList

                // Custom Pages
                val pagesList = mutableListOf<CustomPageDefinition>()
                val pagesArray = root.optJSONArray("pages")
                if (pagesArray != null) {
                    for (i in 0 until pagesArray.length()) {
                        pagesList.add(CustomPageDefinition.fromJson(pagesArray.getJSONObject(i)))
                    }
                }
                _customPages.value = pagesList

                // Profile Toolbar
                if (root.has("profileToolbar")) {
                    _profileToolbarConfig.value = ProfileToolbarConfig.fromJson(root.getJSONObject("profileToolbar"))
                }

                // Home Feed Layout
                if (root.has("homeFeedLayout")) {
                    _homeFeedLayoutConfig.value = HomeFeedLayoutConfig.fromJson(root.getJSONObject("homeFeedLayout"))
                }

                _activePreset.value = root.optString("activePreset", "Default Layout")
            } else {
                initDefaultConfiguration()
            }
        } catch (_: Exception) {
            initDefaultConfiguration()
        }
    }

    private fun initDefaultConfiguration() {
        // Create initial default custom page
        val defaultPageId = "creator_hub_page_1"
        val samplePage = CustomPageDefinition(
            id = defaultPageId,
            title = "Creator Hub",
            description = "Custom Offline Dashboard & Shortcuts",
            iconName = "Star",
            isGlassTheme = true,
            components = listOf(
                CustomPageComponent(
                    type = ComponentTypes.HEADING,
                    text = "Welcome to Creator Hub ✨",
                    fontSizeSp = 20f,
                    alignment = "left",
                    isGlass = false
                ),
                CustomPageComponent(
                    type = ComponentTypes.TEXT,
                    text = "This is a custom page created with the offline visual page builder. Add, remove, and tap any component below.",
                    fontSizeSp = 13f,
                    alignment = "left",
                    isGlass = false
                ),
                CustomPageComponent(
                    type = ComponentTypes.GLASS_PANEL,
                    text = "Quick Studio Controls • Offline Media Studio",
                    iconName = "Camera",
                    isGlass = true,
                    cornerRadiusDp = 16f,
                    paddingDp = 14f
                ),
                CustomPageComponent(
                    type = ComponentTypes.BUTTON,
                    text = "Go to Shorts Studio ⚡",
                    iconName = "Shorts",
                    actionType = ActionTypes.OPEN_SCREEN,
                    actionTarget = "Shorts",
                    cornerRadiusDp = 12f
                ),
                CustomPageComponent(
                    type = ComponentTypes.BUTTON,
                    text = "Open Offline Library 📁",
                    iconName = "Library",
                    actionType = ActionTypes.OPEN_SCREEN,
                    actionTarget = "Library",
                    cornerRadiusDp = 12f
                ),
                CustomPageComponent(
                    type = ComponentTypes.CARD,
                    text = "Tip: You can customize, reorder, or create brand-new pages from Settings → Customize Interface → Interface Builder.",
                    iconName = "Info",
                    isGlass = true,
                    cornerRadiusDp = 14f
                )
            )
        )

        // Default top bar custom icon pointing to the default custom page
        val defaultIcon = CustomIconItem(
            id = "icon_hub_1",
            label = "Creator Hub",
            iconName = "Star",
            targetLocation = IconLocations.MAIN_TOP_BAR,
            alignment = "right",
            order = 0,
            visible = true,
            actionType = ActionTypes.OPEN_CUSTOM_PAGE,
            actionTarget = defaultPageId,
            isUserCreated = false
        )

        _customPages.value = listOf(samplePage)
        _customIcons.value = listOf(defaultIcon)
        _profileToolbarConfig.value = ProfileToolbarConfig(settingsIconPosition = "right")
        _homeFeedLayoutConfig.value = HomeFeedLayoutConfig(
            shortsFrequency = 6,
            shortsGridColumns = 2,
            shortsSectionInterval = 2
        )
        saveConfiguration()
    }

    private fun snapshotForUndo() {
        try {
            val json = serializeState()
            undoStack.add(json)
            if (undoStack.size > 20) undoStack.removeAt(0)
            redoStack.clear()
        } catch (_: Exception) {}
    }

    fun canUndo(): Boolean = undoStack.isNotEmpty()
    fun canRedo(): Boolean = redoStack.isNotEmpty()

    fun undo() {
        if (!canUndo()) return
        val currentJson = serializeState()
        redoStack.add(currentJson)
        val prevJson = undoStack.removeAt(undoStack.size - 1)
        restoreFromJson(prevJson)
    }

    fun redo() {
        if (!canRedo()) return
        val currentJson = serializeState()
        undoStack.add(currentJson)
        val nextJson = redoStack.removeAt(redoStack.size - 1)
        restoreFromJson(nextJson)
    }

    private fun serializeState(): String {
        val root = JSONObject().apply {
            val iconsArr = JSONArray()
            _customIcons.value.forEach { iconsArr.put(it.toJson()) }
            put("icons", iconsArr)

            val pagesArr = JSONArray()
            _customPages.value.forEach { pagesArr.put(it.toJson()) }
            put("pages", pagesArr)

            put("profileToolbar", _profileToolbarConfig.value.toJson())
            put("homeFeedLayout", _homeFeedLayoutConfig.value.toJson())
            put("activePreset", _activePreset.value)
        }
        return root.toString()
    }

    private fun restoreFromJson(jsonStr: String) {
        try {
            val root = JSONObject(jsonStr)
            val iconsList = mutableListOf<CustomIconItem>()
            val iconsArray = root.optJSONArray("icons")
            if (iconsArray != null) {
                for (i in 0 until iconsArray.length()) {
                    iconsList.add(CustomIconItem.fromJson(iconsArray.getJSONObject(i)))
                }
            }
            _customIcons.value = iconsList

            val pagesList = mutableListOf<CustomPageDefinition>()
            val pagesArray = root.optJSONArray("pages")
            if (pagesArray != null) {
                for (i in 0 until pagesArray.length()) {
                    pagesList.add(CustomPageDefinition.fromJson(pagesArray.getJSONObject(i)))
                }
            }
            _customPages.value = pagesList

            if (root.has("profileToolbar")) {
                _profileToolbarConfig.value = ProfileToolbarConfig.fromJson(root.getJSONObject("profileToolbar"))
            }
            if (root.has("homeFeedLayout")) {
                _homeFeedLayoutConfig.value = HomeFeedLayoutConfig.fromJson(root.getJSONObject("homeFeedLayout"))
            }
            _activePreset.value = root.optString("activePreset", "Custom")
            saveConfiguration()
        } catch (_: Exception) {}
    }

    fun saveConfiguration() {
        try {
            val jsonStr = serializeState()
            configFile.writeText(jsonStr)
        } catch (_: Exception) {}
    }

    // -----------------------------------------------------------------
    // ICON & BUTTON OPERATIONS
    // -----------------------------------------------------------------

    fun addCustomIcon(icon: CustomIconItem) {
        snapshotForUndo()
        val list = _customIcons.value.toMutableList()
        list.add(icon)
        _customIcons.value = list
        saveConfiguration()
    }

    fun updateCustomIcon(icon: CustomIconItem) {
        snapshotForUndo()
        val list = _customIcons.value.toMutableList()
        val idx = list.indexOfFirst { it.id == icon.id }
        if (idx >= 0) {
            list[idx] = icon
            _customIcons.value = list
            saveConfiguration()
        }
    }

    fun removeCustomIcon(iconId: String) {
        snapshotForUndo()
        val list = _customIcons.value.toMutableList()
        list.removeAll { it.id == iconId }
        _customIcons.value = list
        saveConfiguration()
    }

    fun toggleIconVisibility(iconId: String) {
        snapshotForUndo()
        val list = _customIcons.value.toMutableList()
        val idx = list.indexOfFirst { it.id == iconId }
        if (idx >= 0) {
            val item = list[idx]
            list[idx] = item.copy(visible = !item.visible)
            _customIcons.value = list
            saveConfiguration()
        }
    }

    fun reorderIcon(iconId: String, moveUp: Boolean) {
        snapshotForUndo()
        val list = _customIcons.value.toMutableList()
        val idx = list.indexOfFirst { it.id == iconId }
        if (idx >= 0) {
            val targetIdx = if (moveUp) idx - 1 else idx + 1
            if (targetIdx in 0 until list.size) {
                val item = list.removeAt(idx)
                list.add(targetIdx, item)
                _customIcons.value = list
                saveConfiguration()
            }
        }
    }

    fun duplicateIcon(iconId: String) {
        snapshotForUndo()
        val orig = _customIcons.value.firstOrNull { it.id == iconId } ?: return
        val duplicate = orig.copy(
            id = java.util.UUID.randomUUID().toString(),
            label = "${orig.label} (Copy)",
            order = orig.order + 1,
            isUserCreated = true
        )
        addCustomIcon(duplicate)
    }

    // -----------------------------------------------------------------
    // CUSTOM PAGE OPERATIONS
    // -----------------------------------------------------------------

    fun addCustomPage(page: CustomPageDefinition) {
        snapshotForUndo()
        val list = _customPages.value.toMutableList()
        list.add(page)
        _customPages.value = list
        saveConfiguration()
    }

    fun updateCustomPage(page: CustomPageDefinition) {
        snapshotForUndo()
        val list = _customPages.value.toMutableList()
        val idx = list.indexOfFirst { it.id == page.id }
        if (idx >= 0) {
            list[idx] = page
            _customPages.value = list
            saveConfiguration()
        }
    }

    fun duplicateCustomPage(pageId: String) {
        snapshotForUndo()
        val orig = _customPages.value.firstOrNull { it.id == pageId } ?: return
        val duplicate = orig.copy(
            id = java.util.UUID.randomUUID().toString(),
            title = "${orig.title} (Copy)",
            components = orig.components.map { it.copy(id = java.util.UUID.randomUUID().toString()) }
        )
        addCustomPage(duplicate)
    }

    fun deleteCustomPage(pageId: String) {
        snapshotForUndo()
        val list = _customPages.value.toMutableList()
        list.removeAll { it.id == pageId }
        _customPages.value = list

        // Reassign any icons referencing this deleted page to NONE
        val iconList = _customIcons.value.map { icon ->
            if (icon.actionType == ActionTypes.OPEN_CUSTOM_PAGE && icon.actionTarget == pageId) {
                icon.copy(actionType = ActionTypes.SHOW_INFO, actionTarget = "")
            } else icon
        }
        _customIcons.value = iconList
        saveConfiguration()
    }

    fun getCustomPage(pageId: String): CustomPageDefinition? {
        return _customPages.value.firstOrNull { it.id == pageId }
    }

    // -----------------------------------------------------------------
    // CUSTOM PAGE COMPONENT OPERATIONS
    // -----------------------------------------------------------------

    fun addComponentToPage(pageId: String, component: CustomPageComponent) {
        snapshotForUndo()
        val page = getCustomPage(pageId) ?: return
        val updatedComponents = page.components.toMutableList().apply { add(component) }
        updateCustomPage(page.copy(components = updatedComponents))
    }

    fun updateComponentInPage(pageId: String, component: CustomPageComponent) {
        snapshotForUndo()
        val page = getCustomPage(pageId) ?: return
        val updatedComponents = page.components.map {
            if (it.id == component.id) component else it
        }
        updateCustomPage(page.copy(components = updatedComponents))
    }

    fun removeComponentFromPage(pageId: String, componentId: String) {
        snapshotForUndo()
        val page = getCustomPage(pageId) ?: return
        val updatedComponents = page.components.filterNot { it.id == componentId }
        updateCustomPage(page.copy(components = updatedComponents))
    }

    fun reorderComponentInPage(pageId: String, componentId: String, moveUp: Boolean) {
        snapshotForUndo()
        val page = getCustomPage(pageId) ?: return
        val list = page.components.toMutableList()
        val idx = list.indexOfFirst { it.id == componentId }
        if (idx >= 0) {
            val targetIdx = if (moveUp) idx - 1 else idx + 1
            if (targetIdx in 0 until list.size) {
                val item = list.removeAt(idx)
                list.add(targetIdx, item)
                updateCustomPage(page.copy(components = list))
            }
        }
    }

    // -----------------------------------------------------------------
    // PROFILE TOOLBAR POSITION OPERATIONS
    // -----------------------------------------------------------------

    fun updateProfileToolbarConfig(config: ProfileToolbarConfig) {
        snapshotForUndo()
        _profileToolbarConfig.value = config
        saveConfiguration()
    }

    fun setSettingsIconPosition(position: String) { // left, center, right
        updateProfileToolbarConfig(_profileToolbarConfig.value.copy(settingsIconPosition = position))
    }

    // -----------------------------------------------------------------
    // HOME FEED & SHORTS GRID OPERATIONS
    // -----------------------------------------------------------------

    fun updateHomeFeedLayoutConfig(config: HomeFeedLayoutConfig) {
        snapshotForUndo()
        _homeFeedLayoutConfig.value = config
        saveConfiguration()
    }

    fun setShortsFrequency(count: Int) {
        updateHomeFeedLayoutConfig(_homeFeedLayoutConfig.value.copy(shortsFrequency = count))
    }

    fun setShortsGridColumns(columns: Int) {
        updateHomeFeedLayoutConfig(_homeFeedLayoutConfig.value.copy(shortsGridColumns = columns))
    }

    fun setShortsSectionInterval(interval: Int) {
        updateHomeFeedLayoutConfig(_homeFeedLayoutConfig.value.copy(shortsSectionInterval = interval))
    }

    fun setShortsCardSpacing(spacing: Float) {
        updateHomeFeedLayoutConfig(_homeFeedLayoutConfig.value.copy(shortsCardSpacingDp = spacing))
    }

    fun setShortsCardRadius(radius: Float) {
        updateHomeFeedLayoutConfig(_homeFeedLayoutConfig.value.copy(shortsCardRadiusDp = radius))
    }

    // -----------------------------------------------------------------
    // PRESETS & RESET
    // -----------------------------------------------------------------

    fun applyPreset(presetName: String) {
        snapshotForUndo()
        _activePreset.value = presetName
        when (presetName) {
            "Minimalist Deck" -> {
                _profileToolbarConfig.value = ProfileToolbarConfig(settingsIconPosition = "right", searchIconVisible = false)
                _homeFeedLayoutConfig.value = HomeFeedLayoutConfig(shortsFrequency = 2, shortsGridColumns = 1, shortsSectionInterval = 4)
            }
            "Creator Mode" -> {
                _profileToolbarConfig.value = ProfileToolbarConfig(settingsIconPosition = "left", accountsPillPosition = "center")
                _homeFeedLayoutConfig.value = HomeFeedLayoutConfig(shortsFrequency = 8, shortsGridColumns = 3, shortsSectionInterval = 2)
            }
            "Glass Studio" -> {
                _profileToolbarConfig.value = ProfileToolbarConfig(settingsIconPosition = "right", toolbarElevationDp = 10f)
                _homeFeedLayoutConfig.value = HomeFeedLayoutConfig(shortsFrequency = 4, shortsGridColumns = 2, shortsSectionInterval = 2, shortsCardRadiusDp = 18f)
            }
            else -> {
                // Default
                _profileToolbarConfig.value = ProfileToolbarConfig(settingsIconPosition = "right")
                _homeFeedLayoutConfig.value = HomeFeedLayoutConfig(shortsFrequency = 6, shortsGridColumns = 2, shortsSectionInterval = 2)
            }
        }
        saveConfiguration()
    }

    fun resetAllLayout() {
        snapshotForUndo()
        configFile.delete()
        initDefaultConfiguration()
    }
}
