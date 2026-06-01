package com.example.expense_tracker.domain.model

data class ExpenseCategory(
    val id: Long = 0,
    val name: String,
    val isPredefined: Boolean = false
)
