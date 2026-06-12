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

// R-4: Unit tests for GetInvestmentsUseCase
class GetInvestmentsUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: GetInvestmentsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetInvestmentsUseCase(repository)
    }

    @Test
    fun returnsFlowFromRepository() = runTest {
        val investments = listOf(
            Investment(1L, "HDFC", AssetClass.STOCKS, 10000.0, 12000.0, LocalDate.now())
        )
        whenever(repository.getAll()).thenReturn(flowOf(investments))
        val result = useCase().first()
        assert(result == investments)
    }
}
