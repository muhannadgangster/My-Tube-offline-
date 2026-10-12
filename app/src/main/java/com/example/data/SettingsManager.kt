package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsManager(context: Context) {
    val dataStore = SettingsDataStore(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs: SharedPreferences = context.getSharedPreferences("mytube_settings", Context.MODE_PRIVATE)

    // User Profile
    private val _userName = MutableStateFlow(prefs.getString("user_name", "Muhannad Murtaza") ?: "Muhannad Murtaza")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userHandle = MutableStateFlow(prefs.getString("user_handle", "@muhannad") ?: "@muhannad")
    val userHandle: StateFlow<String> = _userHandle.asStateFlow()

    private val _userAvatarUri = MutableStateFlow(prefs.getString("user_avatar_uri", null))
    val userAvatarUri: StateFlow<String?> = _userAvatarUri.asStateFlow()

    fun updateProfile(name: String, handle: String, avatarUri: String?) {
        _userName.value = name.trim()
        _userHandle.value = handle.trim()
        _userAvatarUri.value = avatarUri
        prefs.edit()
            .putString("user_name", name.trim())
            .putString("user_handle", handle.trim())
            .putString("user_avatar_uri", avatarUri)
            .apply()
    }

    // General
    private val _remindBreak = MutableStateFlow(prefs.getBoolean("remind_break", false))
    val remindBreak: StateFlow<Boolean> = _remindBreak.asStateFlow()

    private val _breakIntervalMinutes = MutableStateFlow(prefs.getInt("break_interval", 60))
    val breakIntervalMinutes: StateFlow<Int> = _breakIntervalMinutes.asStateFlow()

    private val _remindBedtime = MutableStateFlow(prefs.getBoolean("remind_bedtime", false))
    val remindBedtime: StateFlow<Boolean> = _remindBedtime.asStateFlow()

    private val _bedtimeHour = MutableStateFlow(prefs.getInt("bedtime_hour", 23))
    val bedtimeHour: StateFlow<Int> = _bedtimeHour.asStateFlow()

    // UI Theme Customization
    companion object {
        val THEME_OPTIONS = listOf(
            "Dark Mode",
            "Light Mode",
            "Liquid Glass (Dark)",
            "Liquid Glass (Light)",
            "Liquid Glass with Reflection"
        )

        val ACCENT_COLOR_OPTIONS = listOf(
            "YouTube Red" to 0xFFFF0033L,
            "Maro / Maroon" to 0xFF800000L,
            "Crimson Neon" to 0xFFFF1744L,
            "Electric Cyan" to 0xFF00E5FFL,
            "Vibrant Emerald" to 0xFF00E676L,
            "Purple Flame" to 0xFFD500F9L,
            "Sunset Gold" to 0xFFFFAB00L,
            "Deep Sky Blue" to 0xFF2979FFL,
            "Hot Pink" to 0xFFFF4081L
        )
    }

    private val _uiTheme = MutableStateFlow(prefs.getString("ui_theme", "Dark Mode") ?: "Dark Mode")
    val uiTheme: StateFlow<String> = _uiTheme.asStateFlow()

    private val _darkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", true))
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    // Custom Accent Color (Change app Red color: subscribe button, like button, icons, progress bars, etc.)
    private val _customAccentColorHex = MutableStateFlow(prefs.getLong("custom_accent_color_hex", 0xFFFF0033L))
    val customAccentColorHex: StateFlow<Long> = _customAccentColorHex.asStateFlow()

    fun setCustomAccentColorHex(colorLong: Long) {
        _customAccentColorHex.value = colorLong
        prefs.edit().putLong("custom_accent_color_hex", colorLong).apply()
    }

    // Custom Background Photo (Persistent across app restarts)
    private val _customBackgroundUri = MutableStateFlow(prefs.getString("custom_background_uri", null))
    val customBackgroundUri: StateFlow<String?> = _customBackgroundUri.asStateFlow()

    fun setCustomBackgroundUri(uri: String?) {
        _customBackgroundUri.value = uri
        if (uri == null) {
            prefs.edit().remove("custom_background_uri").apply()
        } else {
            prefs.edit().putString("custom_background_uri", uri).apply()
        }
    }

    // Custom Background Dimming & Blur
    private val _customBackgroundDim = MutableStateFlow(prefs.getFloat("custom_background_dim", 0.75f))
    val customBackgroundDim: StateFlow<Float> = _customBackgroundDim.asStateFlow()

    fun setCustomBackgroundDim(dim: Float) {
        _customBackgroundDim.value = dim
        prefs.edit().putFloat("custom_background_dim", dim).apply()
    }

    // Ambient Neon Lighting / Glow for Player
    private val _ambientNeonLighting = MutableStateFlow(prefs.getBoolean("ambient_neon_lighting", true))
    val ambientNeonLighting: StateFlow<Boolean> = _ambientNeonLighting.asStateFlow()

    fun setAmbientNeonLighting(enabled: Boolean) {
        _ambientNeonLighting.value = enabled
        prefs.edit().putBoolean("ambient_neon_lighting", enabled).apply()
    }

    fun setUiTheme(theme: String) {
        _uiTheme.value = theme
        prefs.edit().putString("ui_theme", theme).apply()
        val isDark = theme != "Light Mode" && theme != "Liquid Glass (Light)"
        _darkTheme.value = isDark
        prefs.edit().putBoolean("dark_theme", isDark).apply()
        scope.launch { dataStore.setDarkMode(isDark) }
    }

    private val _doubleTapSeekSeconds = MutableStateFlow(prefs.getInt("seek_duration", 10))
    val doubleTapSeekSeconds: StateFlow<Int> = _doubleTapSeekSeconds.asStateFlow()

    private val _zoomToFill = MutableStateFlow(prefs.getBoolean("zoom_to_fill", false))
    val zoomToFill: StateFlow<Boolean> = _zoomToFill.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.getString("app_language", "English (US)") ?: "English (US)")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    // Playback
    private val _autoplayNext = MutableStateFlow(prefs.getBoolean("autoplay_next", true))
    val autoplayNext: StateFlow<Boolean> = _autoplayNext.asStateFlow()

    private val _defaultSpeed = MutableStateFlow(prefs.getFloat("default_speed", 1.0f))
    val defaultSpeed: StateFlow<Float> = _defaultSpeed.asStateFlow()

    private val _videoQuality = MutableStateFlow(prefs.getString("video_quality", "Auto (recommended)") ?: "Auto (recommended)")
    val videoQuality: StateFlow<String> = _videoQuality.asStateFlow()

    private val _captionsEnabled = MutableStateFlow(prefs.getBoolean("captions_enabled", false))
    val captionsEnabled: StateFlow<Boolean> = _captionsEnabled.asStateFlow()

    private val _captionSize = MutableStateFlow(prefs.getString("caption_size", "Normal") ?: "Normal")
    val captionSize: StateFlow<String> = _captionSize.asStateFlow()

    // Downloads
    private val _downloadQuality = MutableStateFlow(prefs.getString("download_quality", "Full HD (1080p)") ?: "Full HD (1080p)")
    val downloadQuality: StateFlow<String> = _downloadQuality.asStateFlow()

    private val _smartDownloads = MutableStateFlow(prefs.getBoolean("smart_downloads", true))
    val smartDownloads: StateFlow<Boolean> = _smartDownloads.asStateFlow()

    // History & Privacy
    private val _pauseHistory = MutableStateFlow(prefs.getBoolean("pause_history", false))
    val pauseHistory: StateFlow<Boolean> = _pauseHistory.asStateFlow()

    // Notifications
    private val _scheduledDigest = MutableStateFlow(prefs.getBoolean("scheduled_digest", true))
    val scheduledDigest: StateFlow<Boolean> = _scheduledDigest.asStateFlow()

    private val _subscriptionsNotifications = MutableStateFlow(prefs.getBoolean("subscriptions_notifications", true))
    val subscriptionsNotifications: StateFlow<Boolean> = _subscriptionsNotifications.asStateFlow()

    private val _recommendedNotifications = MutableStateFlow(prefs.getBoolean("recommended_notifications", true))
    val recommendedNotifications: StateFlow<Boolean> = _recommendedNotifications.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("notifications_master", true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _inAppSounds = MutableStateFlow(prefs.getBoolean("in_app_sounds", true))
    val inAppSounds: StateFlow<Boolean> = _inAppSounds.asStateFlow()

    // Feeds & Upload
    private val _playbackInFeeds = MutableStateFlow(prefs.getString("playback_in_feeds", "Always on") ?: "Always on")
    val playbackInFeeds: StateFlow<String> = _playbackInFeeds.asStateFlow()

    private val _uploadQuality = MutableStateFlow(prefs.getString("upload_quality", "Full HD (1080p)") ?: "Full HD (1080p)")
    val uploadQuality: StateFlow<String> = _uploadQuality.asStateFlow()

    // Download over Wi-Fi
    private val _downloadWifiOnly = MutableStateFlow(prefs.getBoolean("download_wifi_only", true))
    val downloadWifiOnly: StateFlow<Boolean> = _downloadWifiOnly.asStateFlow()

    // Search History Privacy
    private val _pauseSearchHistory = MutableStateFlow(prefs.getBoolean("pause_search_history", false))
    val pauseSearchHistory: StateFlow<Boolean> = _pauseSearchHistory.asStateFlow()

    // Experimental Features
    private val _experimentalAiClips = MutableStateFlow(prefs.getBoolean("experimental_ai_clips", true))
    val experimentalAiClips: StateFlow<Boolean> = _experimentalAiClips.asStateFlow()

    private val _experimentalAudioBooster = MutableStateFlow(prefs.getBoolean("experimental_audio_booster", false))
    val experimentalAudioBooster: StateFlow<Boolean> = _experimentalAudioBooster.asStateFlow()

    fun setRemindBreak(enabled: Boolean) {
        _remindBreak.value = enabled
        prefs.edit().putBoolean("remind_break", enabled).apply()
        scope.launch { dataStore.setRemindBreak(enabled) }
    }

    fun setBreakInterval(minutes: Int) {
        _breakIntervalMinutes.value = minutes
        prefs.edit().putInt("break_interval", minutes).apply()
        scope.launch { dataStore.setBreakIntervalMinutes(minutes) }
    }

    fun setRemindBedtime(enabled: Boolean) {
        _remindBedtime.value = enabled
        prefs.edit().putBoolean("remind_bedtime", enabled).apply()
        scope.launch { dataStore.setRemindBedtime(enabled) }
    }

    fun setBedtimeHour(hour: Int) {
        _bedtimeHour.value = hour
        prefs.edit().putInt("bedtime_hour", hour).apply()
    }

    fun setDarkTheme(enabled: Boolean) {
        _darkTheme.value = enabled
        _uiTheme.value = if (enabled) "Dark Mode" else "Light Mode"
        prefs.edit().putBoolean("dark_theme", enabled).putString("ui_theme", _uiTheme.value).apply()
        scope.launch { dataStore.setDarkMode(enabled) }
    }

    fun setDoubleTapSeekSeconds(seconds: Int) {
        _doubleTapSeekSeconds.value = seconds
        prefs.edit().putInt("seek_duration", seconds).apply()
        scope.launch { dataStore.setDoubleTapSeekSeconds(seconds) }
    }

    fun setZoomToFill(enabled: Boolean) {
        _zoomToFill.value = enabled
        prefs.edit().putBoolean("zoom_to_fill", enabled).apply()
        scope.launch { dataStore.setZoomToFill(enabled) }
    }

    fun setAppLanguage(lang: String) {
        _appLanguage.value = lang
        prefs.edit().putString("app_language", lang).apply()
    }

    fun setAutoplayNext(enabled: Boolean) {
        _autoplayNext.value = enabled
        prefs.edit().putBoolean("autoplay_next", enabled).apply()
        scope.launch { dataStore.setAutoplayNext(enabled) }
    }

    fun setDefaultSpeed(speed: Float) {
        _defaultSpeed.value = speed
        prefs.edit().putFloat("default_speed", speed).apply()
        scope.launch { dataStore.setPlaybackSpeed(speed) }
    }

    fun setVideoQuality(quality: String) {
        _videoQuality.value = quality
        prefs.edit().putString("video_quality", quality).apply()
        scope.launch { dataStore.setVideoQuality(quality) }
    }

    fun setCaptionsEnabled(enabled: Boolean) {
        _captionsEnabled.value = enabled
        prefs.edit().putBoolean("captions_enabled", enabled).apply()
        scope.launch { dataStore.setCaptionsEnabled(enabled) }
    }

    fun setCaptionSize(size: String) {
        _captionSize.value = size
        prefs.edit().putString("caption_size", size).apply()
    }

    fun setDownloadQuality(quality: String) {
        _downloadQuality.value = quality
        prefs.edit().putString("download_quality", quality).apply()
        scope.launch { dataStore.setDownloadQuality(quality) }
    }

    fun setSmartDownloads(enabled: Boolean) {
        _smartDownloads.value = enabled
        prefs.edit().putBoolean("smart_downloads", enabled).apply()
        scope.launch { dataStore.setSmartDownloads(enabled) }
    }

    fun setPauseHistory(paused: Boolean) {
        _pauseHistory.value = paused
        prefs.edit().putBoolean("pause_history", paused).apply()
        scope.launch { dataStore.setPauseHistory(paused) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("notifications_master", enabled).apply()
        scope.launch { dataStore.setNotificationsEnabled(enabled) }
    }

    fun setScheduledDigest(enabled: Boolean) {
        _scheduledDigest.value = enabled
        prefs.edit().putBoolean("scheduled_digest", enabled).apply()
        scope.launch { dataStore.setScheduledDigest(enabled) }
    }

    fun setInAppSounds(enabled: Boolean) {
        _inAppSounds.value = enabled
        prefs.edit().putBoolean("in_app_sounds", enabled).apply()
    }

    fun setPlaybackInFeeds(setting: String) {
        _playbackInFeeds.value = setting
        prefs.edit().putString("playback_in_feeds", setting).apply()
        scope.launch { dataStore.setPlaybackInFeeds(setting) }
    }

    fun setUploadQuality(quality: String) {
        _uploadQuality.value = quality
        prefs.edit().putString("upload_quality", quality).apply()
        scope.launch { dataStore.setUploadQuality(quality) }
    }

    fun setDownloadWifiOnly(enabled: Boolean) {
        _downloadWifiOnly.value = enabled
        prefs.edit().putBoolean("download_wifi_only", enabled).apply()
        scope.launch { dataStore.setDownloadWifiOnly(enabled) }
    }

    fun setPauseSearchHistory(paused: Boolean) {
        _pauseSearchHistory.value = paused
        prefs.edit().putBoolean("pause_search_history", paused).apply()
        scope.launch { dataStore.setPauseSearchHistory(paused) }
    }

    fun setSubscriptionsNotifications(enabled: Boolean) {
        _subscriptionsNotifications.value = enabled
        prefs.edit().putBoolean("subscriptions_notifications", enabled).apply()
        scope.launch { dataStore.setSubscriptionsNotifications(enabled) }
    }

    fun setRecommendedNotifications(enabled: Boolean) {
        _recommendedNotifications.value = enabled
        prefs.edit().putBoolean("recommended_notifications", enabled).apply()
        scope.launch { dataStore.setRecommendedNotifications(enabled) }
    }

    fun setExperimentalAiClips(enabled: Boolean) {
        _experimentalAiClips.value = enabled
        prefs.edit().putBoolean("experimental_ai_clips", enabled).apply()
    }

    fun setExperimentalAudioBooster(enabled: Boolean) {
        _experimentalAudioBooster.value = enabled
        prefs.edit().putBoolean("experimental_audio_booster", enabled).apply()
    }

    // Search History
    private val _searchHistory = MutableStateFlow<List<String>>(
        prefs.getStringSet("search_history_items", null)?.toList()
            ?: listOf("Trending Shorts", "Nature 4K", "Music Beat", "Local Media")
    )
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    fun addSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank() || _pauseSearchHistory.value) return
        val current = _searchHistory.value.toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        val trimmedList = current.take(20)
        _searchHistory.value = trimmedList
        prefs.edit().putStringSet("search_history_items", trimmedList.toSet()).apply()
    }

    fun removeSearchQuery(query: String) {
        val current = _searchHistory.value.toMutableList()
        current.remove(query)
        _searchHistory.value = current
        prefs.edit().putStringSet("search_history_items", current.toSet()).apply()
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
        prefs.edit().remove("search_history_items").apply()
    }

    // ==========================================
    // LIQUID GLASS & NEON CUSTOMIZATION SYSTEM
    // ==========================================
    private val _liquidGlassMaster = MutableStateFlow(prefs.getBoolean("lg_master_enabled", true))
    val liquidGlassMaster: StateFlow<Boolean> = _liquidGlassMaster.asStateFlow()

    private val _liquidGlassIntensity = MutableStateFlow(prefs.getFloat("lg_intensity", 0.85f))
    val liquidGlassIntensity: StateFlow<Float> = _liquidGlassIntensity.asStateFlow()

    private val _liquidGlassBlur = MutableStateFlow(prefs.getFloat("lg_blur", 24f))
    val liquidGlassBlur: StateFlow<Float> = _liquidGlassBlur.asStateFlow()

    private val _liquidGlassTransparency = MutableStateFlow(prefs.getFloat("lg_transparency", 0.70f))
    val liquidGlassTransparency: StateFlow<Float> = _liquidGlassTransparency.asStateFlow()

    private val _liquidGlassRefraction = MutableStateFlow(prefs.getFloat("lg_refraction", 0.65f))
    val liquidGlassRefraction: StateFlow<Float> = _liquidGlassRefraction.asStateFlow()

    private val _liquidGlassSpecular = MutableStateFlow(prefs.getBoolean("lg_specular", true))
    val liquidGlassSpecular: StateFlow<Boolean> = _liquidGlassSpecular.asStateFlow()

    private val _liquidGlassBorderThickness = MutableStateFlow(prefs.getFloat("lg_border_thickness", 1.0f))
    val liquidGlassBorderThickness: StateFlow<Float> = _liquidGlassBorderThickness.asStateFlow()

    // Neon Lights system
    private val _neonLightsMaster = MutableStateFlow(prefs.getBoolean("neon_master_enabled", false))
    val neonLightsMaster: StateFlow<Boolean> = _neonLightsMaster.asStateFlow()

    private val _neonColorHex = MutableStateFlow(prefs.getLong("neon_color_hex", 0xFF00E5FFL)) // Electric Cyan
    val neonColorHex: StateFlow<Long> = _neonColorHex.asStateFlow()

    private val _neonIntensity = MutableStateFlow(prefs.getFloat("neon_intensity", 0.85f))
    val neonIntensity: StateFlow<Float> = _neonIntensity.asStateFlow()

    private val _neonAnimated = MutableStateFlow(prefs.getBoolean("neon_animated", false))
    val neonAnimated: StateFlow<Boolean> = _neonAnimated.asStateFlow()

    private val _neonAnimationSpeed = MutableStateFlow(prefs.getFloat("neon_speed", 1.0f))
    val neonAnimationSpeed: StateFlow<Float> = _neonAnimationSpeed.asStateFlow()

    // Layout custom visibility & icon arrangement
    private val _showHomeTopShorts = MutableStateFlow(prefs.getBoolean("layout_show_home_shorts", true))
    val showHomeTopShorts: StateFlow<Boolean> = _showHomeTopShorts.asStateFlow()

    private val _showShortsActionRemix = MutableStateFlow(prefs.getBoolean("layout_shorts_remix", true))
    val showShortsActionRemix: StateFlow<Boolean> = _showShortsActionRemix.asStateFlow()

    private val _showShortsActionSave = MutableStateFlow(prefs.getBoolean("layout_shorts_save", true))
    val showShortsActionSave: StateFlow<Boolean> = _showShortsActionSave.asStateFlow()

    private val _playerControlsSize = MutableStateFlow(prefs.getFloat("layout_player_controls_size", 1.0f)) // 0.8 .. 1.4 scale
    val playerControlsSize: StateFlow<Float> = _playerControlsSize.asStateFlow()

    private val _iconStyleMode = MutableStateFlow(prefs.getString("layout_icon_style", "Glass") ?: "Glass") // Glass, Glow, Standard
    val iconStyleMode: StateFlow<String> = _iconStyleMode.asStateFlow()

    fun setLiquidGlassMaster(enabled: Boolean) {
        _liquidGlassMaster.value = enabled
        prefs.edit().putBoolean("lg_master_enabled", enabled).apply()
        if (enabled && !_uiTheme.value.contains("Liquid Glass")) {
            setUiTheme("Liquid Glass with Reflection")
        } else if (!enabled && _uiTheme.value.contains("Liquid Glass")) {
            setUiTheme("Dark Mode")
        }
    }

    fun setLiquidGlassIntensity(v: Float) {
        _liquidGlassIntensity.value = v
        prefs.edit().putFloat("lg_intensity", v).apply()
    }

    fun setLiquidGlassBlur(v: Float) {
        _liquidGlassBlur.value = v
        prefs.edit().putFloat("lg_blur", v).apply()
    }

    fun setLiquidGlassTransparency(v: Float) {
        _liquidGlassTransparency.value = v
        prefs.edit().putFloat("lg_transparency", v).apply()
    }

    fun setLiquidGlassRefraction(v: Float) {
        _liquidGlassRefraction.value = v
        prefs.edit().putFloat("lg_refraction", v).apply()
    }

    fun setLiquidGlassSpecular(v: Boolean) {
        _liquidGlassSpecular.value = v
        prefs.edit().putBoolean("lg_specular", v).apply()
    }

    fun setLiquidGlassBorderThickness(v: Float) {
        _liquidGlassBorderThickness.value = v
        prefs.edit().putFloat("lg_border_thickness", v).apply()
    }

    fun setNeonLightsMaster(enabled: Boolean) {
        _neonLightsMaster.value = enabled
        prefs.edit().putBoolean("neon_master_enabled", enabled).apply()
    }

    fun setNeonColorHex(colorLong: Long) {
        _neonColorHex.value = colorLong
        prefs.edit().putLong("neon_color_hex", colorLong).apply()
    }

    fun setNeonIntensity(v: Float) {
        _neonIntensity.value = v
        prefs.edit().putFloat("neon_intensity", v).apply()
    }

    fun setNeonAnimated(v: Boolean) {
        _neonAnimated.value = v
        prefs.edit().putBoolean("neon_animated", v).apply()
    }

    fun setNeonAnimationSpeed(v: Float) {
        _neonAnimationSpeed.value = v
        prefs.edit().putFloat("neon_speed", v).apply()
    }

    fun setShowHomeTopShorts(v: Boolean) {
        _showHomeTopShorts.value = v
        prefs.edit().putBoolean("layout_show_home_shorts", v).apply()
    }

    fun setShowShortsActionRemix(v: Boolean) {
        _showShortsActionRemix.value = v
        prefs.edit().putBoolean("layout_shorts_remix", v).apply()
    }

    fun setShowShortsActionSave(v: Boolean) {
        _showShortsActionSave.value = v
        prefs.edit().putBoolean("layout_shorts_save", v).apply()
    }

    fun setPlayerControlsSize(scale: Float) {
        _playerControlsSize.value = scale
        prefs.edit().putFloat("layout_player_controls_size", scale).apply()
    }

    fun setIconStyleMode(mode: String) {
        _iconStyleMode.value = mode
        prefs.edit().putString("layout_icon_style", mode).apply()
    }

    fun resetAllAppearance() {
        setLiquidGlassMaster(true)
        setLiquidGlassIntensity(0.85f)
        setLiquidGlassBlur(24f)
        setLiquidGlassTransparency(0.70f)
        setLiquidGlassRefraction(0.65f)
        setLiquidGlassSpecular(true)
        setLiquidGlassBorderThickness(1.0f)
        setNeonLightsMaster(false)
        setNeonColorHex(0xFF00E5FFL)
        setNeonIntensity(0.85f)
        setNeonAnimated(false)
        setNeonAnimationSpeed(1.0f)
        setCustomAccentColorHex(0xFFFF0033L)
        setCustomBackgroundUri(null)
        setCustomBackgroundDim(0.75f)
        setUiTheme("Liquid Glass with Reflection")
        setShowHomeTopShorts(true)
        setShowShortsActionRemix(true)
        setShowShortsActionSave(true)
        setPlayerControlsSize(1.0f)
        setIconStyleMode("Glass")
    }
}
