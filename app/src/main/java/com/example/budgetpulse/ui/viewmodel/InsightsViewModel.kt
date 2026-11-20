package com.example.budgetpulse.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.BudgetPulseApplication
import com.example.budgetpulse.data.remote.FinancialInsight
import com.example.budgetpulse.data.repository.FinancialInsightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InsightsUiState(
    val isLoading: Boolean = false,
    val insight: FinancialInsight? = null,
    val errorMessage: String? = null
)

class InsightsViewModel(
    private val repository: FinancialInsightsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadFinancialTip()
    }

    fun loadFinancialTip() {
        viewModelScope.launch {
            Log.d("BudgetPulseNetwork", "Requesting financial tip from REST API...")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.fetchDailyFinancialInsight()
                .onSuccess { fetchedInsight ->
                    Log.i("BudgetPulseNetwork", "Fetched budgeting tip: ${fetchedInsight.title}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            insight = fetchedInsight,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e("BudgetPulseNetwork", "Network error fetching tip", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Offline mode: Unable to fetch online tips. Tap refresh to retry."
                        )
                    }
                }
        }
    }

    // Manual DI ViewModel Factory
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BudgetPulseApplication)
                val repository = application.container.financialInsightsRepository
                InsightsViewModel(repository = repository)
            }
        }
    }
}