package com.example.budgetpulse.data.model

data class CategoryItem(
    val id: String,
    val name: String,
    val type: CategoryType,
    val iconName: String,
    val colorHex: Long,
    val amount: String = "RM0",
    val isDefault: Boolean = false
)
