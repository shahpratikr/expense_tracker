package com.example.expense_tracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expense_tracker.presentation.screen.DashboardScreen
import com.example.expense_tracker.presentation.screen.HomeScreen
import com.example.expense_tracker.presentation.screen.InvestmentScreen
import com.example.expense_tracker.presentation.screen.LoanScreen

object NavRoutes {
    const val HOME = "home"
    const val LOAN = "loan"
    const val DASHBOARD = "dashboard"
    const val INVESTMENT = "investment"
}

// Pops any existing instance of the destination (and anything on top of it) back down to HOME before
// pushing a fresh one. Without this, navigating Dashboard <-> Loans repeatedly stacks a new Loans back
// stack entry (and thus a new LoanViewModel) on every click instead of reusing one, which let overlapping
// RecalculateLoanBalancesUseCase invocations race and clobber each other's balance update.
private fun NavHostController.navigateSingleInstance(route: String) {
    navigate(route) {
        popUpTo(NavRoutes.HOME)
        launchSingleTop = true
    }
}

// PRD Feature 3: Navigation graph — Home, Loans, Investments, Dashboard (expense/budget/category removed)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onDashboardClick = { navController.navigateSingleInstance(NavRoutes.DASHBOARD) },
                onLoanClick = { navController.navigateSingleInstance(NavRoutes.LOAN) },
                onInvestmentClick = { navController.navigateSingleInstance(NavRoutes.INVESTMENT) }
            )
        }
        composable(NavRoutes.LOAN) {
            LoanScreen()
        }
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                onLoanClick = { navController.navigateSingleInstance(NavRoutes.LOAN) },
                onInvestmentClick = { navController.navigateSingleInstance(NavRoutes.INVESTMENT) }
            )
        }
        composable(NavRoutes.INVESTMENT) {
            InvestmentScreen()
        }
    }
}
