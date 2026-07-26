package com.example.expense_tracker.data.local.repository

import com.example.expense_tracker.data.local.dao.LoanDao
import com.example.expense_tracker.data.model.LoanEntity
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// PRD Feature 1: Repository implementing entity <-> domain transforms for the loans table
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
        interest_rate = interestRate,
        emi_amount = emiAmount,
        loan_start_date = loanStartDate.format(dateFormatter),
        emi_day_of_month = emiDayOfMonth,
        last_balance_update_date = lastBalanceUpdateDate.format(dateFormatter),
        created_at = createdAt.format(dateFormatter)
    )

    private fun LoanEntity.toDomain() = Loan(
        id = id,
        name = name,
        currentBalance = current_balance,
        interestRate = interest_rate,
        emiAmount = emi_amount,
        loanStartDate = LocalDate.parse(loan_start_date, dateFormatter),
        emiDayOfMonth = emi_day_of_month,
        lastBalanceUpdateDate = LocalDate.parse(last_balance_update_date, dateFormatter),
        createdAt = LocalDate.parse(created_at, dateFormatter)
    )
}
