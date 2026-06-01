package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository

class EditLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(loan: Loan) {
        require(loan.name.isNotBlank()) { "Loan name must not be empty" }
        require(loan.currentBalance >= 0) { "Loan balance must be >= 0" }
        loanRepository.update(loan)
    }
}
