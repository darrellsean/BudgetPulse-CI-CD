package com.example.budgetpulse.ui.screens

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.budgetpulse.data.model.ProfileData
import com.example.budgetpulse.ui.components.NavTab

@Composable
fun WelcomeScreen(
    profileData: ProfileData,
    onSelectDestination: (NavTab) -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp) // tablets: keep content readable instead of stretched
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Greeting Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { -30 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Brand Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFD54F).copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = "BudgetPulse Logo",
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BudgetPulse",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Avatar + Hello "Name" Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (profileData.profileImageUri != null) {
                            AsyncImage(
                                model = profileData.profileImageUri,
                                contentDescription = "Profile Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFFFFD54F), CircleShape)
                            )
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF8E1))
                                    .border(2.dp, Color(0xFFFFD54F), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile Avatar",
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Hello, ${profileData.nickname}!",
                                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Where would you like to start today?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Destination Options List
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(700, delayMillis = 150, easing = FastOutSlowInEasing)
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    WelcomeDestinationCard(
                        title = "Home & Categories",
                        subtitle = "Manage your expense & income categories",
                        icon = Icons.Default.Home,
                        iconBgHex = 0xFFE3F2FD,
                        iconTint = Color(0xFF1E88E5),
                        onClick = { onSelectDestination(NavTab.HOME) }
                    )

                    WelcomeDestinationCard(
                        title = "Transactions Log",
                        subtitle = "View calendar & daily spending records",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconBgHex = 0xFFE8F5E9,
                        iconTint = Color(0xFF43A047),
                        onClick = { onSelectDestination(NavTab.TRANSACTIONS) }
                    )

                    WelcomeDestinationCard(
                        title = "Budget Analytics",
                        subtitle = "Track monthly limits & remaining balance",
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                        iconBgHex = 0xFFF3E5F5,
                        iconTint = Color(0xFF8E24AA),
                        onClick = { onSelectDestination(NavTab.BUDGET) }
                    )

                    WelcomeDestinationCard(
                        title = "Profile & Settings",
                        subtitle = "Edit nickname, photo, theme & currency (${profileData.selectedCurrency.symbol})",
                        icon = Icons.Default.Settings,
                        iconBgHex = 0xFFFFF3E0,
                        iconTint = Color(0xFFFB8C00),
                        onClick = { onSelectDestination(NavTab.SETTINGS) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Primary CTA Button (Yellow Accent matching AppBottomNavigationBar)
            Button(
                onClick = { onSelectDestination(NavTab.HOME) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD54F),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Go to Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to Dashboard",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun WelcomeDestinationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgHex: Long,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(iconBgHex))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open $title",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}