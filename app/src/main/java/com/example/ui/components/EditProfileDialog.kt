package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

/**
 * Offline Edit Profile Dialog:
 * - Select custom avatar image from phone gallery via Photo Picker (zero broad storage permission)
 * - Edit Display Name and @username
 * - Saves locally to persistent storage
 */
@Composable
fun EditProfileDialog(
    currentName: String,
    currentHandle: String,
    currentAvatarUri: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, handle: String, avatarUri: String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var handle by remember { mutableStateOf(currentHandle) }
    var selectedAvatarUri by remember { mutableStateOf(currentAvatarUri) }

    // Zero-permission Android Photo Picker for profile photo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri.toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = YtSurfaceDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top title with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Channel Profile",
                        color = YtTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = YtTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar Picker
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!selectedAvatarUri.isNullOrBlank()) {
                        AsyncImage(
                            model = selectedAvatarUri,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(YtAvatarPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase().ifBlank { "M" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp
                            )
                        }
                    }

                    // Camera overlay icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(YtRed)
                            .border(2.dp, YtSurfaceDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tap to choose gallery photo",
                    color = YtTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Display Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YtRed,
                        unfocusedBorderColor = YtBorder,
                        focusedTextColor = YtTextPrimary,
                        unfocusedTextColor = YtTextPrimary,
                        focusedLabelColor = YtRed,
                        unfocusedLabelColor = YtTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Handle / Username Field
                OutlinedTextField(
                    value = handle,
                    onValueChange = {
                        val formatted = if (it.startsWith("@")) it else "@$it"
                        handle = formatted
                    },
                    label = { Text("Handle / Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_handle_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YtRed,
                        unfocusedBorderColor = YtBorder,
                        focusedTextColor = YtTextPrimary,
                        unfocusedTextColor = YtTextPrimary,
                        focusedLabelColor = YtRed,
                        unfocusedLabelColor = YtTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions: Cancel & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text("Cancel", color = YtTextPrimary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), handle.trim(), selectedAvatarUri)
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = YtRed),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
