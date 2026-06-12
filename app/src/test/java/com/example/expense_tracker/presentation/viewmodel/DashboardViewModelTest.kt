package com.example.expense_tracker.presentation.viewmodel

import com.example.expense_tracker.domain.usecase.dashboard.GetBudgetHeadroomUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetMonthlySpendingUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetTotalLoanBalanceUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
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
import org.mockito.kotlin.whenever

// R-5: Unit tests for DashboardViewModel — all four metric flows and error handling
@ExperimentalCoroutinesApi
class DashboardViewModelTest {

    @Mock private lateinit var getMonthlySpendingUseCase: GetMonthlySpendingUseCase
    @Mock private lateinit var getTotalLoanBalanceUseCase: GetTotalLoanBalanceUseCase
    @Mock private lateinit var getInvestmentGainLossUseCase: GetInvestmentGainLossUseCase
    @Mock private lateinit var getBudgetHeadroomUseCase: GetBudgetHeadroomUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val emptySummary = GetInvestmentGainLossUseCase.GainLossSummary(0.0, 0.0, 0.0, 0.0)

    private fun buildViewModel(): DashboardViewModel {
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(0.0))
        whenever(getTotalLoanBalanceUseCase()).thenReturn(flowOf(0.0))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        whenever(getBudgetHeadroomUseCase()).thenReturn(flowOf(0.0))
        return DashboardViewModel(
            getMonthlySpendingUseCase,
            getTotalLoanBalanceUseCase,
            getInvestmentGainLossUseCase,
            getBudgetHeadroomUseCase
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

    // R-5: Dashboard state updates with monthly spending from use case
    @Test
    fun `uiState reflects totalMonthlySpending from use case`() = runTest {
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(4500.0))
        whenever(getTotalLoanBalanceUseCase()).thenReturn(flowOf(0.0))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        whenever(getBudgetHeadroomUseCase()).thenReturn(flowOf(0.0))
        val vm = DashboardViewModel(
            getMonthlySpendingUseCase,
            getTotalLoanBalanceUseCase,
            getInvestmentGainLossUseCase,
            getBudgetHeadroomUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(4500.0, vm.uiState.value.totalMonthlySpending, 0.001)
    }

    // R-5: Dashboard state updates with total loan balance from use case
    @Test
    fun `uiState reflects totalLoanBalance from use case`() = runTest {
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(0.0))
        whenever(getTotalLoanBalanceUseCase()).thenReturn(flowOf(120000.0))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        whenever(getBudgetHeadroomUseCase()).thenReturn(flowOf(0.0))
        val vm = DashboardViewModel(
            getMonthlySpendingUseCase,
            getTotalLoanBalanceUseCase,
            getInvestmentGainLossUseCase,
            getBudgetHeadroomUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(120000.0, vm.uiState.value.totalLoanBalance, 0.001)
    }

    // R-5: Dashboard state updates with investment gain/loss from use case
    @Test
    fun `uiState reflects investment gain loss from use case`() = runTest {
        val summary = GetInvestmentGainLossUseCase.GainLossSummary(
            totalInvested = 50000.0,
            totalCurrentValue = 55000.0,
            totalGainLossAmount = 5000.0,
            totalGainLossPercent = 10.0
        )
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(0.0))
        whenever(getTotalLoanBalanceUseCase()).thenReturn(flowOf(0.0))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(summary))
        whenever(getBudgetHeadroomUseCase()).thenReturn(flowOf(0.0))
        val vm = DashboardViewModel(
            getMonthlySpendingUseCase,
            getTotalLoanBalanceUseCase,
            getInvestmentGainLossUseCase,
            getBudgetHeadroomUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5000.0, vm.uiState.value.totalInvestmentGainLossAmount, 0.001)
        assertEquals(10.0, vm.uiState.value.totalInvestmentGainLossPercent, 0.001)
    }

    // R-5: Dashboard state updates with budget headroom from use case
    @Test
    fun `uiState reflects budgetHeadroom from use case`() = runTest {
        whenever(getMonthlySpendingUseCase()).thenReturn(flowOf(0.0))
        whenever(getTotalLoanBalanceUseCase()).thenReturn(flowOf(0.0))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        whenever(getBudgetHeadroomUseCase()).thenReturn(flowOf(2500.0))
        val vm = DashboardViewModel(
            getMonthlySpendingUseCase,
            getTotalLoanBalanceUseCase,
            getInvestmentGainLossUseCase,
            getBudgetHeadroomUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2500.0, vm.uiState.value.budgetHeadroom, 0.001)
    }

    // R-5: clearError resets error to null in uiState
    @Test
    fun `clearError resets error to null`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        vm.clearError()
        assertNull(vm.uiState.value.error)
    }
}
