package com.example.budgetpulse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.CategoryIcons
import com.example.budgetpulse.data.model.CategoryItem
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Shown when a category card is tapped on the Home page.
 * Records an expense / income for TODAY with an optional description.
 */
@Composable
fun AddTransactionDialog(
    category: CategoryItem,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String) -> Unit
) {
    val currency = LocalAppCurrency.current.symbol
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val isIncome = category.type == CategoryType.INCOME
    val accent = if (isIncome) Color(0xFF43A047) else Color(0xFFE53935)
    val todayLabel = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH))

    fun submit() {
        val amount = amountText.replace(",", "").toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            showError = true
        } else {
            onConfirm(amount, description)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(category.colorHex))
            ) {
                Icon(
                    imageVector = CategoryIcons.getIcon(category.iconName),
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isIncome) "Add Income" else "Add Expense",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${category.name} • $todayLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        showError = false
                    },
                    label = { Text("Amount") },
                    prefix = { Text("$currency ", color = accent, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Please enter an amount greater than 0") }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 80) description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text(if (isIncome) "e.g. Monthly paycheck" else "e.g. Lunch with team") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { submit() }) {
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