package com.example.expense_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// PRD Feature 1: Room entity for loans table with EMI auto-update columns
@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val current_balance: Double,
    val interest_rate: Double,
    val emi_amount: Double,
    val loan_start_date: String,
    val emi_day_of_month: Int,
    val last_balance_update_date: String,
    val created_at: String
)
