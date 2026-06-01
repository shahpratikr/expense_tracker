package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.repository.ILoanRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GetLoansUseCaseTest {
    @Mock
    private lateinit var loanRepository: ILoanRepository

    private lateinit var getLoansUseCase: GetLoansUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getLoansUseCase = GetLoansUseCase(loanRepository)
    }

    @Test
    fun testGetLoans() = runTest {
        val mockLoans = listOf(
            Loan(1L, "Car Loan", 5000.0, LocalDate.now()),
            Loan(2L, "Home Loan", 12000.0, LocalDate.now())
        )

        whenever(loanRepository.getAll()).thenReturn(flowOf(mockLoans))

        val result = getLoansUseCase()

        result.collect { loans ->
            assert(loans.size == 2)
            assert(loans[0].id == 1L)
            assert(loans[1].id == 2L)
        }
    }
}
