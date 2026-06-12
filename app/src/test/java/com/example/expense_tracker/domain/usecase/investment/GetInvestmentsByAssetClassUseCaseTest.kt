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

// R-4: Unit tests for GetInvestmentsByAssetClassUseCase filter behaviour
class GetInvestmentsByAssetClassUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: GetInvestmentsByAssetClassUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetInvestmentsByAssetClassUseCase(repository)
    }

    @Test
    fun delegatesAssetClassFilterToRepository() = runTest {
        val stockInvestments = listOf(
            Investment(1L, "HDFC", AssetClass.STOCKS, 10000.0, 12000.0, LocalDate.now())
        )
        whenever(repository.getByAssetClass(AssetClass.STOCKS)).thenReturn(flowOf(stockInvestments))
        val result = useCase(AssetClass.STOCKS).first()
        assert(result == stockInvestments)
    }
}
