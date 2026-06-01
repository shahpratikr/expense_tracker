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
                onLoanClick = { navController.navigate(NavRoutes.LOAN) }
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
        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                onSpendingClick = { navController.navigate(NavRoutes.EXPENSE_LIST) }
            )
        }
        composable(NavRoutes.CATEGORY_MANAGEMENT) {
            CategoryManagementScreen()
        }
    }
}
