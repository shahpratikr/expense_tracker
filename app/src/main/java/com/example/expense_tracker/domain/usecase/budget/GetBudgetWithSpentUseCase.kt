package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.first
import java.time.YearMonth

data class BudgetWithSpent(
    val budget: Budget,
    val spent: Double
)

class GetBudgetWithSpentUseCase(
    private val budgetRepository: IBudgetRepository,
    private val expenseRepository: IExpenseRepository
) {
    suspend operator fun invoke(categoryId: Long, monthYear: YearMonth): BudgetWithSpent? {
        val budget = budgetRepository.getByCategoryAndMonth(categoryId, monthYear) ?: return null
        val allExpenses = expenseRepository.getAll().first()
        val spent = allExpenses
            .filter { it.categoryId == categoryId && it.date.year == monthYear.year && it.date.monthValue == monthYear.monthValue }
            .sumOf { it.amount }
        return BudgetWithSpent(budget = budget, spent = spent)
    }
}
