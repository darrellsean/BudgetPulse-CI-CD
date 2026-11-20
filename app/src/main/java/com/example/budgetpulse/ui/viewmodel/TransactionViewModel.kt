package com.example.budgetpulse.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.DailySummary
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

/** Which period the summary card + category list show. */
enum class SummaryPeriod(val label: String) {
    DAY("Daily"),
    WEEK("Weekly"),
    MONTH("Monthly")
}

/** One category block in the bottom list of the Transactions page. */
data class CategoryTransactionGroup(
    val categoryName: String,
    val iconName: String,
    val colorHex: Long,
    val type: CategoryType,
    val total: Double,
    val transactions: List<TransactionItem>
)

/** Pure calculation helpers (easy to unit test later). */
object TransactionCalculations {

    fun weekStart(date: LocalDate): LocalDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun weekEnd(date: LocalDate): LocalDate = weekStart(date).plusDays(6)

    fun totalIncome(items: List<TransactionItem>): Double =
        items.filter { it.type == CategoryType.INCOME }.sumOf { abs(it.amount) }

    fun totalExpense(items: List<TransactionItem>): Double =
        items.filter { it.type == CategoryType.EXPENSE }.sumOf { abs(it.amount) }

    fun balance(items: List<TransactionItem>): Double = totalIncome(items) - totalExpense(items)

    fun dailySummaries(items: List<TransactionItem>): Map<LocalDate, DailySummary> =
        items.groupBy { it.date }.mapValues { (date, dayItems) ->
            DailySummary(date, totalIncome(dayItems), totalExpense(dayItems))
        }

    fun groupByCategory(items: List<TransactionItem>, type: CategoryType): List<CategoryTransactionGroup> =
        items
            .filter { it.type == type }
            .groupBy { it.categoryName.lowercase() }
            .map { (_, groupItems) ->
                val first = groupItems.first()
                CategoryTransactionGroup(
                    categoryName = first.categoryName,
                    iconName = first.categoryIconName,
                    colorHex = first.categoryColorHex,
                    type = first.type,
                    total = groupItems.sumOf { abs(it.amount) },
                    transactions = groupItems.sortedWith(
                        compareByDescending<TransactionItem> { it.date }.thenByDescending { it.time }
                    )
                )
            }
            .sortedByDescending { it.total }
}

data class TransactionUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val period: SummaryPeriod = SummaryPeriod.MONTH,
    val listType: CategoryType = CategoryType.EXPENSE,
    // Everything loaded from Room: the shown month + the full week of the selected date
    val loadedTransactions: List<TransactionItem> = emptyList()
) {
    /** Transactions of the calendar month (for calendar cells + monthly totals). */
    val monthTransactions: List<TransactionItem>
        get() = loadedTransactions.filter { YearMonth.from(it.date) == selectedMonth }

    val periodStart: LocalDate
        get() = when (period) {
            SummaryPeriod.DAY -> selectedDate
            SummaryPeriod.WEEK -> TransactionCalculations.weekStart(selectedDate)
            SummaryPeriod.MONTH -> selectedMonth.atDay(1)
        }

    val periodEnd: LocalDate
        get() = when (period) {
            SummaryPeriod.DAY -> selectedDate
            SummaryPeriod.WEEK -> TransactionCalculations.weekEnd(selectedDate)
            SummaryPeriod.MONTH -> selectedMonth.atEndOfMonth()
        }

    /** Transactions inside the chosen Daily / Weekly / Monthly period. */
    val periodTransactions: List<TransactionItem>
        get() = loadedTransactions.filter { !it.date.isBefore(periodStart) && !it.date.isAfter(periodEnd) }

    val periodIncome: Double get() = TransactionCalculations.totalIncome(periodTransactions)
    val periodExpense: Double get() = TransactionCalculations.totalExpense(periodTransactions)
    val periodBalance: Double get() = TransactionCalculations.balance(periodTransactions)

    /** Income / expense per day -> shown in each calendar cell. */
    val dailySummaries: Map<LocalDate, DailySummary>
        get() = TransactionCalculations.dailySummaries(monthTransactions)

    /** Bottom list: period transactions grouped by category, biggest total first. */
    val categoryGroups: List<CategoryTransactionGroup>
        get() = TransactionCalculations.groupByCategory(periodTransactions, listType)

    val visibleTotal: Double
        get() = categoryGroups.sumOf { it.total }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _period = MutableStateFlow(SummaryPeriod.MONTH)
    private val _listType = MutableStateFlow(CategoryType.EXPENSE)

    // Query range = whole month + whole week of the selected date (a week can cross months)
    private val loadedTransactions = combine(_selectedMonth, _selectedDate) { month, date ->
        val start = minOf(month.atDay(1), TransactionCalculations.weekStart(date))
        val end = maxOf(month.atEndOfMonth(), TransactionCalculations.weekEnd(date))
        start to end
    }
        .distinctUntilChanged()
        .flatMapLatest { (start, end) ->
            Log.d(TAG, "Querying Room for transactions $start .. $end")
            repository.transactionsBetween(start, end)
        }
        .map { list ->
            Log.d(TAG, "Loaded ${list.size} transactions")
            list
        }

    val uiState: StateFlow<TransactionUiState> = combine(
        _selectedMonth,
        _selectedDate,
        _period,
        _listType,
        loadedTransactions
    ) { month, date, period, listType, transactions ->
        TransactionUiState(
            selectedMonth = month,
            selectedDate = date,
            period = period,
            listType = listType,
            loadedTransactions = transactions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionUiState()
    )

    /** Tapping a calendar day switches the summary to that day. */
    fun selectDate(date: LocalDate) {
        Log.d(TAG, "Date selected: $date")
        _selectedDate.value = date
        _selectedMonth.value = YearMonth.from(date)
        if (_period.value == SummaryPeriod.MONTH) _period.value = SummaryPeriod.DAY
    }

    fun setPeriod(period: SummaryPeriod) {
        Log.d(TAG, "Summary period -> $period")
        _period.value = period
    }

    fun setListType(type: CategoryType) {
        Log.d(TAG, "Category list type -> $type")
        _listType.value = type
    }

    fun previousMonth() = changeMonth(_selectedMonth.value.minusMonths(1))

    fun nextMonth() = changeMonth(_selectedMonth.value.plusMonths(1))

    private fun changeMonth(month: YearMonth) {
        Log.d(TAG, "Month changed -> $month")
        _selectedMonth.value = month
        _selectedDate.value = month.atDay(1)
        _period.value = SummaryPeriod.MONTH
    }

    fun goToToday() {
        val today = LocalDate.now()
        Log.d(TAG, "Jump to today: $today")
        _selectedDate.value = today
        _selectedMonth.value = YearMonth.from(today)
        _period.value = SummaryPeriod.DAY
    }

    companion object {
        private const val TAG = "TransactionViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { TransactionViewModel(transactionRepository()) }
        }
    }
}