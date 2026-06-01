package com.example.expense_tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expense_tracker.data.model.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Insert
    suspend fun insert(loan: LoanEntity): Long

    @Update
    suspend fun update(loan: LoanEntity)

    @Delete
    suspend fun delete(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getById(id: Long): LoanEntity?

    @Query("SELECT * FROM loans ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<LoanEntity>>

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteById(id: Long)
}
