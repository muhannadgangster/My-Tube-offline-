package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.VideoItem
import com.example.data.local.CommentEntity
import com.example.ui.theme.YtAvatarPurple
import com.example.ui.theme.YtBorder
import com.example.ui.theme.YtRed
import com.example.ui.theme.YtSurfaceDark
import com.example.ui.theme.YtTextPrimary
import com.example.ui.theme.YtTextSecondary

/**
 * Authentic YouTube Shorts Bottom-Sheet Comment Modal:
 * - Live real-time comments list backed by Room Database
 * - Live counter in header
 * - Sticky bottom input bar with avatar and send action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSheet(
    video: VideoItem,
    comments: List<CommentEntity> = emptyList(),
    onDismiss: () -> Unit,
    onPostComment: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var input by remember { mutableStateOf("") }
    val totalCount = comments.size + if (video.userNotes.isNotBlank()) 1 else 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = YtSurfaceDark,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(top = 12.dp)
        ) {
            // Header: Title with Live Count and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Comments",
                    color = YtTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$totalCount",
                    color = YtTextSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.weight(1f))

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

            HorizontalDivider(
                color = YtBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = 340.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // If user had a legacy offline note
                if (video.userNotes.isNotBlank()) {
                    item(key = "user_note_${video.id}") {
                        CommentRow(
                            authorName = "Muhannad Murtaza",
                            authorHandle = "@muhannad",
                            authorLetter = "M",
                            avatarColor = YtAvatarPurple,
                            timeAgo = "Saved Note",
                            commentText = video.userNotes,
                            likes = 1
                        )
                    }
                }

                // Real-time Database Comments
                items(comments, key = { it.commentId }) { comment ->
                    CommentRow(
                        authorName = comment.authorName,
                        authorHandle = comment.authorHandle,
                        authorLetter = comment.authorAvatarLetter,
                        avatarColor = if (comment.authorAvatarLetter == "M") YtAvatarPurple else Color(0xFF1E88E5),
                        timeAgo = comment.formattedTime,
                        commentText = comment.text,
                        likes = comment.likesCount
                    )
                }

                if (comments.isEmpty() && video.userNotes.isBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No comments yet",
                                    color = YtTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Be the first to share what you think!",
                                    color = YtTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                color = YtBorder.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )

            // Sticky Bottom Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(YtAvatarPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = {
                        Text(
                            "Add a comment...",
                            color = YtTextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YtRed,
                        unfocusedBorderColor = YtBorder,
                        focusedTextColor = YtTextPrimary,
                        unfocusedTextColor = YtTextPrimary,
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            onPostComment(input.trim())
                            input = ""
                        }
                    },
                    enabled = input.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (input.isNotBlank()) YtRed else Color(0xFF2A2A2A))
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post comment",
                        tint = if (input.isNotBlank()) Color.White else Color(0x66FFFFFF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    authorName: String,
    authorHandle: String,
    authorLetter: String,
    avatarColor: Color,
    timeAgo: String,
    commentText: String,
    likes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = authorLetter,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = authorHandle,
                    color = YtTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• $timeAgo",
                    color = YtTextSecondary.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = commentText,
                color = YtTextPrimary,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "Like comment",
                    tint = YtTextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                if (likes > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$likes",
                        color = YtTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
