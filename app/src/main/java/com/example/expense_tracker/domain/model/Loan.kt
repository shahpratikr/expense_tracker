package com.example.expense_tracker.domain.model

import java.time.LocalDate

// PRD Feature 1: Loan domain model — balance auto-recalculates from EMI schedule; feeds prepayment calculator
data class Loan(
    val id: Long = 0,
    val name: String,
    val currentBalance: Double,
    val interestRate: Double,
    val emiAmount: Double,
    val loanStartDate: LocalDate,
    val emiDayOfMonth: Int,
    val lastBalanceUpdateDate: LocalDate,
    val createdAt: LocalDate
)
