package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class AddLoanUseCaseTest {
    @Mock
    private lateinit var loanRepository: ILoanRepository

    private lateinit var addLoanUseCase: AddLoanUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addLoanUseCase = AddLoanUseCase(loanRepository)
    }

    @Test
    fun testAddLoanWithValidInputs() = runTest {
        whenever(loanRepository.add(any())).thenReturn(1L)

        val result = addLoanUseCase(
            "Car Loan", 5000.0, interestRate = 9.5, emiAmount = 500.0,
            loanStartDate = LocalDate.now(), emiDayOfMonth = 5
        )

        assert(result == 1L)
        verify(loanRepository).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddLoanWithBlankName() = runTest {
        addLoanUseCase("   ", 5000.0, 9.5, 500.0, LocalDate.now(), 5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddLoanWithNegativeBalance() = runTest {
        addLoanUseCase("Car Loan", -100.0, 9.5, 500.0, LocalDate.now(), 5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddLoanWithNonPositiveInterestRate() = runTest {
        addLoanUseCase("Car Loan", 5000.0, interestRate = 0.0, emiAmount = 500.0, loanStartDate = LocalDate.now(), emiDayOfMonth = 5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddLoanWithNonPositiveEmiAmount() = runTest {
        addLoanUseCase("Car Loan", 5000.0, interestRate = 9.5, emiAmount = 0.0, loanStartDate = LocalDate.now(), emiDayOfMonth = 5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddLoanWithInvalidEmiDayOfMonth() = runTest {
        addLoanUseCase("Car Loan", 5000.0, interestRate = 9.5, emiAmount = 500.0, loanStartDate = LocalDate.now(), emiDayOfMonth = 32)
    }
}
