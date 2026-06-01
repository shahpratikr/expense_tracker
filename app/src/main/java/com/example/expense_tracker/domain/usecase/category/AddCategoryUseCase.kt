package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository

class AddCategoryUseCase(private val categoryRepository: IExpenseCategoryRepository) {
    suspend operator fun invoke(name: String): Long {
        require(name.isNotBlank()) { "Category name cannot be empty" }
        return categoryRepository.add(ExpenseCategory(name = name, isPredefined = false))
    }
}
