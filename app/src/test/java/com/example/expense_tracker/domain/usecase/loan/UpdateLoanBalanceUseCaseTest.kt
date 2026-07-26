package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class UpdateLoanBalanceUseCaseTest {
    @Mock
    private lateinit var loanRepository: ILoanRepository

    private lateinit var updateLoanBalanceUseCase: UpdateLoanBalanceUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        updateLoanBalanceUseCase = UpdateLoanBalanceUseCase(loanRepository)
    }

    @Test
    fun testUpdateLoanBalanceWithValidInputs() = runTest {
        val existing = Loan(
            id = 1L,
            name = "Car Loan",
            currentBalance = 5000.0,
            interestRate = 9.0,
            emiAmount = 500.0,
            loanStartDate = LocalDate.now(),
            emiDayOfMonth = 5,
            lastBalanceUpdateDate = LocalDate.now(),
            createdAt = LocalDate.now()
        )
        whenever(loanRepository.getById(1L)).thenReturn(existing)

        updateLoanBalanceUseCase(1L, 4000.0)

        verify(loanRepository).update(existing.copy(currentBalance = 4000.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUpdateLoanBalanceWithNegativeBalance() = runTest {
        updateLoanBalanceUseCase(1L, -50.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUpdateLoanBalanceWhenLoanNotFound() = runTest {
        whenever(loanRepository.getById(99L)).thenReturn(null)

        updateLoanBalanceUseCase(99L, 100.0)
    }
}
