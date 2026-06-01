package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.LoanDao
import com.example.expense_tracker.data.model.LoanEntity
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoanRepository(private val loanDao: LoanDao) : ILoanRepository {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun add(loan: Loan): Long {
        return loanDao.insert(loan.toEntity())
    }

    override suspend fun update(loan: Loan) {
        loanDao.update(loan.toEntity())
    }

    override suspend fun delete(loan: Loan) {
        loanDao.delete(loan.toEntity())
    }

    override suspend fun getById(id: Long): Loan? {
        return loanDao.getById(id)?.toDomain()
    }

    override fun getAll(): Flow<List<Loan>> {
        return loanDao.getAllFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun Loan.toEntity() = LoanEntity(
        id = id,
        name = name,
        current_balance = currentBalance,
        created_at = createdAt.format(dateFormatter),
        interest_rate = interestRate,
        emi = emi
    )

    private fun LoanEntity.toDomain() = Loan(
        id = id,
        name = name,
        currentBalance = current_balance,
        createdAt = LocalDate.parse(created_at, dateFormatter),
        interestRate = interest_rate,
        emi = emi
    )
}
