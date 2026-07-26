package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

// PRD Feature 1: Unit tests for RecalculateLoanBalancesUseCase's amortization cycle math
class RecalculateLoanBalancesUseCaseTest {

    @Mock private lateinit var loanRepository: ILoanRepository
    private lateinit var useCase: RecalculateLoanBalancesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = RecalculateLoanBalancesUseCase(loanRepository)
    }

    private fun sampleLoan(
        lastBalanceUpdateDate: LocalDate,
        currentBalance: Double = 100000.0,
        interestRate: Double = 12.0,
        emiAmount: Double = 5000.0,
        emiDayOfMonth: Int = 5
    ) = Loan(
        id = 1L,
        name = "Car Loan",
        currentBalance = currentBalance,
        interestRate = interestRate,
        emiAmount = emiAmount,
        loanStartDate = lastBalanceUpdateDate,
        emiDayOfMonth = emiDayOfMonth,
        lastBalanceUpdateDate = lastBalanceUpdateDate,
        createdAt = lastBalanceUpdateDate
    )

    @Test
    fun `applies one amortization cycle for a single elapsed EMI date`() = runTest {
        val lastUpdate = LocalDate.of(2026, 6, 5)
        val today = LocalDate.of(2026, 7, 5)
        val loan = sampleLoan(lastBalanceUpdateDate = lastUpdate)
        whenever(loanRepository.getAll()).thenReturn(flowOf(listOf(loan)))

        useCase(today)

        val captor = argumentCaptor<Loan>()
        verify(loanRepository).update(captor.capture())
        val monthlyRate = 12.0 / 12.0 / 100.0
        val expectedInterest = 100000.0 * monthlyRate
        val expectedBalance = 100000.0 - (5000.0 - expectedInterest)
        assertEquals(expectedBalance, captor.firstValue.currentBalance, 0.01)
        assertEquals(today, captor.firstValue.lastBalanceUpdateDate)
    }

    @Test
    fun `does not update loan when no EMI date has elapsed`() = runTest {
        val lastUpdate = LocalDate.of(2026, 7, 1)
        val today = LocalDate.of(2026, 7, 4)
        val loan = sampleLoan(lastBalanceUpdateDate = lastUpdate, emiDayOfMonth = 5)
        whenever(loanRepository.getAll()).thenReturn(flowOf(listOf(loan)))

        val warnings = useCase(today)

        verify(loanRepository, never()).update(any())
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `surfaces warning and stops advancing when EMI does not cover interest`() = runTest {
        val lastUpdate = LocalDate.of(2026, 6, 5)
        val today = LocalDate.of(2026, 7, 5)
        // Interest for a cycle: 100000 * (24/12/100) = 2000; EMI of 1000 can't cover it
        val loan = sampleLoan(lastBalanceUpdateDate = lastUpdate, interestRate = 24.0, emiAmount = 1000.0)
        whenever(loanRepository.getAll()).thenReturn(flowOf(listOf(loan)))

        val warnings = useCase(today)

        assertTrue(warnings.isNotEmpty())
        verify(loanRepository, never()).update(any())
    }
}
