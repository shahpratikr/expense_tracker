package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository

// R-2: Use case — edit an existing budget's monthly limit; validates limit > 0
class EditBudgetUseCase(private val budgetRepository: IBudgetRepository) {
    suspend operator fun invoke(budget: Budget) {
        require(budget.monthlyLimit > 0) { "Monthly limit must be greater than 0" }
        budgetRepository.update(budget)
    }
}
