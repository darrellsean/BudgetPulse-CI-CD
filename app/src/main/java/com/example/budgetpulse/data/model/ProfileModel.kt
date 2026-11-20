package com.example.budgetpulse.data.model

import android.net.Uri

enum class AppCurrency(val symbol: String, val displayName: String) {
    MYR("RM", "Malaysian Ringgit (RM)"),
    USD("$", "US Dollar ($)"),
    EUR("€", "Euro (€)"),
    GBP("£", "British Pound (£)"),
    SGD("S$", "Singapore Dollar (S$)")
}

data class ProfileData(
    val nickname: String = "Darrell",
    val profileImageUri: Uri? = null,
    val currentStreak: Int = 0,
    val totalDays: Int = 0,
    val transactionsCount: Int = 0, // filled from Room (TransactionDao.getTransactionCount)
    val isDarkMode: Boolean = false,
    val fontScale: Float = 1.0f, // 0.85f (Small), 1.0f (Normal), 1.15f (Large)
    val selectedCurrency: AppCurrency = AppCurrency.MYR
)