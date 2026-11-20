package com.example.budgetpulse.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIcons {
    val iconMap: Map<String, ImageVector> = mapOf(
        "home" to Icons.Default.Home,
        "fastfood" to Icons.Default.Fastfood,
        "directions_car" to Icons.Default.DirectionsCar,
        "shopping_cart" to Icons.Default.ShoppingCart,
        "more_horiz" to Icons.Default.MoreHoriz,
        "attach_money" to Icons.Default.AttachMoney,
        "movie" to Icons.Default.Movie,
        "school" to Icons.Default.School,
        "medical_services" to Icons.Default.MedicalServices,
        "trending_up" to Icons.AutoMirrored.Filled.TrendingUp,
        "flight" to Icons.Default.Flight,
        "pets" to Icons.Default.Pets,
        "fitness_center" to Icons.Default.FitnessCenter,
        "subscriptions" to Icons.Default.Subscriptions,
        "spa" to Icons.Default.Spa,
        "card_giftcard" to Icons.Default.CardGiftcard,
        "show_chart" to Icons.AutoMirrored.Filled.ShowChart,
        "work" to Icons.Default.Work,
        "real_estate_agent" to Icons.Default.RealEstateAgent,
        "store" to Icons.Default.Store,
        "redeem" to Icons.Default.Redeem,
        "star" to Icons.Default.Star,
        "account_balance" to Icons.Default.AccountBalance,
        "phone_android" to Icons.Default.PhoneAndroid,
        "build" to Icons.Default.Build,
        "shopping_bag" to Icons.Default.ShoppingBag
    )

    fun getIcon(iconName: String): ImageVector {
        return iconMap[iconName.lowercase()] ?: Icons.Default.Category
    }
}
