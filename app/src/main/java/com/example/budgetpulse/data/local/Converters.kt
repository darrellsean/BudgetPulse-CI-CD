package com.example.budgetpulse.data.local

import androidx.room.TypeConverter
import com.example.budgetpulse.data.model.CategoryType
import java.time.LocalDate
import java.time.LocalTime

// Functions are called by Room-generated code (registered with @TypeConverters in BudgetDatabase)
@Suppress("unused")
class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromCategoryType(value: CategoryType?): String? = value?.name

    @TypeConverter
    fun toCategoryType(value: String?): CategoryType? = value?.let { CategoryType.valueOf(it) }
}