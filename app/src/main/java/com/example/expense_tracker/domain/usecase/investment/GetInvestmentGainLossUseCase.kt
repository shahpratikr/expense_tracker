package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// R-4: Use case to calculate total portfolio gain/loss across all investments
class GetInvestmentGainLossUseCase(private val repository: IInvestmentRepository) {

    data class GainLossSummary(
        val totalInvested: Double,
        val totalCurrentValue: Double,
        val totalGainLossAmount: Double,
        val totalGainLossPercent: Double
    )

    // R-4: Returns live flow of aggregated portfolio gain/loss (₹ and %)
    operator fun invoke(): Flow<GainLossSummary> = repository.getAll().map { investments ->
        calculateSummary(investments)
    }

    // R-4: Calculates aggregate gain/loss from a list of investments
    fun calculateSummary(investments: List<Investment>): GainLossSummary {
        val totalInvested = investments.sumOf { it.investedAmount }
        val totalCurrentValue = investments.sumOf { it.currentValue }
        val totalGainLoss = totalCurrentValue - totalInvested
        val totalPercent = if (totalInvested > 0) (totalGainLoss / totalInvested) * 100.0 else 0.0
        return GainLossSummary(
            totalInvested = totalInvested,
            totalCurrentValue = totalCurrentValue,
            totalGainLossAmount = totalGainLoss,
            totalGainLossPercent = totalPercent
        )
    }
}
