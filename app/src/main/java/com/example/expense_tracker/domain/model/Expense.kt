package com.example.expense_tracker.domain.model

import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long? = null,
    val date: LocalDate
)
