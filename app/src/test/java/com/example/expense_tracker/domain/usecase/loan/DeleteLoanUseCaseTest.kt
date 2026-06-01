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

class DeleteLoanUseCaseTest {
    @Mock
    private lateinit var loanRepository: ILoanRepository

    private lateinit var deleteLoanUseCase: DeleteLoanUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        deleteLoanUseCase = DeleteLoanUseCase(loanRepository)
    }

    @Test
    fun testDeleteLoan() = runTest {
        val loan = Loan(
            id = 1L,
            name = "Car Loan",
            currentBalance = 5000.0,
            createdAt = LocalDate.now()
        )

        deleteLoanUseCase(loan)

        verify(loanRepository).delete(loan)
    }
}
