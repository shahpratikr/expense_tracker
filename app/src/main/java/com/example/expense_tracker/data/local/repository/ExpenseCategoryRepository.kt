package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.ExpenseCategoryDao
import com.example.expense_tracker.data.model.ExpenseCategoryEntity
import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseCategoryRepository(private val categoryDao: ExpenseCategoryDao) :
    IExpenseCategoryRepository {

    override suspend fun add(category: ExpenseCategory): Long {
        return categoryDao.insert(category.toEntity())
    }

    override suspend fun update(category: ExpenseCategory) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun delete(category: ExpenseCategory) {
        categoryDao.delete(category.toEntity())
    }

    override suspend fun getById(id: Long): ExpenseCategory? {
        return categoryDao.getById(id)?.toDomain()
    }

    override fun getAll(): Flow<List<ExpenseCategory>> {
        return categoryDao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPredefined(): Flow<List<ExpenseCategory>> {
        return categoryDao.getPredefinedFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun ExpenseCategory.toEntity() = ExpenseCategoryEntity(
        id = id,
        name = name,
        is_predefined = if (isPredefined) 1 else 0
    )

    private fun ExpenseCategoryEntity.toDomain() = ExpenseCategory(
        id = id,
        name = name,
        isPredefined = is_predefined == 1
    )
}
