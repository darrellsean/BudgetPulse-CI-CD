package com.example.budgetpulse

import android.app.Application
import com.example.budgetpulse.di.AppContainer
import com.example.budgetpulse.di.DefaultAppContainer

class BudgetPulseApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}