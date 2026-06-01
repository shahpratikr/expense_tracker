package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import kotlinx.coroutines.flow.Flow

class GetBudgetsUseCase(private val budgetRepository: IBudgetRepository) {
    operator fun invoke(): Flow<List<Budget>> = budgetRepository.getAll()
}
