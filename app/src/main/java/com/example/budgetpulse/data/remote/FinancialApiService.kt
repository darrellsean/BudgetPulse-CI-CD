package com.example.budgetpulse.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

interface FinancialApiService {
    @GET
    suspend fun getBudgetingTips(
        @Url rawGistUrl: String
    ): List<FinancialInsight>
}