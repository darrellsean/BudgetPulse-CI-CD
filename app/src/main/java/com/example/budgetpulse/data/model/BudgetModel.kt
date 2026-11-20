package com.example.budgetpulse.data.model

enum class BudgetViewMode {
    PLAN,
    REMAIN
}

data class CategoryBudget(
    val category: CategoryItem,
    val allocatedBudget: Double? = null, // null represents "None"
    val spentAmount: Double = 0.0
) {
    val remainingAmount: Double
        get() = (allocatedBudget ?: 0.0) - spentAmount
}