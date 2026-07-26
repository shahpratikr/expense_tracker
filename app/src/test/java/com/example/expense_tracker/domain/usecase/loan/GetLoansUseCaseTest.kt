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

    private fun sampleLoan(id: Long, name: String, balance: Double) = Loan(
        id = id,
        name = name,
        currentBalance = balance,
        interestRate = 9.0,
        emiAmount = 500.0,
        loanStartDate = LocalDate.now(),
        emiDayOfMonth = 5,
        lastBalanceUpdateDate = LocalDate.now(),
        createdAt = LocalDate.now()
    )

    @Test
    fun testGetLoans() = runTest {
        val mockLoans = listOf(
            sampleLoan(1L, "Car Loan", 5000.0),
            sampleLoan(2L, "Home Loan", 12000.0)
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
