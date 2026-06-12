package com.example.expense_tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// R-4: Room entity for the investments table
@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val asset_class: String,       // stored as enum name string
    val invested_amount: Double,
    val current_value: Double,
    val date: String               // ISO-8601 date string
)
