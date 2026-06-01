package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import java.time.YearMonth

class AddBudgetUseCase(private val budgetRepository: IBudgetRepository) {
    suspend operator fun invoke(categoryId: Long, monthlyLimit: Double, monthYear: YearMonth): Long {
        require(monthlyLimit > 0) { "Monthly limit must be greater than 0" }
        return budgetRepository.add(Budget(categoryId = categoryId, monthlyLimit = monthlyLimit, monthYear = monthYear))
    }
}
