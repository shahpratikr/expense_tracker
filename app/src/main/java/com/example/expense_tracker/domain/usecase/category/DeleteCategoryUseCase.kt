package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository

// R-1: User can delete custom categories
class DeleteCategoryUseCase(private val categoryRepository: IExpenseCategoryRepository) {
    suspend operator fun invoke(category: ExpenseCategory) {
        require(!category.isPredefined) { "Cannot delete a predefined category" }
        categoryRepository.delete(category)
    }
}
