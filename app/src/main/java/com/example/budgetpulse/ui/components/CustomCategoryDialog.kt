package com.example.budgetpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.CategoryIcons
import com.example.budgetpulse.data.model.CategoryType

@Composable
fun CustomCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: CategoryType, iconName: String, colorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(CategoryType.EXPENSE) }
    var selectedIcon by remember { mutableStateOf("star") }
    var selectedColorHex by remember { mutableLongStateOf(0xFFFFF3E0) }

    val iconOptions = listOf(
        "star", "movie", "school", "medical_services", "trending_up",
        "flight", "pets", "fitness_center", "spa", "card_giftcard",
        "account_balance", "phone_android", "build"
    )

    val colorOptions = listOf(
        0xFFE3F2FD, // Soft Blue
        0xFFFFF3E0, // Soft Orange
        0xFFE8F5E9, // Soft Green
        0xFFF3E5F5, // Soft Purple
        0xFFFFEBEE, // Soft Pink
        0xFFE0F2F1, // Soft Teal
        0xFFFFF8E1  // Soft Gold
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Custom Category", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Type Selection
                Text("Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = selectedType == CategoryType.EXPENSE,
                        onClick = { selectedType = CategoryType.EXPENSE },
                        label = { Text("Expense") }
                    )
                    FilterChip(
                        selected = selectedType == CategoryType.INCOME,
                        onClick = { selectedType = CategoryType.INCOME },
                        label = { Text("Income") }
                    )
                }

                // Icon Selection
                Text("Icon", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(iconOptions) { iconName ->
                        val isSelected = selectedIcon == iconName
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedIcon = iconName }
                        ) {
                            Icon(
                                imageVector = CategoryIcons.getIcon(iconName),
                                contentDescription = iconName,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Color Selection
                Text("Card Color", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colorOptions) { colorHex ->
                        val isSelected = selectedColorHex == colorHex
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex))
                                .clickable { selectedColorHex = colorHex }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedType, selectedIcon, selectedColorHex)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}