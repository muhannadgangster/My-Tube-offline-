package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Supported locations where custom icons and buttons can be placed
 */
object IconLocations {
    const val MAIN_TOP_BAR = "main_top_bar"
    const val PROFILE_TOP_BAR = "profile_top_bar"
    const val PLAYER_CONTROLS = "player_controls"
    const val FLOATING_ACTIONS = "floating_actions"
    const val HOME_ACTIONS = "home_actions"

    val ALL = listOf(
        MAIN_TOP_BAR to "Main Top Bar",
        PROFILE_TOP_BAR to "Profile Top Bar",
        PLAYER_CONTROLS to "Video Player",
        FLOATING_ACTIONS to "Floating Action Bar",
        HOME_ACTIONS to "Home Section Header"
    )
}

/**
 * Supported action types for icons and buttons
 */
object ActionTypes {
    const val OPEN_CUSTOM_PAGE = "open_custom_page"
    const val OPEN_SCREEN = "open_screen"
    const val OPEN_SETTINGS = "open_settings"
    const val TOGGLE_FEATURE = "toggle_feature"
    const val SHOW_INFO = "show_info"
    const val BACK = "back"
    const val NONE = "none"
}

/**
 * Model representing a configurable or custom icon/button in the UI
 */
data class CustomIconItem(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val iconName: String = "Star",
    val targetLocation: String = IconLocations.MAIN_TOP_BAR,
    val alignment: String = "right", // left, center, right
    val order: Int = 0,
    val visible: Boolean = true,
    val tintColorHex: Long? = null,
    val sizeDp: Float = 22f,
    val actionType: String = ActionTypes.SHOW_INFO,
    val actionTarget: String = "",
    val isUserCreated: Boolean = true
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("label", label)
        put("iconName", iconName)
        put("targetLocation", targetLocation)
        put("alignment", alignment)
        put("order", order)
        put("visible", visible)
        tintColorHex?.let { put("tintColorHex", it) }
        put("sizeDp", sizeDp.toDouble())
        put("actionType", actionType)
        put("actionTarget", actionTarget)
        put("isUserCreated", isUserCreated)
    }

    companion object {
        fun fromJson(json: JSONObject): CustomIconItem {
            return CustomIconItem(
                id = json.optString("id", UUID.randomUUID().toString()),
                label = json.optString("label", "Custom Icon"),
                iconName = json.optString("iconName", "Star"),
                targetLocation = json.optString("targetLocation", IconLocations.MAIN_TOP_BAR),
                alignment = json.optString("alignment", "right"),
                order = json.optInt("order", 0),
                visible = json.optBoolean("visible", true),
                tintColorHex = if (json.has("tintColorHex")) json.getLong("tintColorHex") else null,
                sizeDp = json.optDouble("sizeDp", 22.0).toFloat(),
                actionType = json.optString("actionType", ActionTypes.SHOW_INFO),
                actionTarget = json.optString("actionTarget", ""),
                isUserCreated = json.optBoolean("isUserCreated", true)
            )
        }
    }
}

/**
 * Component types supported in the Custom Page Visual Builder
 */
object ComponentTypes {
    const val HEADING = "heading"
    const val TEXT = "text"
    const val BUTTON = "button"
    const val ICON = "icon"
    const val GLASS_PANEL = "glass_panel"
    const val CARD = "card"
    const val DIVIDER = "divider"
    const val SPACER = "spacer"
    const val CONTAINER = "container"
    const val GRID = "grid"
    const val LIST = "list"
    const val IMAGE = "image"
}

/**
 * Visual element inside a user-created Custom Page
 */
data class CustomPageComponent(
    val id: String = UUID.randomUUID().toString(),
    val type: String = ComponentTypes.TEXT,
    val text: String = "Sample Text",
    val iconName: String = "Star",
    val actionType: String = ActionTypes.NONE,
    val actionTarget: String = "",
    val fontSizeSp: Float = 14f,
    val textColorHex: Long? = null,
    val backgroundColorHex: Long? = null,
    val cornerRadiusDp: Float = 12f,
    val paddingDp: Float = 12f,
    val isGlass: Boolean = true,
    val widthPercent: Float = 1.0f,
    val alignment: String = "left" // left, center, right
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("type", type)
        put("text", text)
        put("iconName", iconName)
        put("actionType", actionType)
        put("actionTarget", actionTarget)
        put("fontSizeSp", fontSizeSp.toDouble())
        textColorHex?.let { put("textColorHex", it) }
        backgroundColorHex?.let { put("backgroundColorHex", it) }
        put("cornerRadiusDp", cornerRadiusDp.toDouble())
        put("paddingDp", paddingDp.toDouble())
        put("isGlass", isGlass)
        put("widthPercent", widthPercent.toDouble())
        put("alignment", alignment)
    }

    companion object {
        fun fromJson(json: JSONObject): CustomPageComponent {
            return CustomPageComponent(
                id = json.optString("id", UUID.randomUUID().toString()),
                type = json.optString("type", ComponentTypes.TEXT),
                text = json.optString("text", ""),
                iconName = json.optString("iconName", "Star"),
                actionType = json.optString("actionType", ActionTypes.NONE),
                actionTarget = json.optString("actionTarget", ""),
                fontSizeSp = json.optDouble("fontSizeSp", 14.0).toFloat(),
                textColorHex = if (json.has("textColorHex")) json.getLong("textColorHex") else null,
                backgroundColorHex = if (json.has("backgroundColorHex")) json.getLong("backgroundColorHex") else null,
                cornerRadiusDp = json.optDouble("cornerRadiusDp", 12.0).toFloat(),
                paddingDp = json.optDouble("paddingDp", 12.0).toFloat(),
                isGlass = json.optBoolean("isGlass", true),
                widthPercent = json.optDouble("widthPercent", 1.0).toFloat(),
                alignment = json.optString("alignment", "left")
            )
        }
    }
}

