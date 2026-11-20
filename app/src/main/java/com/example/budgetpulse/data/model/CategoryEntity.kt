package com.example.budgetpulse.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table for the categories the user has on the Home page.
 * Saving them here means added / custom / deleted categories and
 * monthly budget limits survive an app restart.
 */
@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: CategoryType,

    @ColumnInfo(name = "icon_name")
    val iconName: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: Long,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,

    // Monthly budget limit used by the Budget page (null = "None")
    @ColumnInfo(name = "monthly_budget")
    val monthlyBudget: Double? = null,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Long = System.currentTimeMillis()
)

fun CategoryEntity.toCategoryItem(amount: String = "RM0"): CategoryItem = CategoryItem(
    id = id,
    name = name,
    type = type,
    iconName = iconName,
    colorHex = colorHex,
    amount = amount,
    isDefault = isDefault
)

fun CategoryItem.toEntity(sortOrder: Long = System.currentTimeMillis()): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    type = type,
    iconName = iconName,
    colorHex = colorHex,
    isDefault = isDefault,
    sortOrder = sortOrder
)

/** Categories inserted the first time the app runs, plus the presets shown in "Add Category". */
object DefaultCategories {

    val active: List<CategoryItem> = listOf(
        CategoryItem("exp_1", "Housing & Utilities", CategoryType.EXPENSE, "home", 0xFFE3F2FD, "RM0", true),
        CategoryItem("exp_2", "Food & Drink", CategoryType.EXPENSE, "fastfood", 0xFFFFF3E0, "RM0", true),
        CategoryItem("exp_3", "Transportation", CategoryType.EXPENSE, "directions_car", 0xFFE8F5E9, "RM0", true),
        CategoryItem("exp_4", "Shopping", CategoryType.EXPENSE, "shopping_cart", 0xFFF3E5F5, "RM0", true),
        CategoryItem("exp_5", "Other", CategoryType.EXPENSE, "more_horiz", 0xFFE0F2F1, "RM0", true),
        CategoryItem("inc_1", "Salary", CategoryType.INCOME, "attach_money", 0xFFFFF8E1, "RM0", true)
    )

    val presets: List<CategoryItem> = listOf(
        CategoryItem("preset_1", "Entertainment", CategoryType.EXPENSE, "movie", 0xFFEDE7F6),
        CategoryItem("preset_2", "Education", CategoryType.EXPENSE, "school", 0xFFE8EAF6),
        CategoryItem("preset_3", "Healthcare", CategoryType.EXPENSE, "medical_services", 0xFFFFEBEE),
        CategoryItem("preset_4", "Investment", CategoryType.EXPENSE, "trending_up", 0xFFE0F7FA),
        CategoryItem("preset_5", "Travel", CategoryType.EXPENSE, "flight", 0xFFE1F5FE),
        CategoryItem("preset_6", "Pets", CategoryType.EXPENSE, "pets", 0xFFFBE9E7),
        CategoryItem("preset_7", "Fitness", CategoryType.EXPENSE, "fitness_center", 0xFFF3E5F5),
        CategoryItem("preset_8", "Subscriptions", CategoryType.EXPENSE, "subscriptions", 0xFFE8EAF6),
        CategoryItem("preset_9", "Beauty & Care", CategoryType.EXPENSE, "spa", 0xFFFCE4EC),
        CategoryItem("preset_10", "Gifts & Donation", CategoryType.EXPENSE, "card_giftcard", 0xFFFFF3E0),

        CategoryItem("preset_inc_1", "Bonus", CategoryType.INCOME, "card_giftcard", 0xFFFFF8E1),
        CategoryItem("preset_inc_2", "Investment Returns", CategoryType.INCOME, "show_chart", 0xFFE8F5E9),
        CategoryItem("preset_inc_3", "Freelance", CategoryType.INCOME, "work", 0xFFE0F2F1),
        CategoryItem("preset_inc_4", "Rental Income", CategoryType.INCOME, "real_estate_agent", 0xFFE3F2FD),
        CategoryItem("preset_inc_5", "Side Business", CategoryType.INCOME, "store", 0xFFEDE7F6),
        CategoryItem("preset_inc_6", "Gift Income", CategoryType.INCOME, "redeem", 0xFFFFF3E0)
    )
}