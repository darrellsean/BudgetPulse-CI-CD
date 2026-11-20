package com.example.budgetpulse.data.repository

import com.example.budgetpulse.data.remote.FinancialApiService
import com.example.budgetpulse.data.remote.FinancialInsight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FinancialInsightsRepository {
    suspend fun fetchDailyFinancialInsight(): Result<FinancialInsight>
}

class NetworkFinancialInsightsRepository(
    private val apiService: FinancialApiService
) : FinancialInsightsRepository {

    private val gistUrl =
        "https://gist.githubusercontent.com/darrellsean/4549b8d7608802060cba818829c8fa02/raw/36000905d3b37a3d2145ade84ac963966ad8ea64/budgeting_tips.json"

    private var currentIndex = 0

    override suspend fun fetchDailyFinancialInsight(): Result<FinancialInsight> {
        return withContext(Dispatchers.IO) {
            try {
                val tips = apiService.getBudgetingTips(gistUrl)
                if (tips.isNotEmpty()) {
                    val selectedTip = tips[currentIndex % tips.size]
                    currentIndex++
                    Result.success(selectedTip)
                } else {
                    Result.failure(Exception("No budgeting tips returned from server."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}