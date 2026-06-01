package com.example.expense_tracker.domain.repository

import com.example.expense_tracker.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface IBudgetRepository {
    suspend fun add(budget: Budget): Long
    suspend fun update(budget: Budget)
    suspend fun delete(budget: Budget)
    suspend fun getById(id: Long): Budget?
    fun getAll(): Flow<List<Budget>>
    fun getByMonthYear(monthYear: YearMonth): Flow<List<Budget>>
    suspend fun getByCategoryAndMonth(categoryId: Long, monthYear: YearMonth): Budget?
}
