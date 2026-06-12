package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import java.time.LocalDate

// R-1: Add a new expense with amount, optional category, and date
class AddExpenseUseCase(private val expenseRepository: IExpenseRepository) {
    suspend operator fun invoke(amount: Double, categoryId: Long?, date: LocalDate): Long {
        require(amount > 0) { "Amount must be greater than 0" }
        require(date <= LocalDate.now()) { "Date cannot be in the future" }
        return expenseRepository.add(Expense(amount = amount, categoryId = categoryId, date = date))
    }
}
