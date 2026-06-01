package com.example.expense_tracker.domain.model

import java.time.LocalDate

data class Loan(
    val id: Long = 0,
    val name: String,
    val currentBalance: Double,
    val createdAt: LocalDate
)
