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

// PRD Feature 3: Unit tests for GetTotalLoanBalanceUseCase
class GetTotalLoanBalanceUseCaseTest {

    @Mock private lateinit var loanRepository: ILoanRepository
    private lateinit var useCase: GetTotalLoanBalanceUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetTotalLoanBalanceUseCase(loanRepository)
    }

    private fun sampleLoan(id: Long, name: String, balance: Double, interestRate: Double, emiAmount: Double) = Loan(
        id = id,
        name = name,
        currentBalance = balance,
        interestRate = interestRate,
        emiAmount = emiAmount,
        loanStartDate = LocalDate.now(),
        emiDayOfMonth = 5,
        lastBalanceUpdateDate = LocalDate.now(),
        createdAt = LocalDate.now()
    )

    @Test
    fun `returns sum of all loan balances`() = runTest {
        val loans = listOf(
            sampleLoan(1L, "Car", 50000.0, 9.0, 2000.0),
            sampleLoan(2L, "Home", 300000.0, 7.5, 15000.0)
        )
        whenever(loanRepository.getAll()).thenReturn(flowOf(loans))

        useCase().collect { total ->
            assertEquals(350000.0, total, 0.001)
        }
    }

    @Test
    fun `returns zero when no loans`() = runTest {
        whenever(loanRepository.getAll()).thenReturn(flowOf(emptyList()))

        useCase().collect { total ->
            assertEquals(0.0, total, 0.001)
        }
    }
}
