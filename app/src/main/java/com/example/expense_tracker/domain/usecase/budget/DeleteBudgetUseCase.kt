package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository

// R-2: Use case — delete a budget entry
class DeleteBudgetUseCase(private val budgetRepository: IBudgetRepository) {
    suspend operator fun invoke(budget: Budget) {
        budgetRepository.delete(budget)
    }
}
