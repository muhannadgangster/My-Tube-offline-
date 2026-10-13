package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Catalog of built-in system and media icons for the Interface Builder
 */
object IconCatalog {
    val AVAILABLE_ICONS: List<Pair<String, ImageVector>> = listOf(
        "Star" to Icons.Default.Star,
        "Favorite" to Icons.Default.Favorite,
        "Bookmark" to Icons.Default.Bookmark,
        "Music" to Icons.Default.MusicNote,
        "Camera" to Icons.Default.CameraAlt,
        "Folder" to Icons.Default.Folder,
        "Download" to Icons.Default.Download,
        "History" to Icons.Default.History,
        "List" to Icons.AutoMirrored.Filled.List,
        "Playlist" to Icons.AutoMirrored.Filled.PlaylistAdd,
        "Settings" to Icons.Default.Settings,
        "Tune" to Icons.Default.Tune,
        "Search" to Icons.Default.Search,
        "Notifications" to Icons.Default.Notifications,
        "Shuffle" to Icons.Default.Shuffle,
        "Share" to Icons.Default.Share,
        "ThumbUp" to Icons.Default.ThumbUp,
        "Info" to Icons.Default.Info,
        "Home" to Icons.Default.Home,
        "Shorts" to Icons.Default.Whatshot,
        "Library" to Icons.Default.VideoLibrary,
        "Person" to Icons.Default.Person,
        "Play" to Icons.Default.PlayArrow,
        "Add" to Icons.Default.Add,
        "Edit" to Icons.Default.Edit,
        "Delete" to Icons.Default.Delete,
        "Visibility" to Icons.Default.Visibility,
        "Back" to Icons.AutoMirrored.Filled.ArrowBack
    )

    fun getIconByName(name: String): ImageVector {
        return AVAILABLE_ICONS.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second
            ?: Icons.Default.Star
    }
}
