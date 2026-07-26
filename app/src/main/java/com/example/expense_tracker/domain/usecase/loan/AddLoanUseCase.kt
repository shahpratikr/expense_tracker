package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import java.time.LocalDate

// PRD Feature 1: Add a loan — validates name, balance, interest rate, EMI amount and EMI day of month
class AddLoanUseCase(private val loanRepository: ILoanRepository) {
    suspend operator fun invoke(
        name: String,
        currentBalance: Double,
        interestRate: Double,
        emiAmount: Double,
        loanStartDate: LocalDate,
        emiDayOfMonth: Int
    ): Long {
        require(name.isNotBlank()) { "Loan name must not be empty" }
        require(currentBalance >= 0) { "Loan balance must be >= 0" }
        require(interestRate > 0) { "Interest rate must be > 0" }
        require(emiAmount > 0) { "EMI amount must be > 0" }
        require(emiDayOfMonth in 1..31) { "EMI day of month must be between 1 and 31" }
        val loan = Loan(
            name = name.trim(),
            currentBalance = currentBalance,
            interestRate = interestRate,
            emiAmount = emiAmount,
            loanStartDate = loanStartDate,
            emiDayOfMonth = emiDayOfMonth,
            lastBalanceUpdateDate = loanStartDate,
            createdAt = LocalDate.now()
        )
        return loanRepository.add(loan)
    }
}
