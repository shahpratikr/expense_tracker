package com.example.expense_tracker.domain.usecase.investment

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.repository.IInvestmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

// R-4: Unit tests for UpdateInvestmentValueUseCase
class UpdateInvestmentValueUseCaseTest {

    @Mock
    private lateinit var repository: IInvestmentRepository

    private lateinit var useCase: UpdateInvestmentValueUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = UpdateInvestmentValueUseCase(repository)
    }

    private fun sampleInvestment() = Investment(
        id = 1L,
        name = "HDFC Stock",
        assetClass = AssetClass.STOCKS,
        investedAmount = 10000.0,
        currentValue = 12000.0,
        date = LocalDate.now()
    )

    @Test
    fun updatesCurrentValueOnExistingInvestment() = runTest {
        whenever(repository.getById(1L)).thenReturn(sampleInvestment())
        useCase(1L, 15000.0)
        verify(repository).update(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeValueThrows() = runTest {
        useCase(1L, -500.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun investmentNotFoundThrows() = runTest {
        whenever(repository.getById(99L)).thenReturn(null)
        useCase(99L, 5000.0)
    }
}
