package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository

class DeleteBudgetUseCase(private val budgetRepository: IBudgetRepository) {
    suspend operator fun invoke(budget: Budget) {
        budgetRepository.delete(budget)
    }
}
