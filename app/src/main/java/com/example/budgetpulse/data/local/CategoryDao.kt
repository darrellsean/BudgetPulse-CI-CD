package com.example.budgetpulse.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.budgetpulse.data.model.CategoryEntity
import com.example.budgetpulse.data.model.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category_table ORDER BY sort_order ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category_table WHERE type = :type ORDER BY sort_order ASC")
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM category_table")
    suspend fun getCategoryCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM category_table WHERE name = :name COLLATE NOCASE AND type = :type)")
    suspend fun categoryExists(name: String, type: CategoryType): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM category_table WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: String)

    @Query("UPDATE category_table SET monthly_budget = :budget WHERE id = :categoryId")
    suspend fun updateMonthlyBudget(categoryId: String, budget: Double?)
}