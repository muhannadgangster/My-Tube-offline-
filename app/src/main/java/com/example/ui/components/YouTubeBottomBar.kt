package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalUiTheme

/**
 * YouTube Navigation Bar exactly matching Screenshot_20261002-122340~3.jpg:
 * 1. Home: Outlined house with inner arched doorway + "Home" label
 * 2. Shorts: Authentic YouTube Shorts ribbon outline with inner play triangle + "Shorts" label
 * 3. Plus (+): Solid dark circular button (#272727) with centered thick white plus icon (no label)
 * 4. Subscriptions: Box with top cap bar and inner play triangle + "Subscriptions" label
 * 5. You: Purple circular avatar (#8E24AA) with bold white "M" + "You" label
 */
@Composable
fun YouTubeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val activeColor = MaterialTheme.colorScheme.onSurface
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor)
            .navigationBarsPadding()
    ) {
        // Thin subtle top border divider
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            thickness = 0.6.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home Tab
            BottomNavItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_yt_home_outline),
                        contentDescription = "Home",
                        tint = if (selectedTab == 0) activeColor else inactiveColor,
                        modifier = Modifier.size(23.dp)
                    )
                },
                label = "Home",
                isSelected = selectedTab == 0,
                testTag = "nav_home",
                onClick = { onTabSelected(0) }
            )

            // 2. Shorts Tab (Official YouTube Shorts outline logo)
            BottomNavItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shorts_logo_outline),
                        contentDescription = "Shorts",
                        tint = if (selectedTab == 1) activeColor else inactiveColor,
                        modifier = Modifier.size(23.dp)
                    )
                },
                label = "Shorts",
                isSelected = selectedTab == 1,
                testTag = "nav_shorts",
                onClick = { onTabSelected(1) }
            )

            // 3. Center Plus (+) Button (Solid circle with white plus, no text label)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(2) }
                    .testTag("nav_add_plus"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create / Camera",
                    tint = activeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Subscriptions Tab (Official YouTube Subscriptions icon)
            BottomNavItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_yt_subscriptions_outline),
                        contentDescription = "Subscriptions",
                        tint = if (selectedTab == 3) activeColor else inactiveColor,
                        modifier = Modifier.size(23.dp)
                    )
                },
                label = "Subscriptions",
                isSelected = selectedTab == 3,
                testTag = "nav_subscriptions",
                onClick = { onTabSelected(3) }
            )

            // 5. You Tab (Purple circle with bold white "M" + "You" label)
            BottomNavItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8E24AA))
                            .then(
                                if (selectedTab == 4) {
                                    Modifier.border(1.5.dp, Color.White, CircleShape)
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                },
                label = "You",
                isSelected = selectedTab == 4,
                testTag = "nav_you",
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
