package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import java.time.LocalDate

// R-4: Unit tests for DeleteInvestmentUseCase
class DeleteInvestmentUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: DeleteInvestmentUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = DeleteInvestmentUseCase(repository)
    }

    @Test
    fun deleteInvestmentDelegatesToRepository() = runTest {
        val investment = Investment(
            id = 1L,
            name = "Gold ETF",
            assetClass = AssetClass.OTHER,
            investedAmount = 10000.0,
            currentValue = 11000.0,
            date = LocalDate.now()
        )
        useCase(investment)
        verify(repository).delete(investment)
    }
}
