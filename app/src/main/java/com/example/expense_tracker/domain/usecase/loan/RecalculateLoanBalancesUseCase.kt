package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

// PRD Feature 1: Auto-recalculates each loan's balance for every elapsed EMI date since the last update
class RecalculateLoanBalancesUseCase(private val loanRepository: ILoanRepository) {
    // Serializes invocations so overlapping calls (e.g. rapid nav between screens that each trigger a
    // recalculation) can't read the same stale loan snapshot and clobber each other's balance update.
    private val mutex = Mutex()

    // PRD Feature 1: Runs on Loans screen load; returns warnings for loans whose EMI can't cover interest
    suspend operator fun invoke(today: LocalDate = LocalDate.now()): List<String> = mutex.withLock {
        val loans = loanRepository.getAll().first()
        loans.mapNotNull { loan -> recalculate(loan, today) }
    }

    private suspend fun recalculate(loan: Loan, today: LocalDate): String? {
        val emiDates = elapsedEmiDates(loan.lastBalanceUpdateDate, today, loan.emiDayOfMonth)
        if (emiDates.isEmpty()) return null

        val monthlyRate = loan.interestRate / 12.0 / 100.0
        var balance = loan.currentBalance
        var lastApplied = loan.lastBalanceUpdateDate
        var negativeAmortization = false

        for (emiDate in emiDates) {
            val interest = balance * monthlyRate
            // R: negative amortization guard — stop advancing rather than growing the balance
            if (loan.emiAmount <= interest) {
                negativeAmortization = true
                break
            }
            val principalComponent = loan.emiAmount - interest
            balance = (balance - principalComponent).coerceAtLeast(0.0)
            lastApplied = emiDate
        }

        if (balance != loan.currentBalance || lastApplied != loan.lastBalanceUpdateDate) {
            loanRepository.update(loan.copy(currentBalance = balance, lastBalanceUpdateDate = lastApplied))
        }

        return if (negativeAmortization) {
            "${loan.name}: EMI amount is too low to cover accrued interest — balance was not advanced for the missed cycle(s)."
        } else {
            null
        }
    }

    // Computes each EMI-day date strictly after `from` and on/before `today`.
    private fun elapsedEmiDates(from: LocalDate, today: LocalDate, dayOfMonth: Int): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var cursor = from.withDayOfMonth(minOf(dayOfMonth, from.lengthOfMonth()))
        if (!cursor.isAfter(from)) {
            cursor = cursor.plusMonths(1)
        }
        while (!cursor.isAfter(today)) {
            val emiDate = cursor.withDayOfMonth(minOf(dayOfMonth, cursor.lengthOfMonth()))
            if (emiDate.isAfter(from) && !emiDate.isAfter(today)) {
                dates += emiDate
            }
            cursor = cursor.plusMonths(1)
        }
        return dates
    }
}
