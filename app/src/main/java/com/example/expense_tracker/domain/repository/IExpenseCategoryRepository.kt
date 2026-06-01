package com.example.expense_tracker.domain.repository

import com.example.expense_tracker.domain.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

interface IExpenseCategoryRepository {
    suspend fun add(category: ExpenseCategory): Long
    suspend fun update(category: ExpenseCategory)
    suspend fun delete(category: ExpenseCategory)
    suspend fun getById(id: Long): ExpenseCategory?
    fun getAll(): Flow<List<ExpenseCategory>>
    fun getPredefined(): Flow<List<ExpenseCategory>>
}
