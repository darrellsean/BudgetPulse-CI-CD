package com.example.budgetpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.ui.components.AppBottomNavigationBar
import com.example.budgetpulse.ui.components.CalendarView
import com.example.budgetpulse.ui.components.CategoryListHeader
import com.example.budgetpulse.ui.components.CategoryTransactionGroupCard
import com.example.budgetpulse.ui.components.FinancialSummaryCard
import com.example.budgetpulse.ui.components.NavTab
import com.example.budgetpulse.ui.components.SummaryPeriodSelector
import com.example.budgetpulse.ui.components.WindowWidthClass
import com.example.budgetpulse.ui.components.LocalWindowWidthClass
import com.example.budgetpulse.ui.viewmodel.SummaryPeriod
import com.example.budgetpulse.ui.viewmodel.TransactionUiState
import com.example.budgetpulse.ui.viewmodel.TransactionViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onNavigateTab: (NavTab) -> Unit = {},
    onOpenTransaction: (Int) -> Unit = {},
    viewModel: TransactionViewModel = viewModel(factory = TransactionViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val isWide = LocalWindowWidthClass.current != WindowWidthClass.COMPACT

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // Same navbar setup as Budget & Settings pages (hidden on wide screens, rail is used instead)
        bottomBar = {
            AppBottomNavigationBar(
                currentTab = NavTab.TRANSACTIONS,
                onTabSelected = onNavigateTab
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val listPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)

        if (isWide) {
            // Tablet / landscape: calendar + summary on the left, category list on the right
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = listPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    overviewItems(uiState, viewModel)
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = listPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categoryItems(uiState, viewModel, onOpenTransaction)
                }
            }
        } else {
            // Phone portrait: everything in one scrolling column
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = listPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                overviewItems(uiState, viewModel)
                categoryItems(uiState, viewModel, onOpenTransaction)
            }
        }
    }
}

/** Calendar (daily amounts) + Daily / Weekly / Monthly selector + totals card. */
private fun LazyListScope.overviewItems(
    uiState: TransactionUiState,
    viewModel: TransactionViewModel
) {
    item(key = "calendar") {
        CalendarView(
            selectedMonth = uiState.selectedMonth,
            selectedDate = uiState.selectedDate,
            dailySummaries = uiState.dailySummaries,
            onDateSelected = { date -> viewModel.selectDate(date) },
            onPreviousMonth = { viewModel.previousMonth() },
            onNextMonth = { viewModel.nextMonth() },
            onTodayClick = { viewModel.goToToday() }
        )
    }

    item(key = "period") {
        SummaryPeriodSelector(
            selected = uiState.period,
            onSelected = { viewModel.setPeriod(it) },
            rangeLabel = periodLabel(uiState)
        )
    }

    item(key = "summary") {
        FinancialSummaryCard(
            income = uiState.periodIncome,
            expense = uiState.periodExpense,
            balance = uiState.periodBalance
        )
    }
}

/** Expense / Income categories, each listing its transactions with descriptions. */
private fun LazyListScope.categoryItems(
    uiState: TransactionUiState,
    viewModel: TransactionViewModel,
    onOpenTransaction: (Int) -> Unit
) {
    item(key = "category_header") {
        CategoryListHeader(
            listType = uiState.listType,
            onListTypeChange = { viewModel.setListType(it) },
            visibleTotal = uiState.visibleTotal
        )
    }

    val groups = uiState.categoryGroups
    if (groups.isEmpty()) {
        item(key = "empty") {
            val typeLabel = if (uiState.listType == CategoryType.INCOME) "income" else "expenses"
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            ) {
                Text(
                    text = "No $typeLabel recorded for ${periodLabel(uiState).lowercase(Locale.ENGLISH)}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        items(items = groups, key = { "cat_${it.type}_${it.categoryName}" }) { group ->
            CategoryTransactionGroupCard(
                group = group,
                sectionTotal = uiState.visibleTotal,
                onTransactionClick = { onOpenTransaction(it.id) }
            )
        }
    }
}

private fun periodLabel(uiState: TransactionUiState): String {
    val dayFmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)
    val shortFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
    return when (uiState.period) {
        SummaryPeriod.DAY -> uiState.selectedDate.format(dayFmt)
        SummaryPeriod.WEEK -> "Week of ${uiState.periodStart.format(shortFmt)} – ${uiState.periodEnd.format(shortFmt)}"
        SummaryPeriod.MONTH -> uiState.selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    }
}