package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository

class DeleteLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(loan: Loan) {
        loanRepository.delete(loan)
    }
}
