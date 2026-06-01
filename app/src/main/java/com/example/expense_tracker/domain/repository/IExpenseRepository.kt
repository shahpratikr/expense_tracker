package com.example.expense_tracker.domain.repository

import com.example.expense_tracker.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface IExpenseRepository {
    suspend fun add(expense: Expense): Long
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)
    suspend fun getById(id: Long): Expense?
    fun getAll(): Flow<List<Expense>>
    fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
}
