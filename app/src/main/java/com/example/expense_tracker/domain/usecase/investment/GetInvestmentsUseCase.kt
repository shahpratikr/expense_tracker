package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.flow.Flow

// R-4: Use case to observe all investments
class GetInvestmentsUseCase(private val repository: IInvestmentRepository) {

    // R-4: Returns live flow of all investment records
    operator fun invoke(): Flow<List<Investment>> = repository.getAll()
}
