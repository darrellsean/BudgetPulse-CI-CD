package com.example.budgetpulse.ui.screens

import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.budgetpulse.ui.components.AppBottomNavigationBar
import com.example.budgetpulse.ui.components.EditNicknameDialog
import com.example.budgetpulse.ui.components.NavTab
import com.example.budgetpulse.ui.components.SettingsDialog
import com.example.budgetpulse.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateTab: (NavTab) -> Unit,
    viewModel: ProfileViewModel,
    onRestartSetup: () -> Unit = {}
) {
    var showRestartDialog by remember { mutableStateOf(false) }
    val state by viewModel.profileState.collectAsState()
    var showEditNicknameDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Modern photo picker contract
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.updateProfileImage(uri) }
    )

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentTab = NavTab.SETTINGS,
                onTabSelected = onNavigateTab
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp) // tablets: keep content readable instead of stretched
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Top Profile Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image with click-to-upload
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(96.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        if (state.profileImageUri != null) {
                            AsyncImage(
                                model = state.profileImageUri,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEE58).copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Avatar",
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }

                        // Small camera overlay badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Upload Photo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nickname with edit icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showEditNicknameDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = state.nickname,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Nickname",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Three Stats Columns: Streak, Days, Transactions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatColumn(count = "${state.currentStreak}", label = "Current Streak")
                        ProfileStatColumn(count = "${state.totalDays}", label = "Total Days")
                        ProfileStatColumn(count = "${state.transactionsCount}", label = "Transactions")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Navigation Rows (Replacing Ledger Manager to Live Activity)
            Text(
                text = "Pages Navigation",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp),
                fontWeight = FontWeight.SemiBold
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Home,
                        iconTint = Color(0xFF1E88E5),
                        title = "Home Screen",
                        onClick = { onNavigateTab(NavTab.HOME) }
                    )
                    ProfileMenuRow(
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = Color(0xFF43A047),
                        title = "Transactions Log",
                        onClick = { onNavigateTab(NavTab.TRANSACTIONS) }
                    )
                    ProfileMenuRow(
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        iconTint = Color(0xFF8E24AA),
                        title = "Budget Analytics",
                        onClick = { onNavigateTab(NavTab.BUDGET) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Application Preferences Row
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp),
                fontWeight = FontWeight.SemiBold
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileMenuRow(
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFFFF8F00),
                        title = "Settings",
                        badge = "${state.selectedCurrency.symbol} • ${if (state.isDarkMode) "Dark" else "Light"}",
                        onClick = { showSettingsDialog = true }
                    )
                    // Clears the onboarding flag in DataStore -> app redirects to the Setup screen
                    ProfileMenuRow(
                        icon = Icons.Default.RestartAlt,
                        iconTint = Color(0xFFE53935),
                        title = "Restart Setup",
                        onClick = { showRestartDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Edit Nickname Dialog
        if (showEditNicknameDialog) {
            EditNicknameDialog(
                currentNickname = state.nickname,
                onDismiss = { showEditNicknameDialog = false },
                onSave = {
                    viewModel.updateNickname(it)
                    showEditNicknameDialog = false
                }
            )
        }

        // Restart Setup confirmation
        if (showRestartDialog) {
            AlertDialog(
                onDismissRequest = { showRestartDialog = false },
                title = { Text("Restart setup?") },
                text = { Text("You will go back to the setup screen. Your transactions are kept.") },
                confirmButton = {
                    TextButton(onClick = {
                        showRestartDialog = false
                        onRestartSetup()
                    }) { Text("Restart") }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Settings Dialog (Theme, Font Scale, Currency)
        if (showSettingsDialog) {
            SettingsDialog(
                isDarkMode = state.isDarkMode,
                currentScale = state.fontScale,
                currentCurrency = state.selectedCurrency,
                onToggleTheme = { viewModel.toggleTheme(it) },
                onScaleChange = { viewModel.updateFontScale(it) },
                onCurrencyChange = { viewModel.updateCurrency(it) },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

@Composable
private fun ProfileStatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}