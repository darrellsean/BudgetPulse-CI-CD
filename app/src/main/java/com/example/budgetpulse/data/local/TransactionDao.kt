package com.example.budgetpulse.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.budgetpulse.data.model.TransactionItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(item: TransactionItem): Long

    @Update
    suspend fun updateTransaction(item: TransactionItem)

    @Delete
    suspend fun deleteTransaction(item: TransactionItem)

    // Used by the Transaction Detail screen (id is passed through navigation)
    @Query("SELECT * FROM transactionitem_table WHERE id = :id")
    fun getTransactionById(id: Int): Flow<TransactionItem?>

    // Dates are stored as ISO text (yyyy-MM-dd), so BETWEEN works correctly on them.
    @Query(
        "SELECT * FROM transactionitem_table " +
                "WHERE date BETWEEN :startDate AND :endDate " +
                "ORDER BY date DESC, time DESC"
    )
    fun getTransactionsBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<TransactionItem>>

    // Used by the Settings page
    @Query("SELECT COUNT(*) FROM transactionitem_table")
    fun getTransactionCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM transactionitem_table ORDER BY date DESC")
    fun getRecordedDates(): Flow<List<LocalDate>>
}