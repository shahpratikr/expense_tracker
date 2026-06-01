package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository

class CreatePredefinedCategoriesUseCase(private val categoryRepository: IExpenseCategoryRepository) {
    suspend operator fun invoke() {
        val predefined = listOf(
            ExpenseCategory(name = "Food", isPredefined = true),
            ExpenseCategory(name = "Transport", isPredefined = true),
            ExpenseCategory(name = "Entertainment", isPredefined = true),
            ExpenseCategory(name = "Utilities", isPredefined = true),
            ExpenseCategory(name = "Healthcare", isPredefined = true),
            ExpenseCategory(name = "Other", isPredefined = true)
        )
        predefined.forEach { categoryRepository.add(it) }
    }
}
