package com.example.expense_tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense_tracker.data.model.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: Long): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY month_year DESC")
    fun getAllFlow(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE month_year = :monthYear ORDER BY category_id ASC")
    fun getByMonthYearFlow(monthYear: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId AND month_year = :monthYear LIMIT 1")
    suspend fun getByCategoryAndMonth(categoryId: Long, monthYear: String): BudgetEntity?

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
