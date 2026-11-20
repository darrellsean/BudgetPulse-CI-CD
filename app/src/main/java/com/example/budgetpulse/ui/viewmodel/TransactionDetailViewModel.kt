package com.example.budgetpulse.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.data.repository.TransactionRepository
import com.example.budgetpulse.ui.navigation.BudgetPulseDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.abs

/**
 * Detail / edit screen for ONE transaction.
 * The transaction id is passed between screens as a navigation argument
 * ("transaction/{transactionId}") and read here from SavedStateHandle.
 */
class TransactionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TransactionRepository
) : ViewModel() {

    val transactionId: Int = checkNotNull(savedStateHandle[BudgetPulseDestinations.TRANSACTION_ID_ARG])

    /** null while loading or after the transaction was deleted. */
    val transaction: StateFlow<TransactionItem?> = repository.transactionById(transactionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Becomes true after save/delete so the screen can navigate back. */
    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    init {
        Log.d(TAG, "Opened detail screen for transaction #$transactionId")
    }

    fun saveChanges(original: TransactionItem, amount: Double, description: String, date: LocalDate) {
        if (amount <= 0.0) {
            Log.w(TAG, "Ignored save with invalid amount $amount")
            return
        }
        val signedAmount = if (original.type == CategoryType.EXPENSE) -abs(amount) else abs(amount)
        viewModelScope.launch {
            repository.updateTransaction(
                original.copy(amount = signedAmount, note = description.trim(), date = date)
            )
            _isFinished.value = true
        }
    }

    fun delete(item: TransactionItem) {
        viewModelScope.launch {
            repository.deleteTransaction(item)
            _isFinished.value = true
        }
    }

    companion object {
        private const val TAG = "TransactionDetailVM"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TransactionDetailViewModel(createSavedStateHandle(), transactionRepository())
            }
        }
    }
}