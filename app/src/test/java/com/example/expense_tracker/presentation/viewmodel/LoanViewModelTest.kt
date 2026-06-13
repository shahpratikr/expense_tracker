package com.example.expense_tracker.presentation.viewmodel

import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
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

    private val testDispatcher = StandardTestDispatcher()

    private fun buildViewModel(): LoanViewModel {
        whenever(getLoansUseCase()).thenReturn(flowOf(emptyList()))
        return LoanViewModel(
            getLoansUseCase,
            addLoanUseCase,
            editLoanUseCase,
            deleteLoanUseCase,
            updateLoanBalanceUseCase
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
        val loan = Loan(id = 1L, name = "Home", currentBalance = 500000.0, createdAt = LocalDate.now())
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectLoan(loan)
        assertEquals(1L, vm.uiState.value.selectedLoanId)
    }

    @Test
    fun `selectLoan same loan deselects it`() = runTest {
        val loan = Loan(id = 1L, name = "Home", currentBalance = 500000.0, createdAt = LocalDate.now())
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectLoan(loan)
        vm.selectLoan(loan)
        assertNull(vm.uiState.value.selectedLoanId)
    }

    @Test
    fun `addLoan delegates to addLoanUseCase`() = runTest {
        whenever(addLoanUseCase(any(), any(), any(), any())).thenReturn(1L)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.addLoan("Car Loan", 10000.0, 9.5, 500.0)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addLoanUseCase).invoke("Car Loan", 10000.0, 9.5, 500.0)
    }

    @Test
    fun `deleteLoan delegates to deleteLoanUseCase`() = runTest {
        val loan = Loan(id = 1L, name = "Test", currentBalance = 100.0, createdAt = LocalDate.now())
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.deleteLoan(loan)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(deleteLoanUseCase).invoke(loan)
    }

    @Test
    fun `addLoan failure sets error in uiState`() = runTest {
        whenever(addLoanUseCase(any(), any(), any(), any()))
            .thenThrow(IllegalArgumentException("Loan name must not be empty"))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.addLoan("", 100.0, 0.0, 0.0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Loan name must not be empty", vm.uiState.value.error)
    }
}
