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
import java.time.LocalDate

// R-4: Unit tests for EditInvestmentUseCase validation
class EditInvestmentUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: EditInvestmentUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = EditInvestmentUseCase(repository)
    }

    @Test
    fun editValidInvestmentDelegatesToRepository() = runTest {
        useCase(1L, "SBI MF", AssetClass.MUTUAL_FUNDS, 5000.0, 6000.0, LocalDate.now())
        verify(repository).update(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun blankNameThrows() = runTest {
        useCase(1L, "  ", AssetClass.STOCKS, 5000.0, 6000.0, LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroInvestedAmountThrows() = runTest {
        useCase(1L, "SBI MF", AssetClass.MUTUAL_FUNDS, 0.0, 6000.0, LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeCurrentValueThrows() = runTest {
        useCase(1L, "SBI MF", AssetClass.MUTUAL_FUNDS, 5000.0, -1.0, LocalDate.now())
    }
}
