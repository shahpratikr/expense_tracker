package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.repository.ILoanRepository

class UpdateLoanBalanceUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(loanId: Long, newBalance: Double) {
        require(newBalance >= 0) { "Loan balance must be >= 0" }
        val existing = loanRepository.getById(loanId)
            ?: throw IllegalArgumentException("Loan with id $loanId not found")
        loanRepository.update(existing.copy(currentBalance = newBalance))
    }
}
