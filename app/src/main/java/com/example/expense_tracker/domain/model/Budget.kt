package com.example.expense_tracker.domain.model

import java.time.YearMonth

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val monthlyLimit: Double,
    val monthYear: YearMonth
)
