package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.ExpenseDao
import com.example.expense_tracker.data.model.ExpenseEntity
import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExpenseRepository(private val expenseDao: ExpenseDao) : IExpenseRepository {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun add(expense: Expense): Long {
        return expenseDao.insert(expense.toEntity())
    }

    override suspend fun update(expense: Expense) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun delete(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }

    override suspend fun getById(id: Long): Expense? {
        return expenseDao.getById(id)?.toDomain()
    }

    override fun getAll(): Flow<List<Expense>> {
        return expenseDao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        val startStr = startDate.format(dateFormatter)
        val endStr = endDate.format(dateFormatter)
        return expenseDao.getByDateRangeFlow(startStr, endStr).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun Expense.toEntity() = ExpenseEntity(
        id = id,
        amount = amount,
        category_id = categoryId,
        date = date.format(dateFormatter)
    )

    private fun ExpenseEntity.toDomain() = Expense(
        id = id,
        amount = amount,
        categoryId = category_id,
        date = LocalDate.parse(date, dateFormatter)
    )
}
