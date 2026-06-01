package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import java.time.LocalDate

class AddLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(
        name: String,
        currentBalance: Double,
        interestRate: Double = 0.0,
        emi: Double = 0.0
    ): Long {
        require(name.isNotBlank()) { "Loan name must not be empty" }
        require(currentBalance >= 0) { "Loan balance must be >= 0" }
        require(interestRate >= 0) { "Interest rate must be >= 0" }
        require(emi >= 0) { "EMI must be >= 0" }
        val loan = Loan(
            name = name.trim(),
            currentBalance = currentBalance,
            createdAt = LocalDate.now(),
            interestRate = interestRate,
            emi = emi
        )
        return loanRepository.add(loan)
    }
}
