package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import java.time.LocalDate

class EditLoanUseCaseTest {
    @Mock
    private lateinit var loanRepository: ILoanRepository

    private lateinit var editLoanUseCase: EditLoanUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        editLoanUseCase = EditLoanUseCase(loanRepository)
    }

    private fun sampleLoan(
        name: String = "Home Loan",
        currentBalance: Double = 12000.0,
        interestRate: Double = 8.0,
        emiAmount: Double = 500.0,
        emiDayOfMonth: Int = 5
    ) = Loan(
        id = 1L,
        name = name,
        currentBalance = currentBalance,
        interestRate = interestRate,
        emiAmount = emiAmount,
        loanStartDate = LocalDate.now(),
        emiDayOfMonth = emiDayOfMonth,
        lastBalanceUpdateDate = LocalDate.now(),
        createdAt = LocalDate.now()
    )

    @Test
    fun testEditLoanWithValidInputs() = runTest {
        val loan = sampleLoan()

        editLoanUseCase(loan)

        verify(loanRepository).update(loan)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithBlankName() = runTest {
        editLoanUseCase(sampleLoan(name = ""))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithNegativeBalance() = runTest {
        editLoanUseCase(sampleLoan(currentBalance = -1.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithNonPositiveInterestRate() = runTest {
        editLoanUseCase(sampleLoan(interestRate = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithNonPositiveEmiAmount() = runTest {
        editLoanUseCase(sampleLoan(emiAmount = 0.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithInvalidEmiDayOfMonth() = runTest {
        editLoanUseCase(sampleLoan(emiDayOfMonth = 0))
    }
}
