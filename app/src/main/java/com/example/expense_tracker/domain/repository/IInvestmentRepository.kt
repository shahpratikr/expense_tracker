package com.example.expense_tracker.domain.repository

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import kotlinx.coroutines.flow.Flow

// R-4: Repository interface for investment data access — I-prefix as per CLAUDE.md convention
interface IInvestmentRepository {
    // R-4: Add a new investment record
    suspend fun add(investment: Investment): Long

    // R-4: Update an existing investment record
    suspend fun update(investment: Investment)

    // R-4: Delete an investment record
    suspend fun delete(investment: Investment)

    // R-4: Get a single investment by id
    suspend fun getById(id: Long): Investment?

    // R-4: Observe all investments as a live Flow
    fun getAll(): Flow<List<Investment>>

    // R-4: Observe investments filtered by asset class
    fun getByAssetClass(assetClass: AssetClass): Flow<List<Investment>>
}
