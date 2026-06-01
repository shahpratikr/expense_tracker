package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(private val expenseRepository: IExpenseRepository) {
    operator fun invoke(): Flow<List<Expense>> {
        return expenseRepository.getAll()
    }
}
