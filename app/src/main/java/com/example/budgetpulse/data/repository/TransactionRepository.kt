package com.example.budgetpulse.data.repository

import android.util.Log
import com.example.budgetpulse.data.local.CategoryDao
import com.example.budgetpulse.data.local.TransactionDao
import com.example.budgetpulse.data.model.CategoryEntity
import com.example.budgetpulse.data.model.CategoryItem
import com.example.budgetpulse.data.model.CategoryType
import com.example.budgetpulse.data.model.DefaultCategories
import com.example.budgetpulse.data.model.TransactionItem
import com.example.budgetpulse.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID
import kotlin.math.abs

/**
 * Single place the ViewModels talk to for anything stored in Room.
 */
class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {

    // ---------- Transactions ----------

    val transactionCount: Flow<Int> = transactionDao.getTransactionCount()

    val recordedDates: Flow<List<LocalDate>> = transactionDao.getRecordedDates()

    fun transactionsForMonth(month: YearMonth): Flow<List<TransactionItem>> =
        transactionDao.getTransactionsBetween(month.atDay(1), month.atEndOfMonth())

    fun transactionsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<TransactionItem>> =
        transactionDao.getTransactionsBetween(startDate, endDate)

    fun transactionById(id: Int): Flow<TransactionItem?> = transactionDao.getTransactionById(id)

    /**
     * Saves a transaction for [category]. Expenses are stored as negative numbers,
     * income as positive, so the existing formatting code keeps working.
     */
    suspend fun addTransaction(
        category: CategoryItem,
        amount: Double,
        description: String,
        date: LocalDate = LocalDate.now(),
        time: LocalTime = LocalTime.now().withNano(0)
    ) {
        val signedAmount = if (category.type == CategoryType.EXPENSE) -abs(amount) else abs(amount)
        val newId = transactionDao.insertTransaction(
            TransactionItem(
                categoryName = category.name,
                categoryIconName = category.iconName,
                categoryColorHex = category.colorHex,
                amount = signedAmount,
                type = category.type,
                date = date,
                time = time,
                note = description.trim()
            )
        )
        Log.i(TAG, "Inserted transaction #$newId: ${category.type} ${category.name} amount=$signedAmount on $date")
    }

    suspend fun updateTransaction(item: TransactionItem) {
        transactionDao.updateTransaction(item)
        Log.i(TAG, "Updated transaction #${item.id}: amount=${item.amount}, date=${item.date}, note='${item.note}'")
    }

    suspend fun deleteTransaction(item: TransactionItem) {
        transactionDao.deleteTransaction(item)
        Log.i(TAG, "Deleted transaction #${item.id} (${item.categoryName})")
    }

    // ---------- Categories ----------

    val categories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun categoriesByType(type: CategoryType): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    /** Inserts the 5 expense + 1 income default cards the very first time the app runs. */
    suspend fun ensureDefaultCategories() {
        if (categoryDao.getCategoryCount() == 0) {
            Log.i(TAG, "First run: inserting ${DefaultCategories.active.size} default categories")
            categoryDao.insertAll(
                DefaultCategories.active.mapIndexed { index, item -> item.toEntity(sortOrder = index.toLong()) }
            )
        }
    }

    /** Returns false if a category with the same name and type already exists. */
    suspend fun addCategory(category: CategoryItem): Boolean {
        if (categoryDao.categoryExists(category.name, category.type)) {
            Log.w(TAG, "Category '${category.name}' (${category.type}) already exists, skipped")
            return false
        }
        categoryDao.insertCategory(
            category.copy(id = UUID.randomUUID().toString(), isDefault = false).toEntity()
        )
        Log.i(TAG, "Inserted category '${category.name}' (${category.type})")
        return true
    }

    suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteCategoryById(categoryId)
        Log.i(TAG, "Deleted category $categoryId")
    }

    suspend fun updateCategoryBudget(categoryId: String, budget: Double?) {
        categoryDao.updateMonthlyBudget(categoryId, budget)
        Log.i(TAG, "Updated monthly budget of category $categoryId to $budget")
    }

    companion object {
        private const val TAG = "BudgetPulseDatabase"
    }
}