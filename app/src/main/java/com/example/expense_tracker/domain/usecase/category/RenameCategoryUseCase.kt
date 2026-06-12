package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository

// R-1: User can rename custom categories
class RenameCategoryUseCase(private val categoryRepository: IExpenseCategoryRepository) {
    suspend operator fun invoke(category: ExpenseCategory, newName: String) {
        require(!category.isPredefined) { "Cannot rename a predefined category" }
        require(newName.isNotBlank()) { "Category name cannot be empty" }
        categoryRepository.update(category.copy(name = newName))
    }
}
