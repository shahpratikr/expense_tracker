package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

// R-4: Unit tests for AddInvestmentUseCase validation and delegation
class AddInvestmentUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: AddInvestmentUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = AddInvestmentUseCase(repository)
    }

    @Test
    fun addValidInvestmentDelegatesToRepository() = runTest {
        whenever(repository.add(any())).thenReturn(1L)
        val result = useCase("HDFC Stock", AssetClass.STOCKS, 10000.0, 12000.0)
        assert(result == 1L)
        verify(repository).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankNameThrows() = runTest {
        useCase("  ", AssetClass.STOCKS, 10000.0, 12000.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroInvestedAmountThrows() = runTest {
        useCase("HDFC Stock", AssetClass.STOCKS, 0.0, 12000.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeInvestedAmountThrows() = runTest {
        useCase("HDFC Stock", AssetClass.STOCKS, -100.0, 12000.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeCurrentValueThrows() = runTest {
        useCase("HDFC Stock", AssetClass.STOCKS, 10000.0, -1.0)
    }

    @Test
    fun zeroCurrentValueIsAllowed() = runTest {
        whenever(repository.add(any())).thenReturn(2L)
        val result = useCase("HDFC Stock", AssetClass.STOCKS, 10000.0, 0.0)
        assert(result == 2L)
    }
}
