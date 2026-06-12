package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth

// R-5: Use case — calculate remaining budget headroom for the current month
// Headroom = sum of all budget limits for current month − total spending in those categories
class GetBudgetHeadroomUseCase(
    private val budgetRepository: IBudgetRepository,
    private val expenseRepository: IExpenseRepository
) {
    // R-5: Returns live flow of total remaining budget headroom (can be negative when overspent)
    operator fun invoke(month: YearMonth = YearMonth.now()): Flow<Double> {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        return combine(
            budgetRepository.getByMonthYear(month),
            expenseRepository.getByDateRange(startDate, endDate)
        ) { budgets, expenses ->
            val totalLimit = budgets.sumOf { it.monthlyLimit }
            // Only count spending in budgeted categories
            val budgetedCategoryIds = budgets.map { it.categoryId }.toSet()
            val totalSpent = expenses
                .filter { it.categoryId != null && it.categoryId in budgetedCategoryIds }
                .sumOf { it.amount }
            totalLimit - totalSpent
        }
    }
}
