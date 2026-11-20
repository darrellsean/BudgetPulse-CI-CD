package com.example.budgetpulse.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.data.model.BudgetViewMode
import com.example.budgetpulse.data.model.CategoryBudget
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.toCategoryItem
import com.example.budgetpulse.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.abs

data class BudgetUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    // 0.0 = user has not set a budget for any category yet
    val totalBudget: Double = 0.0,
    val viewMode: BudgetViewMode = BudgetViewMode.PLAN,
    val categoryBudgets: List<CategoryBudget> = emptyList(),
    val editingCategoryBudget: CategoryBudget? = null
) {
    val totalSpent: Double
        get() = categoryBudgets.sumOf { it.spentAmount }

    val daysInMonth: Int
        get() = selectedMonth.lengthOfMonth()

    val dailyAverage: Double
        get() = if (daysInMonth > 0) totalBudget / daysInMonth else 0.0

    val progressFraction: Float
        get() = if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
}

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    private val _viewMode = MutableStateFlow(BudgetViewMode.PLAN)
    private val _editingCategoryBudget = MutableStateFlow<CategoryBudget?>(null)

    private val monthTransactions = _selectedMonth.flatMapLatest { month ->
        repository.transactionsForMonth(month)
    }

    init {
        viewModelScope.launch { repository.ensureDefaultCategories() }
    }

    val uiState: StateFlow<BudgetUiState> = combine(
        _selectedMonth,
        _viewMode,
        _editingCategoryBudget,
        repository.categoriesByType(CategoryType.EXPENSE), // same expense cards as Home page
        monthTransactions
    ) { month, mode, editing, expenseCategories, transactions ->
        val expenses = transactions.filter { it.type == CategoryType.EXPENSE }

        val categoryBudgetsList = expenseCategories.map { entity ->
            val spent = expenses
                .filter { it.categoryName.equals(entity.name, ignoreCase = true) }
                .sumOf { abs(it.amount) }
            CategoryBudget(
                category = entity.toCategoryItem(),
                allocatedBudget = entity.monthlyBudget,
                spentAmount = spent
            )
        }

        // Total budget = sum of the limits the user set (0 when nothing is set yet)
        val allocatedSum = categoryBudgetsList.sumOf { it.allocatedBudget ?: 0.0 }

        BudgetUiState(
            selectedMonth = month,
            totalBudget = allocatedSum,
            viewMode = mode,
            categoryBudgets = categoryBudgetsList,
            editingCategoryBudget = editing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
        Log.d(TAG, "Budget month -> ${_selectedMonth.value}")
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
        Log.d(TAG, "Budget month -> ${_selectedMonth.value}")
    }

    fun setViewMode(mode: BudgetViewMode) {
        _viewMode.value = mode
        Log.d(TAG, "Budget view mode -> $mode")
    }

    fun startEditingBudget(item: CategoryBudget) {
        _editingCategoryBudget.value = item
    }

    fun dismissEditingBudget() {
        _editingCategoryBudget.value = null
    }

    /** Saved to Room, so limits are kept after restarting the app. */
    fun updateAllocatedBudget(categoryId: String, newBudget: Double?) {
        Log.d(TAG, "Set budget for $categoryId -> $newBudget")
        viewModelScope.launch { repository.updateCategoryBudget(categoryId, newBudget) }
        _editingCategoryBudget.value = null
    }

    companion object {
        private const val TAG = "BudgetViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { BudgetViewModel(transactionRepository()) }
        }
    }
}