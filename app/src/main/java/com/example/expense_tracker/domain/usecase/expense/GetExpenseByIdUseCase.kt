package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository

class GetExpenseByIdUseCase(private val expenseRepository: IExpenseRepository) {
    suspend operator fun invoke(id: Long): Expense? {
        return expenseRepository.getById(id)
    }
}
