package com.example.expense_tracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.expense_tracker.presentation.screen.BudgetScreen
import com.example.expense_tracker.presentation.screen.CategoryManagementScreen
import com.example.expense_tracker.presentation.screen.DashboardScreen
import com.example.expense_tracker.presentation.screen.ExpenseDetailScreen
import com.example.expense_tracker.presentation.screen.ExpenseListScreen
import com.example.expense_tracker.presentation.screen.HomeScreen
import com.example.expense_tracker.presentation.screen.InvestmentScreen
import com.example.expense_tracker.presentation.screen.LoanScreen

object NavRoutes {
    const val HOME = "home"
    const val EXPENSE_LIST = "expense_list"
    const val EXPENSE_DETAIL = "expense_detail"
    const val EXPENSE_DETAIL_WITH_ID = "expense_detail/{expenseId}"
    const val BUDGET = "budget"
    const val LOAN = "loan"
    const val DASHBOARD = "dashboard"
    const val CATEGORY_MANAGEMENT = "category_management"
    // R-4: Route for investment tracking screen
    const val INVESTMENT = "investment"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onExpenseListClick = { navController.navigate(NavRoutes.EXPENSE_LIST) },
                onCategoryManagementClick = { navController.navigate(NavRoutes.CATEGORY_MANAGEMENT) },
                onDashboardClick = { navController.navigate(NavRoutes.DASHBOARD) },
                onBudgetClick = { navController.navigate(NavRoutes.BUDGET) },
                onLoanClick = { navController.navigate(NavRoutes.LOAN) },
                // R-4: Navigate to investment tracking screen
                onInvestmentClick = { navController.navigate(NavRoutes.INVESTMENT) }
            )
        }
        composable(NavRoutes.EXPENSE_LIST) {
            ExpenseListScreen(
                onExpenseClick = { expense ->
                    navController.navigate("expense_detail/${expense.id}")
                },
                onAddExpenseClick = { navController.navigate(NavRoutes.EXPENSE_DETAIL) }
            )
        }
        composable(NavRoutes.EXPENSE_DETAIL) {
            ExpenseDetailScreen(
                onBackClick = { navController.popBackStack() },
                expenseId = null
            )
        }
        composable(NavRoutes.EXPENSE_DETAIL_WITH_ID) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toLongOrNull()
            ExpenseDetailScreen(
                onBackClick = { navController.popBackStack() },
                expenseId = expenseId
            )
        }
        composable(NavRoutes.BUDGET) {
            BudgetScreen()
        }
        composable(NavRoutes.LOAN) {
            LoanScreen()
        }
        // R-5: Dashboard screen — all four metrics tappable, each navigates to its detail screen
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                onSpendingClick = { navController.navigate(NavRoutes.EXPENSE_LIST) },
                onLoanClick = { navController.navigate(NavRoutes.LOAN) },
                onInvestmentClick = { navController.navigate(NavRoutes.INVESTMENT) },
                onBudgetClick = { navController.navigate(NavRoutes.BUDGET) }
            )
        }
        composable(NavRoutes.CATEGORY_MANAGEMENT) {
            CategoryManagementScreen()
        }
        // R-4: Investment tracking screen route
        composable(NavRoutes.INVESTMENT) {
            InvestmentScreen()
        }
    }
}
