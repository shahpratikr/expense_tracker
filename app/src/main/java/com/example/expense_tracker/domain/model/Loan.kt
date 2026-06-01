package com.example.expense_tracker.domain.model

import java.time.LocalDate

data class Loan(
    val id: Long = 0,
    val name: String,
    val currentBalance: Double,
    val createdAt: LocalDate,
    val interestRate: Double = 0.0,
    val emi: Double = 0.0
)
