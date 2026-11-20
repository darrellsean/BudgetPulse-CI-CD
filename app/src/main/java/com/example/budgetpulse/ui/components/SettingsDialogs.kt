package com.example.budgetpulse.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetpulse.data.model.AppCurrency

@Composable
fun EditNicknameDialog(
    currentNickname: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentNickname) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Nickname", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Nickname") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text) }, enabled = text.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SettingsDialog(
    isDarkMode: Boolean,
    currentScale: Float,
    currentCurrency: AppCurrency,
    onToggleTheme: (Boolean) -> Unit,
    onScaleChange: (Float) -> Unit,
    onCurrencyChange: (AppCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Theme Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (isDarkMode) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isDarkMode, onCheckedChange = onToggleTheme)
                }

                // 2. Font Size Scaling
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Font Scale", fontWeight = FontWeight.SemiBold)
                        val sizeLabel = when {
                            currentScale < 0.95f -> "Small"
                            currentScale > 1.05f -> "Large"
                            else -> "Normal"
                        }
                        Text(
                            text = sizeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    var sliderValue by remember { mutableFloatStateOf(currentScale) }
                    Slider(
                        value = sliderValue,
                        onValueChange = {
                            sliderValue = it
                            onScaleChange(it)
                        },
                        valueRange = 0.85f..1.15f,
                        steps = 1
                    )
                }

                // 3. Base Currency Picker
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCurrencyDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Currency", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = currentCurrency.symbol,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )

    if (showCurrencyDialog) {
        CurrencyPickerDialog(
            selectedCurrency = currentCurrency,
            onSelect = {
                onCurrencyChange(it)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
}

@Composable
fun CurrencyPickerDialog(
    selectedCurrency: AppCurrency,
    onSelect: (AppCurrency) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppCurrency.entries.forEach { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(currency) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currency == selectedCurrency,
                            onClick = { onSelect(currency) }
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(text = "${currency.symbol} - ${currency.displayName}")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}