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

// PRD Feature 3: Navigation graph — Home, Loans, Investments, Dashboard (expense/budget/category removed)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onDashboardClick = { navController.navigate(NavRoutes.DASHBOARD) },
                onLoanClick = { navController.navigate(NavRoutes.LOAN) },
                onInvestmentClick = { navController.navigate(NavRoutes.INVESTMENT) }
            )
        }
        composable(NavRoutes.LOAN) {
            LoanScreen()
        }
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                onLoanClick = { navController.navigate(NavRoutes.LOAN) },
                onInvestmentClick = { navController.navigate(NavRoutes.INVESTMENT) }
            )
        }
        composable(NavRoutes.INVESTMENT) {
            InvestmentScreen()
        }
    }
}