/**
 * Definition of a user-designed Custom Page
 */
data class CustomPageDefinition(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val iconName: String = "Folder",
    val isGlassTheme: Boolean = true,
    val components: List<CustomPageComponent> = emptyList()
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("description", description)
        put("iconName", iconName)
        put("isGlassTheme", isGlassTheme)
        val arr = JSONArray()
        components.forEach { arr.put(it.toJson()) }
        put("components", arr)
    }

    companion object {
        fun fromJson(json: JSONObject): CustomPageDefinition {
            val componentsList = mutableListOf<CustomPageComponent>()
            val arr = json.optJSONArray("components")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    componentsList.add(CustomPageComponent.fromJson(arr.getJSONObject(i)))
                }
            }
            return CustomPageDefinition(
                id = json.optString("id", UUID.randomUUID().toString()),
                title = json.optString("title", "Untitled Page"),
                description = json.optString("description", ""),
                iconName = json.optString("iconName", "Folder"),
                isGlassTheme = json.optBoolean("isGlassTheme", true),
                components = componentsList
            )
        }
    }
}

/**
 * Dedicated layout configuration for the Profile Screen Toolbar
 */
data class ProfileToolbarConfig(
    val settingsIconPosition: String = "right", // left, center, right
    val searchIconVisible: Boolean = true,
    val notificationIconVisible: Boolean = true,
    val accountsPillPosition: String = "left", // left, center, right
    val toolbarElevationDp: Float = 6f
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("settingsIconPosition", settingsIconPosition)
        put("searchIconVisible", searchIconVisible)
        put("notificationIconVisible", notificationIconVisible)
        put("accountsPillPosition", accountsPillPosition)
        put("toolbarElevationDp", toolbarElevationDp.toDouble())
    }

    companion object {
        fun fromJson(json: JSONObject): ProfileToolbarConfig {
            return ProfileToolbarConfig(
                settingsIconPosition = json.optString("settingsIconPosition", "right"),
                searchIconVisible = json.optBoolean("searchIconVisible", true),
                notificationIconVisible = json.optBoolean("notificationIconVisible", true),
                accountsPillPosition = json.optString("accountsPillPosition", "left"),
                toolbarElevationDp = json.optDouble("toolbarElevationDp", 6.0).toFloat()
            )
        }
    }
}

/**
 * Dedicated layout configuration for Home Feed and Shorts Grid
 */
data class HomeFeedLayoutConfig(
    val shortsFrequency: Int = 6, // 2, 4, 6, 8
    val shortsGridColumns: Int = 2, // 1, 2, 3
    val shortsSectionInterval: Int = 2, // after every 2, 4, 6, 8 regular videos
    val shortsCardSpacingDp: Float = 10f,
    val shortsCardRadiusDp: Float = 14f,
    val shortsAspectRatio: Float = 9f / 16f
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("shortsFrequency", shortsFrequency)
        put("shortsGridColumns", shortsGridColumns)
        put("shortsSectionInterval", shortsSectionInterval)
        put("shortsCardSpacingDp", shortsCardSpacingDp.toDouble())
        put("shortsCardRadiusDp", shortsCardRadiusDp.toDouble())
        put("shortsAspectRatio", shortsAspectRatio.toDouble())
    }

    companion object {
        fun fromJson(json: JSONObject): HomeFeedLayoutConfig {
            return HomeFeedLayoutConfig(
                shortsFrequency = json.optInt("shortsFrequency", 6),
                shortsGridColumns = json.optInt("shortsGridColumns", 2),
                shortsSectionInterval = json.optInt("shortsSectionInterval", 2),
                shortsCardSpacingDp = json.optDouble("shortsCardSpacingDp", 10.0).toFloat(),
                shortsCardRadiusDp = json.optDouble("shortsCardRadiusDp", 14.0).toFloat(),
                shortsAspectRatio = json.optDouble("shortsAspectRatio", (9.0 / 16.0)).toFloat()
            )
        }
    }
}
