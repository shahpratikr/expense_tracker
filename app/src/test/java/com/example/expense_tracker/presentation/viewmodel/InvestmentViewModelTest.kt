package com.example.expense_tracker.presentation.viewmodel

import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.usecase.investment.AddInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.DeleteInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.EditInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentsUseCase
import com.example.expense_tracker.domain.usecase.investment.UpdateInvestmentValueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

// R-4: Unit tests for InvestmentViewModel — state emission, filter, and CRUD delegation
@ExperimentalCoroutinesApi
class InvestmentViewModelTest {

    @Mock private lateinit var getInvestmentsUseCase: GetInvestmentsUseCase
    @Mock private lateinit var addInvestmentUseCase: AddInvestmentUseCase
    @Mock private lateinit var editInvestmentUseCase: EditInvestmentUseCase
    @Mock private lateinit var deleteInvestmentUseCase: DeleteInvestmentUseCase
    @Mock private lateinit var updateInvestmentValueUseCase: UpdateInvestmentValueUseCase
    @Mock private lateinit var getInvestmentGainLossUseCase: GetInvestmentGainLossUseCase

    private val testDispatcher = StandardTestDispatcher()

    private val sampleInvestments = listOf(
        Investment(1L, "HDFC Stock", AssetClass.STOCKS, 10000.0, 12000.0, LocalDate.now()),
        Investment(2L, "SBI MF", AssetClass.MUTUAL_FUNDS, 5000.0, 4500.0, LocalDate.now())
    )

    private val emptySummary = GetInvestmentGainLossUseCase.GainLossSummary(
        totalInvested = 0.0,
        totalCurrentValue = 0.0,
        totalGainLossAmount = 0.0,
        totalGainLossPercent = 0.0
    )

    private fun buildViewModel(): InvestmentViewModel {
        whenever(getInvestmentsUseCase()).thenReturn(flowOf(sampleInvestments))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        return InvestmentViewModel(
            getInvestmentsUseCase,
            addInvestmentUseCase,
            editInvestmentUseCase,
            deleteInvestmentUseCase,
            updateInvestmentValueUseCase,
            getInvestmentGainLossUseCase
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

    // R-4: Initial state should be Loading before flows emit
    @Test
    fun `initial uiState is Loading`() {
        whenever(getInvestmentsUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getInvestmentGainLossUseCase()).thenReturn(flowOf(emptySummary))
        val vm = InvestmentViewModel(
            getInvestmentsUseCase,
            addInvestmentUseCase,
            editInvestmentUseCase,
            deleteInvestmentUseCase,
            updateInvestmentValueUseCase,
            getInvestmentGainLossUseCase
        )
        assertTrue(vm.uiState.value is InvestmentViewModel.UiState.Loading)
    }

    // R-4: uiState transitions to Success with all investments when no filter applied
    @Test
    fun `uiState emits Success with all investments when no filter`() = runTest {
        val vm = buildViewModel()
        // Collect to activate WhileSubscribed stateIn
        val collectJob = launch { vm.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is InvestmentViewModel.UiState.Success)
        val success = state as InvestmentViewModel.UiState.Success
        assertEquals(2, success.investments.size)
        assertNull(success.selectedAssetClass)
        collectJob.cancel()
    }

    // R-4: filterByAssetClass filters the investment list to the selected asset class
    @Test
    fun `filterByAssetClass filters investments to selected class`() = runTest {
        val vm = buildViewModel()
        val collectJob = launch { vm.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        vm.filterByAssetClass(AssetClass.STOCKS)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value as InvestmentViewModel.UiState.Success
        assertEquals(1, state.investments.size)
        assertEquals("HDFC Stock", state.investments[0].name)
        assertEquals(AssetClass.STOCKS, state.selectedAssetClass)
        collectJob.cancel()
    }

    // R-4: filterByAssetClass(null) resets to show all investments
    @Test
    fun `filterByAssetClass null shows all investments`() = runTest {
        val vm = buildViewModel()
        val collectJob = launch { vm.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        vm.filterByAssetClass(AssetClass.STOCKS)
        testDispatcher.scheduler.advanceUntilIdle()
        vm.filterByAssetClass(null)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value as InvestmentViewModel.UiState.Success
        assertEquals(2, state.investments.size)
        assertNull(state.selectedAssetClass)
        collectJob.cancel()
    }

    // R-4: addInvestment delegates to use case
    @Test
    fun `addInvestment delegates to addInvestmentUseCase`() = runTest {
        whenever(addInvestmentUseCase(any(), any(), any(), any(), any())).thenReturn(3L)
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val date = LocalDate.now()
        vm.addInvestment("New Stock", AssetClass.STOCKS, 20000.0, 22000.0, date)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(addInvestmentUseCase).invoke("New Stock", AssetClass.STOCKS, 20000.0, 22000.0, date)
    }

    // R-4: deleteInvestment delegates to use case
    @Test
    fun `deleteInvestment delegates to deleteInvestmentUseCase`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.deleteInvestment(sampleInvestments[0])
        testDispatcher.scheduler.advanceUntilIdle()

        verify(deleteInvestmentUseCase).invoke(sampleInvestments[0])
    }

    // R-4: updateCurrentValue delegates to use case
    @Test
    fun `updateCurrentValue delegates to updateInvestmentValueUseCase`() = runTest {
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.updateCurrentValue(1L, 15000.0)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(updateInvestmentValueUseCase).invoke(1L, 15000.0)
    }

    // R-4: addInvestment validation failure exposes error via errorMessage
    @Test
    fun `addInvestment failure exposes error message`() = runTest {
        whenever(addInvestmentUseCase(any(), any(), any(), any(), any()))
            .thenThrow(IllegalArgumentException("Investment name is required"))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.addInvestment("", AssetClass.STOCKS, 10000.0, 11000.0, LocalDate.now())
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Investment name is required", vm.errorMessage.value)
    }

    // R-4: clearError resets errorMessage to null
    @Test
    fun `clearError resets errorMessage to null`() = runTest {
        whenever(addInvestmentUseCase(any(), any(), any(), any(), any()))
            .thenThrow(IllegalArgumentException("Some error"))
        val vm = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.addInvestment("", AssetClass.STOCKS, 10000.0, 11000.0, LocalDate.now())
        testDispatcher.scheduler.advanceUntilIdle()

        vm.clearError()
        assertNull(vm.errorMessage.value)
    }
}
