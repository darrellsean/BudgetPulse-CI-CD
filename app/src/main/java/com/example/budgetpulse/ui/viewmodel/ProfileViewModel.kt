package com.example.budgetpulse.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetpulse.data.model.AppCurrency
import com.example.budgetpulse.data.model.ProfileData
import com.example.budgetpulse.data.repository.TransactionRepository
import com.example.budgetpulse.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    transactionRepository: TransactionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    /**
     * null  = DataStore not loaded yet (show nothing / splash)
     * false = first launch -> Setup screen
     * true  = already set up -> Welcome screen
     * Used by the NavHost for dynamic navigation.
     */
    val isOnboarded: StateFlow<Boolean?> = preferencesRepository.userPreferences
        .map { it.onboardingComplete }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * Profile = saved preferences (DataStore) + live stats (Room):
     *  - transactionsCount: number of transactions the user has recorded
     *  - totalDays: number of different days with at least one record
     *  - currentStreak: consecutive days with a record, up to today (or yesterday)
     */
    val profileState: StateFlow<ProfileData> = combine(
        preferencesRepository.userPreferences,
        transactionRepository.transactionCount,
        transactionRepository.recordedDates
    ) { prefs, count, dates ->
        ProfileData(
            nickname = prefs.nickname.ifBlank { "User" },
            profileImageUri = prefs.profileImageUri?.let { Uri.parse(it) },
            currentStreak = calculateStreak(dates),
            totalDays = dates.size,
            transactionsCount = count,
            isDarkMode = prefs.isDarkMode,
            fontScale = prefs.fontScale,
            selectedCurrency = prefs.currency
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileData()
    )

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val dateSet = dates.toSet()
        val today = LocalDate.now()
        var day = when {
            today in dateSet -> today
            today.minusDays(1) in dateSet -> today.minusDays(1)
            else -> return 0
        }
        var streak = 0
        while (day in dateSet) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    // ---------- Setup (first launch) ----------

    fun completeSetup(nickname: String, currency: AppCurrency) {
        Log.d(TAG, "completeSetup(nickname=$nickname, currency=$currency)")
        viewModelScope.launch { preferencesRepository.completeOnboarding(nickname, currency) }
    }

    fun restartSetup() {
        Log.d(TAG, "restartSetup()")
        viewModelScope.launch { preferencesRepository.resetOnboarding() }
    }

    // ---------- Settings (all saved to DataStore) ----------

    fun updateNickname(newName: String) {
        if (newName.isNotBlank()) {
            viewModelScope.launch { preferencesRepository.updateNickname(newName) }
        }
    }

    fun updateProfileImage(uri: Uri?) {
        viewModelScope.launch { preferencesRepository.updateProfileImage(uri) }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { preferencesRepository.updateDarkMode(isDark) }
    }

    fun updateFontScale(scale: Float) {
        viewModelScope.launch { preferencesRepository.updateFontScale(scale) }
    }

    fun updateCurrency(currency: AppCurrency) {
        viewModelScope.launch { preferencesRepository.updateCurrency(currency) }
    }

    companion object {
        private const val TAG = "ProfileViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel(transactionRepository(), userPreferencesRepository())
            }
        }
    }
}