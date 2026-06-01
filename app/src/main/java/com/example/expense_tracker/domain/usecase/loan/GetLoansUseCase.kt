package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.Flow

class GetLoansUseCase(private val loanRepository: ILoanRepository) {
    operator fun invoke(): Flow<List<Loan>> = loanRepository.getAll()
}
