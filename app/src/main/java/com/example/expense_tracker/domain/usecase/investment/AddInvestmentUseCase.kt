package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import java.time.LocalDate

// R-4: Use case to add a new investment with validation
class AddInvestmentUseCase(private val repository: IInvestmentRepository) {

    // R-4: Validates inputs then persists the investment; returns new row id
    suspend operator fun invoke(
        name: String,
        assetClass: AssetClass,
        investedAmount: Double,
        currentValue: Double,
        date: LocalDate = LocalDate.now()
    ): Long {
        require(name.isNotBlank()) { "Investment name is required" }
        require(investedAmount > 0) { "Invested amount must be greater than 0" }
        require(currentValue >= 0) { "Current value must be >= 0" }

        val investment = Investment(
            name = name.trim(),
            assetClass = assetClass,
            investedAmount = investedAmount,
            currentValue = currentValue,
            date = date
        )
        return repository.add(investment)
    }
}
