package com.example.expense_tracker.domain.model

import java.time.LocalDate

// R-4: Domain model for investment tracking — no Room/Android imports
data class Investment(
    val id: Long = 0,
    val name: String,
    val assetClass: AssetClass,
    val investedAmount: Double,
    val currentValue: Double,
    val date: LocalDate
) {
    // R-4: Calculated gain/loss in ₹
    val gainLossAmount: Double get() = currentValue - investedAmount

    // R-4: Calculated gain/loss as percentage
    val gainLossPercent: Double
        get() = if (investedAmount > 0) (gainLossAmount / investedAmount) * 100.0 else 0.0
}
