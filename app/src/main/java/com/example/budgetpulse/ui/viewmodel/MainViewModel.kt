package com.example.budgetpulse.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.data.model.CategoryItem
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.DefaultCategories
import com.example.budgetpulse.data.model.toCategoryItem
import com.example.budgetpulse.data.repository.TransactionRepository
import com.example.budgetpulse.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.abs

data class UiState(
    val isEditMode: Boolean = false,
    val activeCategories: List<CategoryItem> = emptyList(),
    val availableCategories: List<CategoryItem> = DefaultCategories.presets,
    val showAddCategorySheet: Boolean = false,
    val showCustomCategoryDialog: Boolean = false,
    // Category the user tapped -> shows the "Add Transaction" dialog
    val addingTransactionCategory: CategoryItem? = null,
    val currentMonth: YearMonth = YearMonth.now()
)

class MainViewModel(
    private val repository: TransactionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { repository.ensureDefaultCategories() }
        observeCategoriesAndMonthTotals()
    }

    /**
     * Categories come from Room, and each card shows the total recorded for it
     * in the CURRENT month only.
     */
    private fun observeCategoriesAndMonthTotals() {
        val month = YearMonth.now()
        viewModelScope.launch {
            combine(
                repository.categories,
                repository.transactionsForMonth(month),
                preferencesRepository.userPreferences
            ) { categories, monthTransactions, prefs ->
                categories.map { entity ->
                    val total = monthTransactions
                        .filter { it.type == entity.type && it.categoryName.equals(entity.name, ignoreCase = true) }
                        .sumOf { abs(it.amount) }
                    entity.toCategoryItem(amount = formatMoney(prefs.currency.symbol, total))
                }
            }.collect { active ->
                val presetNames = DefaultCategories.presets.map { it.name.lowercase() to it.type }.toSet()
                val customCategories = active.filter {
                    !it.isDefault && (it.name.lowercase() to it.type) !in presetNames
                }
                _uiState.update {
                    it.copy(
                        activeCategories = active,
                        availableCategories = DefaultCategories.presets + customCategories,
                        currentMonth = month
                    )
                }
            }
        }
    }

    // ---------- Edit mode ----------

    fun toggleEditMode() {
        _uiState.update { it.copy(isEditMode = !it.isEditMode) }
        Log.d(TAG, "Edit mode -> ${_uiState.value.isEditMode}")
    }

    // ---------- Categories (saved in Room) ----------

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch { repository.deleteCategory(categoryId) }
    }

    fun addCategoryToActive(category: CategoryItem) {
        viewModelScope.launch { repository.addCategory(category) }
    }

    fun addCustomCategory(name: String, type: CategoryType, iconName: String, colorHex: Long) {
        viewModelScope.launch {
            repository.addCategory(
                CategoryItem(
                    id = "",
                    name = name.trim(),
                    type = type,
                    iconName = iconName,
                    colorHex = colorHex
                )
            )
        }
        _uiState.update { it.copy(showCustomCategoryDialog = false) }
    }

    fun openAddCategorySheet() {
        _uiState.update { it.copy(showAddCategorySheet = true) }
    }

    fun closeAddCategorySheet() {
        _uiState.update { it.copy(showAddCategorySheet = false) }
    }

    fun openCustomCategoryDialog() {
        _uiState.update { it.copy(showCustomCategoryDialog = true) }
    }

    fun closeCustomCategoryDialog() {
        _uiState.update { it.copy(showCustomCategoryDialog = false) }
    }

    // ---------- Add transaction (today) ----------

    fun startAddingTransaction(category: CategoryItem) {
        Log.d(TAG, "Open Add Transaction dialog for '${category.name}'")
        _uiState.update { it.copy(addingTransactionCategory = category) }
    }

    fun dismissAddingTransaction() {
        _uiState.update { it.copy(addingTransactionCategory = null) }
    }

    /** Saves an expense / income for TODAY under [category]. Description is optional. */
    fun saveTransaction(category: CategoryItem, amount: Double, description: String) {
        if (amount <= 0.0) {
            Log.w(TAG, "Rejected transaction with invalid amount: $amount")
            return
        }
        viewModelScope.launch {
            repository.addTransaction(category, amount, description)
        }
        _uiState.update { it.copy(addingTransactionCategory = null) }
    }

    companion object {
        private const val TAG = "MainViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { MainViewModel(transactionRepository(), userPreferencesRepository()) }
        }
    }
}