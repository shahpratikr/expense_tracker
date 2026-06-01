package com.example.expense_tracker.domain.repository

import com.example.expense_tracker.domain.model.Loan
import kotlinx.coroutines.flow.Flow

interface ILoanRepository {
    suspend fun add(loan: Loan): Long
    suspend fun update(loan: Loan)
    suspend fun delete(loan: Loan)
    suspend fun getById(id: Long): Loan?
    fun getAll(): Flow<List<Loan>>
}
