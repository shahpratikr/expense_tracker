package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.flow.Flow

// R-4: Use case to filter investments by asset class
class GetInvestmentsByAssetClassUseCase(private val repository: IInvestmentRepository) {

    // R-4: Returns live flow of investments for the given asset class
    operator fun invoke(assetClass: AssetClass): Flow<List<Investment>> =
        repository.getByAssetClass(assetClass)
}
