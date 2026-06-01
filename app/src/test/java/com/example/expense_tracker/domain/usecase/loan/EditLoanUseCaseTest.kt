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

    @Test
    fun testEditLoanWithValidInputs() = runTest {
        val loan = Loan(
            id = 1L,
            name = "Home Loan",
            currentBalance = 12000.0,
            createdAt = LocalDate.now()
        )

        editLoanUseCase(loan)

        verify(loanRepository).update(loan)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithBlankName() = runTest {
        val loan = Loan(
            id = 1L,
            name = "",
            currentBalance = 12000.0,
            createdAt = LocalDate.now()
        )

        editLoanUseCase(loan)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditLoanWithNegativeBalance() = runTest {
        val loan = Loan(
            id = 1L,
            name = "Home Loan",
            currentBalance = -1.0,
            createdAt = LocalDate.now()
        )

        editLoanUseCase(loan)
    }
}
