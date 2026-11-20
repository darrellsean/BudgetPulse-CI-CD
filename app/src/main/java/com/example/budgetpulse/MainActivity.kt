package com.example.budgetpulse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.budgetpulse.ui.components.LocalWindowWidthClass
import com.example.budgetpulse.ui.components.windowWidthClassFor
import com.example.budgetpulse.ui.navigation.BudgetPulseApp
import com.example.budgetpulse.ui.theme.BudgetPulseTheme
import com.example.budgetpulse.ui.theme.LocalAppCurrency
import com.example.budgetpulse.ui.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {

    // Shared by Setup, Welcome and Settings. Reads/writes DataStore + Room stats.
    private val profileViewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "onCreate")

        setContent {
            val profileState by profileViewModel.profileState.collectAsState()
            val isOnboarded by profileViewModel.isOnboarded.collectAsState()

            // Font scale chosen in Settings (saved in DataStore) applied to all pages
            val currentDensity = LocalDensity.current
            val scaledDensity = Density(
                density = currentDensity.density,
                fontScale = profileState.fontScale
            )

            // Adaptive layout: phone vs tablet / landscape
            val widthClass = windowWidthClassFor(LocalConfiguration.current.screenWidthDp)

            CompositionLocalProvider(
                LocalDensity provides scaledDensity,
                LocalAppCurrency provides profileState.selectedCurrency,
                LocalWindowWidthClass provides widthClass
            ) {
                BudgetPulseTheme(darkTheme = profileState.isDarkMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val onboarded = isOnboarded
                        if (onboarded == null) {
                            // DataStore still loading -> decide the start screen once it is ready
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            BudgetPulseApp(
                                profileViewModel = profileViewModel,
                                startOnboarded = onboarded
                            )
                        }
                    }
                }
            }
        }
    }
}