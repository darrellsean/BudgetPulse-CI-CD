package com.example.budgetpulse

import com.example.budgetpulse.data.model.CategoryBudget
import com.example.budgetpulse.data.model.CategoryItem
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.ui.viewmodel.BudgetUiState
import com.example.budgetpulse.ui.viewmodel.TransactionUiState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

class BudgetPulseUnitTest {

    private lateinit var sampleFoodCategory: CategoryItem

    @Before
    fun setUp() {
        // Arrange: Create a fresh category object before each test runs
        sampleFoodCategory = CategoryItem(
            id = "exp_2",
            name = "Food & Drink",
            type = CategoryType.EXPENSE,
            iconName = "fastfood",
            colorHex = 0xFFFFF8E1
        )
    }

    // Test 1: Remaining Budget Calculation (Allocated - Spent)
    @Test
    fun categoryBudget_allocatedAndSpent_returnsRemainingAmount() {
        // Arrange
        val budget = CategoryBudget(
            category = sampleFoodCategory,
            allocatedBudget = 500.0,
            spentAmount = 145.50
        )

        // Act
        val remaining = budget.remainingAmount

        // Assert (0.001 delta avoids Double floating-point rounding drift)
        assertEquals(354.50, remaining, 0.001)
    }

    // Test 2: Monthly Net Balance Calculation (Income - Expense)
    @Test
    fun transactionUiState_incomeAndExpenses_calculatesNetBalance() {
        // Arrange
        val today = LocalDate.now()
        val sampleTransactions = listOf(
            TransactionItem(
                id = 1,
                categoryName = "Salary",
                categoryIconName = "attach_money",
                categoryColorHex = 0xFFFFF8E1,
                amount = 3000.0,
                type = CategoryType.INCOME,
                date = today,
                time = LocalTime.of(9, 0)
            ),
            TransactionItem(
                id = 2,
                categoryName = "Food & Drink",
                categoryIconName = "fastfood",
                categoryColorHex = 0xFFFFF3E0,
                amount = -150.0,
                type = CategoryType.EXPENSE,
                date = today,
                time = LocalTime.of(12, 30)
            ),
            TransactionItem(
                id = 3,
                categoryName = "Transportation",
                categoryIconName = "directions_car",
                categoryColorHex = 0xFFE8F5E9,
                amount = -50.0,
                type = CategoryType.EXPENSE,
                date = today,
                time = LocalTime.of(18, 0)
            )
        )
        val uiState = TransactionUiState(
            selectedMonth = YearMonth.from(today),
            selectedDate = today,
            loadedTransactions = sampleTransactions
        )

        // Act & Assert
        assertEquals(3000.0, uiState.periodIncome, 0.001)
        assertEquals(200.0, uiState.periodExpense, 0.001)
        assertEquals(2800.0, uiState.periodBalance, 0.001)
    }

    // Test 3: Daily Allowance Calculation in a 31-Day Month
    @Test
    fun budgetUiState_thirtyOneDayMonth_calculatesDailyAverage() {
        // Arrange (October has 31 days)
        val uiState = BudgetUiState(
            selectedMonth = YearMonth.of(2026, 10),
            totalBudget = 2500.0
        )

        // Act
        val dailyAvg = uiState.dailyAverage

        // Assert (2500 / 31 = 80.6451...)
        assertEquals(80.645, dailyAvg, 0.001)
    }
}