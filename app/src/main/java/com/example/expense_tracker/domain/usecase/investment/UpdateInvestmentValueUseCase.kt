package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.repository.IInvestmentRepository

// R-4: Use case to manually update an investment's current value
class UpdateInvestmentValueUseCase(private val repository: IInvestmentRepository) {

    // R-4: Validates new value then updates current_value on the investment
    suspend operator fun invoke(id: Long, newCurrentValue: Double) {
        require(newCurrentValue >= 0) { "Current value must be >= 0" }

        val existing = repository.getById(id)
            ?: throw IllegalArgumentException("Investment not found: $id")

        repository.update(existing.copy(currentValue = newCurrentValue))
    }
}
