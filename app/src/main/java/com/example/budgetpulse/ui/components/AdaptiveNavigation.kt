package com.example.budgetpulse.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Material window size classes (based on available width):
 *  COMPACT  < 600dp  -> phone portrait   -> bottom navigation bar
 *  MEDIUM   < 840dp  -> phone landscape / small tablet -> navigation rail
 *  EXPANDED >= 840dp -> tablet / foldable -> navigation rail + wider layouts
 */
enum class WindowWidthClass { COMPACT, MEDIUM, EXPANDED }

fun windowWidthClassFor(widthDp: Int): WindowWidthClass = when {
    widthDp < 600 -> WindowWidthClass.COMPACT
    widthDp < 840 -> WindowWidthClass.MEDIUM
    else -> WindowWidthClass.EXPANDED
}

/** Provided once in MainActivity, read by any screen that adapts its layout. */
val LocalWindowWidthClass = staticCompositionLocalOf { WindowWidthClass.COMPACT }

/** true when the side NavigationRail is used instead of the bottom bar. */
@Composable
fun useNavigationRail(): Boolean = LocalWindowWidthClass.current != WindowWidthClass.COMPACT

private data class TabItem(val tab: NavTab, val label: String, val icon: ImageVector)

private val tabItems = listOf(
    TabItem(NavTab.HOME, "Home", Icons.Default.Home),
    TabItem(NavTab.TRANSACTIONS, "Transactions", Icons.AutoMirrored.Filled.ReceiptLong),
    TabItem(NavTab.BUDGET, "Budget", Icons.AutoMirrored.Filled.ShowChart),
    TabItem(NavTab.SETTINGS, "Setting", Icons.Default.Settings)
)

/** Side navigation used on tablets / landscape instead of AppBottomNavigationBar. */
@Composable
fun AppNavigationRail(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        header = {
            Icon(
                imageVector = Icons.Default.Wallet,
                contentDescription = "BudgetPulse",
                tint = Color(0xFFF57F17),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    ) {
        tabItems.forEach { item ->
            NavigationRailItem(
                selected = currentTab == item.tab,
                onClick = { onTabSelected(item.tab) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = Color(0xFFFFD54F),
                    selectedIconColor = Color.Black,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}