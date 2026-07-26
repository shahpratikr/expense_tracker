package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository

// PRD Feature 1: Edit a loan — re-validates all business rules before persisting
class EditLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(loan: Loan) {
        require(loan.name.isNotBlank()) { "Loan name must not be empty" }
        require(loan.currentBalance >= 0) { "Loan balance must be >= 0" }
        require(loan.interestRate > 0) { "Interest rate must be > 0" }
        require(loan.emiAmount > 0) { "EMI amount must be > 0" }
        require(loan.emiDayOfMonth in 1..31) { "EMI day of month must be between 1 and 31" }
        loanRepository.update(loan)
    }
}
