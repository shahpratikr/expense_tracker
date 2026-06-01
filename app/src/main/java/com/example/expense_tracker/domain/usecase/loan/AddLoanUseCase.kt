package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import java.time.LocalDate

class AddLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(name: String, currentBalance: Double): Long {
        require(name.isNotBlank()) { "Loan name must not be empty" }
        require(currentBalance >= 0) { "Loan balance must be >= 0" }
        val loan = Loan(
            name = name.trim(),
            currentBalance = currentBalance,
            createdAt = LocalDate.now()
        )
        return loanRepository.add(loan)
    }
}
