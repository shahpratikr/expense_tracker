package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(private val categoryRepository: IExpenseCategoryRepository) {
    operator fun invoke(): Flow<List<ExpenseCategory>> {
        return categoryRepository.getAll()
    }
}
