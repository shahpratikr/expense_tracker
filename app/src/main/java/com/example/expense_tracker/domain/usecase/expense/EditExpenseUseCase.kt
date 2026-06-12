package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import java.time.LocalDate

// R-1: Edit an existing expense
class EditExpenseUseCase(private val expenseRepository: IExpenseRepository) {
    suspend operator fun invoke(id: Long, amount: Double, categoryId: Long?, date: LocalDate) {
        require(amount > 0) { "Amount must be greater than 0" }
        require(date <= LocalDate.now()) { "Date cannot be in the future" }
        expenseRepository.update(Expense(id = id, amount = amount, categoryId = categoryId, date = date))
    }
}
