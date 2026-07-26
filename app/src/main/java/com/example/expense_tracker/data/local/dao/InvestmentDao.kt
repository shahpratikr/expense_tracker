package com.example.expense_tracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expense_tracker.data.model.InvestmentEntity
import kotlinx.coroutines.flow.Flow

// R-4: DAO for investments table — CRUD + filter by asset class
@Dao
interface InvestmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investment: InvestmentEntity): Long

    @Update
    suspend fun update(investment: InvestmentEntity)

    @Delete
    suspend fun delete(investment: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: Long): InvestmentEntity?

    // R-4: Observe all investments, ordered newest first
    @Query("SELECT * FROM investments ORDER BY date DESC")
    fun getAllFlow(): Flow<List<InvestmentEntity>>
}
