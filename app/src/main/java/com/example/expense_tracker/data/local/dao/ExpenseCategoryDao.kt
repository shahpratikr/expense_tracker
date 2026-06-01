package com.example.expense_tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense_tracker.data.model.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {
    @Insert
    suspend fun insert(category: ExpenseCategoryEntity): Long

    @Update
    suspend fun update(category: ExpenseCategoryEntity)

    @Delete
    suspend fun delete(category: ExpenseCategoryEntity)

    @Query("SELECT * FROM expense_categories WHERE id = :id")
    suspend fun getById(id: Long): ExpenseCategoryEntity?

    @Query("SELECT * FROM expense_categories ORDER BY name ASC")
    fun getAllFlow(): Flow<List<ExpenseCategoryEntity>>

    @Query("SELECT * FROM expense_categories WHERE is_predefined = 1 ORDER BY name ASC")
    fun getPredefinedFlow(): Flow<List<ExpenseCategoryEntity>>

    @Query("DELETE FROM expense_categories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
