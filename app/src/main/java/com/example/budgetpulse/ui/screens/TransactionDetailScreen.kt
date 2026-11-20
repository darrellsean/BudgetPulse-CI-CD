package com.example.budgetpulse.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetpulse.data.model.CategoryIcons
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import com.example.budgetpulse.ui.viewmodel.TransactionDetailViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

/**
 * Opened from the Transactions page with the selected transaction's id
 * (route "transaction/{transactionId}"). Lets the user edit or delete it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    onBack: () -> Unit,
    viewModel: TransactionDetailViewModel = viewModel(factory = TransactionDetailViewModel.Factory)
) {
    val transaction by viewModel.transaction.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Go back automatically after saving / deleting
    LaunchedEffect(isFinished) {
        if (isFinished) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (transaction != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete transaction",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val item = transaction
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            TransactionEditForm(
                item = item,
                onSave = { amount, description, date -> viewModel.saveChanges(item, amount, description, date) },
                onDelete = { showDeleteDialog = true },
                modifier = Modifier.padding(innerPadding)
            )

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete transaction?") },
                    text = { Text("This ${item.categoryName} record will be removed permanently.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.delete(item)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionEditForm(
    item: TransactionItem,
    onSave: (amount: Double, description: String, date: LocalDate) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currency = LocalAppCurrency.current.symbol
    val isIncome = item.type == CategoryType.INCOME
    val accent = if (isIncome) Color(0xFF43A047) else Color(0xFFE53935)

    // Form state starts from the saved transaction
    var amountText by rememberSaveable(item.id) { mutableStateOf(String.format(Locale.ENGLISH, "%.2f", abs(item.amount))) }
    var description by rememberSaveable(item.id) { mutableStateOf(item.note) }
    var dateText by rememberSaveable(item.id) { mutableStateOf(item.date.toString()) }
    var showError by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val date = LocalDate.parse(dateText)

    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 600.dp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Category header
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(item.categoryColorHex))
                ) {
                    Icon(
                        imageVector = CategoryIcons.getIcon(item.categoryIconName),
                        contentDescription = item.categoryName,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(item.categoryName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = (if (isIncome) "Income" else "Expense") + " • recorded at ${item.formattedTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = accent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 80) description = it },
            label = { Text("Description (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Date: " + date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val amount = amountText.replace(",", "").toDoubleOrNull()
                if (amount == null || amount <= 0.0) showError = true else onSave(amount, description, date)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F), contentColor = Color.Black),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Save Changes", fontWeight = FontWeight.Bold)
        }

        TextButton(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Transaction", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }

    if (showDatePicker) {
        val todayMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            // Can't move a record into the future
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        dateText = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}