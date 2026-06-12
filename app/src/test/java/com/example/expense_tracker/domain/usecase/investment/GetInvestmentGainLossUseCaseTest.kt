package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate

// R-4: Unit tests for GetInvestmentGainLossUseCase — verifies gain/loss calculation
class GetInvestmentGainLossUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: GetInvestmentGainLossUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetInvestmentGainLossUseCase(repository)
    }

    @Test
    fun calculatesSummaryCorrectlyForGain() {
        val investments = listOf(
            Investment(1L, "A", AssetClass.STOCKS, 10000.0, 12000.0, LocalDate.now()),
            Investment(2L, "B", AssetClass.MUTUAL_FUNDS, 5000.0, 4000.0, LocalDate.now())
        )
        val summary = useCase.calculateSummary(investments)
        assert(summary.totalInvested == 15000.0)
        assert(summary.totalCurrentValue == 16000.0)
        assert(summary.totalGainLossAmount == 1000.0)
        assert(summary.totalGainLossPercent == (1000.0 / 15000.0) * 100)
    }

    @Test
    fun returnsZeroPercentForEmptyList() {
        val summary = useCase.calculateSummary(emptyList())
        assert(summary.totalInvested == 0.0)
        assert(summary.totalCurrentValue == 0.0)
        assert(summary.totalGainLossAmount == 0.0)
        assert(summary.totalGainLossPercent == 0.0)
    }

    @Test
    fun emitsFromRepositoryFlow() = runTest {
        val investments = listOf(
            Investment(1L, "A", AssetClass.STOCKS, 10000.0, 11000.0, LocalDate.now())
        )
        whenever(repository.getAll()).thenReturn(flowOf(investments))
        val summary = useCase().first()
        assert(summary.totalGainLossAmount == 1000.0)
    }
}
