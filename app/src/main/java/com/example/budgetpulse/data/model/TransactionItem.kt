package com.example.budgetpulse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("SpellCheckingInspection") // table name kept as-is so the existing schema does not change
@Entity(tableName = "transactionitem_table")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "category_name")
    val categoryName: String,

    @ColumnInfo(name = "category_icon_name")
    val categoryIconName: String,

    @ColumnInfo(name = "category_color_hex")
    val categoryColorHex: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "type")
    val type: CategoryType,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    @ColumnInfo(name = "time")
    val time: LocalTime,

    @ColumnInfo(name = "note")
    val note: String = ""
) {
    val formattedTime: String
        get() = time.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH))
}

data class DailySummary(
    val date: LocalDate,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
) {
    val netTotal: Double get() = totalIncome - totalExpense
}