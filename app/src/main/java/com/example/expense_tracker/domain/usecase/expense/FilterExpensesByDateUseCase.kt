package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FilterExpensesByDateUseCase(private val expenseRepository: IExpenseRepository) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        require(startDate <= endDate) { "Start date must be before or equal to end date" }
        return expenseRepository.getByDateRange(startDate, endDate)
    }
}
