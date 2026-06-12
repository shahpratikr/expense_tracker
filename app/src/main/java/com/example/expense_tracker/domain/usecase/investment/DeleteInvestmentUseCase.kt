package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository

// R-4: Use case to delete an investment record
class DeleteInvestmentUseCase(private val repository: IInvestmentRepository) {

    // R-4: Deletes the given investment from persistence
    suspend operator fun invoke(investment: Investment) {
        repository.delete(investment)
    }
}
