package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// R-5: Use case — sum all active loan balances for the dashboard summary
class GetTotalLoanBalanceUseCase(private val loanRepository: ILoanRepository) {
    // R-5: Returns live flow of the total outstanding loan balance across all loans
    operator fun invoke(): Flow<Double> =
        loanRepository.getAll().map { loans -> loans.sumOf { it.currentBalance } }
}
