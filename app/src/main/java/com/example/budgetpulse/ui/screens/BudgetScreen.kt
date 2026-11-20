package com.example.budgetpulse.ui.screens

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetpulse.data.model.CategoryBudget
import com.example.budgetpulse.ui.components.AppBottomNavigationBar
import com.example.budgetpulse.ui.components.BudgetCategoryRow
import com.example.budgetpulse.ui.components.BudgetOverviewCard
import com.example.budgetpulse.ui.components.FinancialInsightCard
import com.example.budgetpulse.ui.components.NavTab
import com.example.budgetpulse.ui.components.PlanRemainToggle
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import com.example.budgetpulse.ui.viewmodel.BudgetViewModel
import com.example.budgetpulse.ui.viewmodel.InsightsViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BudgetScreen(
    onNavigateTab: (NavTab) -> Unit = {},
    viewModel: BudgetViewModel = viewModel(factory = BudgetViewModel.Factory),
    insightsViewModel: InsightsViewModel = viewModel(factory = InsightsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val insightsState by insightsViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(
                currentTab = NavTab.BUDGET,
                onTabSelected = onNavigateTab
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Adapts to Light / Dark automatically
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .wrapContentWidth(Alignment.CenterHorizontally)
                .widthIn(max = 720.dp) // tablets: keep content readable instead of stretched
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Top Header Bar
            BudgetTopHeader(
                selectedMonth = uiState.selectedMonth,
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Budget Overview Card
            BudgetOverviewCard(
                totalBudget = uiState.totalBudget,
                dailyAverage = uiState.dailyAverage,
                progressFraction = uiState.progressFraction,
                totalSpent = uiState.totalSpent
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Scrollable Network Insights Card + Category Budgets List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                // Online Financial Advice & Remote Banner Image (REST API + Coil)
                item(key = "financial_insight_card") {
                    FinancialInsightCard(
                        uiState = insightsState,
                        onRefreshClick = { insightsViewModel.loadFinancialTip() }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Category Budgets
                items(uiState.categoryBudgets, key = { it.category.id }) { item ->
                    BudgetCategoryRow(
                        item = item,
                        viewMode = uiState.viewMode,
                        onClick = { viewModel.startEditingBudget(item) }
                    )
                }
            }

            // 4. Segmented Plan vs Remain Toggle
            PlanRemainToggle(
                selectedMode = uiState.viewMode,
                onModeSelected = { viewModel.setViewMode(it) }
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        // Edit Budget Dialog
        uiState.editingCategoryBudget?.let { item ->
            EditCategoryBudgetDialog(
                item = item,
                onDismiss = { viewModel.dismissEditingBudget() },
                onSave = { amount ->
                    viewModel.updateAllocatedBudget(item.category.id, amount)
                }
            )
        }
    }
}

@Composable
private fun BudgetTopHeader(
    selectedMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { }
        ) {
            Text(
                text = "Bik",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Switch Account",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPreviousMonth, modifier = Modifier.size(26.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            val isCurrent = selectedMonth == YearMonth.now()
            val monthLabel = if (isCurrent) {
                "This Month"
            } else {
                selectedMonth.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH))
            }
            Text(
                text = monthLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onNextMonth, modifier = Modifier.size(26.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Budget",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EditCategoryBudgetDialog(
    item: CategoryBudget,
    onDismiss: () -> Unit,
    onSave: (Double?) -> Unit
) {
    val currency = LocalAppCurrency.current.symbol
    var textValue by remember {
        mutableStateOf(item.allocatedBudget?.toString() ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Set Budget for ${item.category.name}", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text("Budget Allocation (empty for None)") },
                prefix = { Text("$currency ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                val parsed = textValue.toDoubleOrNull()
                onSave(parsed)
            }) {
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