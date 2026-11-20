package com.example.budgetpulse.data.remote

import com.google.gson.annotations.SerializedName

data class FinancialInsight(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("tip")
    val tipText: String,
    @SerializedName("imageUrl")
    val bannerImageUrl: String
)