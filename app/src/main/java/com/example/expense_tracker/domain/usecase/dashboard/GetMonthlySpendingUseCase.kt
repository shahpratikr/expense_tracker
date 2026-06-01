package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth

class GetMonthlySpendingUseCase(private val expenseRepository: IExpenseRepository) {
    operator fun invoke(month: YearMonth = YearMonth.now()): Flow<Double> {
        val startDate = month.atDay(1)
        val endDate = month.atEndOfMonth()
        return expenseRepository.getByDateRange(startDate, endDate)
            .map { expenses -> expenses.sumOf { it.amount } }
    }
}
