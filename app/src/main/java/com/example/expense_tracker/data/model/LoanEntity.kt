package com.example.expense_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val current_balance: Double,
    val created_at: String,
    val interest_rate: Double = 0.0,
    val emi: Double = 0.0
)
