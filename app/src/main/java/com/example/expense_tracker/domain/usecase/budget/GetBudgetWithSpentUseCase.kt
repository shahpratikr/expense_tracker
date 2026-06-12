package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.first
import java.time.YearMonth

// R-2: Result model — pairs a Budget with the actual amount spent in that month/category
data class BudgetWithSpent(
    val budget: Budget,
    val spent: Double
)

// R-2: Use case — calculate actual spending vs budget limit for a given category and month
class GetBudgetWithSpentUseCase(
    private val budgetRepository: IBudgetRepository,
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(categoryId: Long, monthYear: YearMonth): BudgetWithSpent? {
        val budget = budgetRepository.getByCategoryAndMonth(categoryId, monthYear) ?: return null
        val monthExpenses = expenseRepository
            .getByDateRange(monthYear.atDay(1), monthYear.atEndOfMonth())
            .first()
        val spent = monthExpenses
            .filter { it.categoryId == categoryId }
            .sumOf { it.amount }
        return BudgetWithSpent(budget = budget, spent = spent)
    }
}
