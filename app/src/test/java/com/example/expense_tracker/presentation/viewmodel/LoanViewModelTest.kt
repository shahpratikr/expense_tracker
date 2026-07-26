package com.example.expense_tracker.presentation.viewmodel

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
import com.example.expense_tracker.domain.usecase.loan.RecalculateLoanBalancesUseCase
import com.example.expense_tracker.domain.usecase.loan.UpdateLoanBalanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExperimentalCoroutinesApi
class LoanViewModelTest {

    @Mock private lateinit var getLoansUseCase: GetLoansUseCase
    @Mock private lateinit var addLoanUseCase: AddLoanUseCase
    @Mock private lateinit var editLoanUseCase: EditLoanUseCase
    @Mock private lateinit var deleteLoanUseCase: DeleteLoanUseCase
    @Mock private lateinit var updateLoanBalanceUseCase: UpdateLoanBalanceUseCase
    @Mock private lateinit var recalculateLoanBalancesUseCase: RecalculateLoanBalancesUseCase

    private val testDispatcher = StandardTestDispatcher()

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

    private suspend fun buildViewModel(): LoanViewModel {
        whenever(getLoansUseCase()).thenReturn(flowOf(emptyList()))
        whenever(recalculateLoanBalancesUseCase()).thenReturn(emptyList())
        return LoanViewModel(
            getLoansUseCase,
            addLoanUseCase,
            editLoanUseCase,
            deleteLoanUseCase,
            updateLoanBalanceUseCase,
            recalculateLoanBalancesUseCase
        )
    }

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has no selected loan`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(vm.uiState.value.selectedLoanId)
    }

    @Test
    fun `selectLoan sets selectedLoanId`() = runTest {
        val loan = sampleLoan(1L, "Home", 500000.0)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectLoan(loan)
        assertEquals(1L, vm.uiState.value.selectedLoanId)
    }

    @Test
    fun `selectLoan same loan deselects it`() = runTest {
        val loan = sampleLoan(1L, "Home", 500000.0)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectLoan(loan)
        vm.selectLoan(loan)
        assertNull(vm.uiState.value.selectedLoanId)
    }

    @Test
    fun `loadLoans invokes recalculateLoanBalancesUseCase before loading list`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(recalculateLoanBalancesUseCase).invoke()
    }

    @Test
    fun `addLoan delegates to addLoanUseCase`() = runTest {
        whenever(addLoanUseCase(any(), any(), any(), any(), any(), any())).thenReturn(1L)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val startDate = LocalDate.now()
        vm.addLoan("Car Loan", 10000.0, 9.5, 500.0, startDate, 5)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addLoanUseCase).invoke("Car Loan", 10000.0, 9.5, 500.0, startDate, 5)
    }

    @Test
    fun `deleteLoan delegates to deleteLoanUseCase`() = runTest {
        val loan = sampleLoan(1L, "Test", 100.0)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.deleteLoan(loan)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(deleteLoanUseCase).invoke(loan)
    }

    @Test
    fun `addLoan failure sets error in uiState`() = runTest {
        whenever(addLoanUseCase(any(), any(), any(), any(), any(), any()))
            .thenThrow(IllegalArgumentException("Loan name must not be empty"))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.addLoan("", 100.0, 9.0, 500.0, LocalDate.now(), 5)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Loan name must not be empty", vm.uiState.value.error)
    }
}
