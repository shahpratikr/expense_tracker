package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate

// R-5: Unit tests for GetTotalLoanBalanceUseCase
class GetTotalLoanBalanceUseCaseTest {

    @Mock private lateinit var loanRepository: ILoanRepository
    private lateinit var useCase: GetTotalLoanBalanceUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetTotalLoanBalanceUseCase(loanRepository)
    }

    // R-5: Sum of all loan balances is returned
    @Test
    fun `returns sum of all loan balances`() = runTest {
        val loans = listOf(
            Loan(id = 1L, name = "Car", currentBalance = 50000.0, createdAt = LocalDate.now(), interestRate = 9.0, emi = 2000.0),
            Loan(id = 2L, name = "Home", currentBalance = 300000.0, createdAt = LocalDate.now(), interestRate = 7.5, emi = 15000.0)
        )
        whenever(loanRepository.getAll()).thenReturn(flowOf(loans))

        useCase().collect { total ->
            assertEquals(350000.0, total, 0.001)
        }
    }

    // R-5: Zero when no loans exist
    @Test
    fun `returns zero when no loans`() = runTest {
        whenever(loanRepository.getAll()).thenReturn(flowOf(emptyList()))

        useCase().collect { total ->
            assertEquals(0.0, total, 0.001)
        }
    }
}
