package com.example.budgetpulse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetpulse.data.model.ProfileData
import com.example.budgetpulse.data.remote.FinancialInsight
import com.example.budgetpulse.ui.components.FinancialInsightCard
import com.example.budgetpulse.ui.screens.ProfileScreen
import com.example.budgetpulse.ui.screens.WelcomeScreen
import com.example.budgetpulse.ui.theme.BudgetPulseTheme
import com.example.budgetpulse.ui.viewmodel.InsightsUiState
import com.example.budgetpulse.ui.viewmodel.ProfileViewModel
import org.junit.Rule
import org.junit.Test

class BudgetPulseUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // UI Test 1: Welcome Screen Greeting & Navigation Options
    @Test
    fun welcomeScreen_displaysGreetingAndNavigationCards() {
        // Arrange
        composeTestRule.setContent {
            BudgetPulseTheme {
                WelcomeScreen(
                    profileData = ProfileData(nickname = "Darrell"),
                    onSelectDestination = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Hello, Darrell!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Budget Analytics").assertIsDisplayed()
    }

    // UI Test 2: Network Budgeting Tip Card Rendering
    @Test
    fun financialInsightCard_displaysFetchedTipAndRefreshButton() {
        // Arrange
        val mockInsightState = InsightsUiState(
            isLoading = false,
            insight = FinancialInsight(
                id = 1,
                title = "The 50/30/20 Budget Rule",
                tipText = "Allocate 50% to needs, 30% to wants, and 20% to savings.",
                bannerImageUrl = ""
            )
        )

        composeTestRule.setContent {
            BudgetPulseTheme {
                FinancialInsightCard(
                    uiState = mockInsightState,
                    onRefreshClick = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("The 50/30/20 Budget Rule").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "\"Allocate 50% to needs, 30% to wants, and 20% to savings.\""
        ).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh Tip").assertIsDisplayed()
    }

    // UI Test 3: Profile Screen Stats & Navigation Options
    @Test
    fun profileScreen_displaysStatsAndNavigationOptions() {
        // Arrange
        composeTestRule.setContent {
            BudgetPulseTheme {
                val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
                ProfileScreen(
                    onNavigateTab = {},
                    viewModel = profileViewModel
                )
            }
        }

        // Assert (all visible above the fold without scrolling)
        composeTestRule.onNodeWithText("Current Streak").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pages Navigation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transactions Log").assertIsDisplayed()
    }
}