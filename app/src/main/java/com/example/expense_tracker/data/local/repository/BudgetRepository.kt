package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.BudgetDao
import com.example.expense_tracker.data.model.BudgetEntity
import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class BudgetRepository(private val budgetDao: BudgetDao) : IBudgetRepository {
    private val yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    override suspend fun add(budget: Budget): Long {
        return budgetDao.insert(budget.toEntity())
    }

    override suspend fun update(budget: Budget) {
        budgetDao.update(budget.toEntity())
    }

    override suspend fun delete(budget: Budget) {
        budgetDao.delete(budget.toEntity())
    }

    override suspend fun getById(id: Long): Budget? {
        return budgetDao.getById(id)?.toDomain()
    }

    override fun getAll(): Flow<List<Budget>> {
        return budgetDao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getByMonthYear(monthYear: YearMonth): Flow<List<Budget>> {
        val monthYearStr = monthYear.format(yearMonthFormatter)
        return budgetDao.getByMonthYearFlow(monthYearStr).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getByCategoryAndMonth(categoryId: Long, monthYear: YearMonth): Budget? {
        val monthYearStr = monthYear.format(yearMonthFormatter)
        return budgetDao.getByCategoryAndMonth(categoryId, monthYearStr)?.toDomain()
    }

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        category_id = categoryId,
        monthly_limit = monthlyLimit,
        month_year = monthYear.format(yearMonthFormatter)
    )

    private fun BudgetEntity.toDomain() = Budget(
        id = id,
        categoryId = category_id,
        monthlyLimit = monthly_limit,
        monthYear = YearMonth.parse(month_year, yearMonthFormatter)
    )
}
