package com.example.budgetpulse.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.budgetpulse.ui.components.AppNavigationRail
import com.example.budgetpulse.ui.components.NavTab
import com.example.budgetpulse.ui.components.useNavigationRail
import com.example.budgetpulse.ui.screens.BudgetScreen
import com.example.budgetpulse.ui.screens.HomeScreen
import com.example.budgetpulse.ui.screens.ProfileScreen
import com.example.budgetpulse.ui.screens.SetupScreen
import com.example.budgetpulse.ui.screens.TransactionDetailScreen
import com.example.budgetpulse.ui.screens.TransactionScreen
import com.example.budgetpulse.ui.screens.WelcomeScreen
import com.example.budgetpulse.ui.viewmodel.ProfileViewModel

/** All routes of the app in one place. */
object BudgetPulseDestinations {
    const val SETUP = "setup"
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val BUDGET = "budget"
    const val SETTINGS = "settings"

    // Detail screen receives the selected transaction's id as a navigation argument
    const val TRANSACTION_ID_ARG = "transactionId"
    const val TRANSACTION_DETAIL = "transaction/{$TRANSACTION_ID_ARG}"
    fun transactionDetail(id: Int) = "transaction/$id"

    fun routeFor(tab: NavTab): String = when (tab) {
        NavTab.HOME -> HOME
        NavTab.TRANSACTIONS -> TRANSACTIONS
        NavTab.BUDGET -> BUDGET
        NavTab.SETTINGS -> SETTINGS
    }

    fun tabFor(route: String?): NavTab? = when (route) {
        HOME -> NavTab.HOME
        TRANSACTIONS -> NavTab.TRANSACTIONS
        BUDGET -> NavTab.BUDGET
        SETTINGS -> NavTab.SETTINGS
        else -> null
    }
}

private const val NAV_TAG = "BudgetPulseNavigation"

/** Switch bottom-bar / rail tabs without stacking copies of the same screen. */
fun NavHostController.navigateToTab(tab: NavTab) {
    val route = BudgetPulseDestinations.routeFor(tab)
    navigate(route) {
        popUpTo(BudgetPulseDestinations.WELCOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Root of the UI.
 *  - Start destination depends on the saved onboarding state (DataStore):
 *      not set up -> Setup screen, already set up -> Welcome screen.
 *  - If the state changes later (finish setup / restart setup in Settings) the user is redirected.
 *  - Phone portrait shows the bottom bar inside each screen; wide screens show a NavigationRail here.
 */
@Composable
fun BudgetPulseApp(
    profileViewModel: ProfileViewModel,
    startOnboarded: Boolean
) {
    val navController = rememberNavController()
    val startDestination = remember {
        if (startOnboarded) BudgetPulseDestinations.WELCOME else BudgetPulseDestinations.SETUP
    }
    val isOnboarded by profileViewModel.isOnboarded.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()

    // Log every screen change (debugging requirement)
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, arguments ->
            val id = arguments?.getInt(BudgetPulseDestinations.TRANSACTION_ID_ARG, -1) ?: -1
            Log.i(NAV_TAG, "Navigated to ${destination.route}" + if (id >= 0) " (transactionId=$id)" else "")
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    // Dynamic navigation based on the saved onboarding ("authentication") state
    LaunchedEffect(isOnboarded) {
        val currentRoute = navController.currentDestination?.route
        when (isOnboarded) {
            false -> if (currentRoute != BudgetPulseDestinations.SETUP) {
                Log.i(NAV_TAG, "User not set up -> redirect to Setup")
                navController.navigate(BudgetPulseDestinations.SETUP) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
            true -> if (currentRoute == BudgetPulseDestinations.SETUP) {
                Log.i(NAV_TAG, "Setup finished -> redirect to Welcome")
                navController.navigate(BudgetPulseDestinations.WELCOME) {
                    popUpTo(BudgetPulseDestinations.SETUP) { inclusive = true }
                }
            }
            null -> Unit
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentTab = BudgetPulseDestinations.tabFor(backStackEntry?.destination?.route)
    val showRail = useNavigationRail() && currentTab != null
    val openTab: (NavTab) -> Unit = { navController.navigateToTab(it) }

    Row(modifier = Modifier.fillMaxSize()) {
        if (showRail && currentTab != null) {
            AppNavigationRail(currentTab = currentTab, onTabSelected = openTab)
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            composable(BudgetPulseDestinations.SETUP) {
                SetupScreen(
                    initialNickname = "",
                    initialCurrency = profileState.selectedCurrency,
                    onFinishSetup = { nickname, currency -> profileViewModel.completeSetup(nickname, currency) }
                )
            }

            composable(BudgetPulseDestinations.WELCOME) {
                WelcomeScreen(
                    profileData = profileState,
                    onSelectDestination = openTab
                )
            }

            composable(BudgetPulseDestinations.HOME) {
                HomeScreen(onNavigateTab = openTab)
            }

            composable(BudgetPulseDestinations.TRANSACTIONS) {
                TransactionScreen(
                    onNavigateTab = openTab,
                    onOpenTransaction = { id ->
                        navController.navigate(BudgetPulseDestinations.transactionDetail(id))
                    }
                )
            }

            composable(
                route = BudgetPulseDestinations.TRANSACTION_DETAIL,
                arguments = listOf(navArgument(BudgetPulseDestinations.TRANSACTION_ID_ARG) { type = NavType.IntType })
            ) {
                TransactionDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(BudgetPulseDestinations.BUDGET) {
                BudgetScreen(onNavigateTab = openTab)
            }

            composable(BudgetPulseDestinations.SETTINGS) {
                ProfileScreen(
                    onNavigateTab = openTab,
                    viewModel = profileViewModel,
                    onRestartSetup = { profileViewModel.restartSetup() }
                )
            }
        }
    }
}