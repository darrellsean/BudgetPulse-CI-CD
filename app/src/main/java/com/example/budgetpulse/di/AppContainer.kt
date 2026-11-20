package com.example.budgetpulse.di

import android.content.Context
import com.example.budgetpulse.data.local.BudgetDatabase
import com.example.budgetpulse.data.remote.FinancialApiService
import com.example.budgetpulse.data.repository.FinancialInsightsRepository
import com.example.budgetpulse.data.repository.NetworkFinancialInsightsRepository
import com.example.budgetpulse.data.repository.TransactionRepository
import com.example.budgetpulse.data.repository.UserPreferencesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val financialInsightsRepository: FinancialInsightsRepository
    val transactionRepository: TransactionRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://gist.githubusercontent.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService: FinancialApiService by lazy {
        retrofit.create(FinancialApiService::class.java)
    }

    override val financialInsightsRepository: FinancialInsightsRepository by lazy {
        NetworkFinancialInsightsRepository(retrofitService)
    }

    // Room database (local storage)
    private val database: BudgetDatabase by lazy {
        BudgetDatabase.getDatabase(context)
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database.transactionDao(), database.categoryDao())
    }

    // Jetpack DataStore (user preferences)
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}