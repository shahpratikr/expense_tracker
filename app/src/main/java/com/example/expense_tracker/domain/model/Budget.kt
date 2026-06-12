package com.example.expense_tracker.domain.model

import java.time.YearMonth

// R-2: Domain model for a monthly budget limit per category
data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val monthlyLimit: Double,
    val monthYear: YearMonth
)
