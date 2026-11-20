package com.example.budgetpulse.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NavTab {
    HOME,
    TRANSACTIONS,
    BUDGET,
    SETTINGS
}

@Composable
fun AppBottomNavigationBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // On tablets / landscape the NavigationRail (AdaptiveNavigation.kt) is shown instead
    if (useNavigationRail()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) }
                )

                NavItem(
                    label = "Transactions",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    isSelected = currentTab == NavTab.TRANSACTIONS,
                    onClick = { onTabSelected(NavTab.TRANSACTIONS) }
                )

                NavItem(
                    label = "Budget",
                    icon = Icons.AutoMirrored.Filled.ShowChart,
                    isSelected = currentTab == NavTab.BUDGET,
                    onClick = { onTabSelected(NavTab.BUDGET) }
                )

                NavItem(
                    label = "Setting",
                    icon = Icons.Default.Settings,
                    isSelected = currentTab == NavTab.SETTINGS,
                    onClick = { onTabSelected(NavTab.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val pillBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFFD54F) else Color.Transparent,
        animationSpec = tween(300),
        label = "pillBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "contentColor"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(pillBackgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}