package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.YearMonth

// R-5: Unit tests for GetBudgetHeadroomUseCase
class GetBudgetHeadroomUseCaseTest {

    @Mock private lateinit var budgetRepository: IBudgetRepository
    @Mock private lateinit var expenseRepository: IExpenseRepository
    private lateinit var useCase: GetBudgetHeadroomUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetBudgetHeadroomUseCase(budgetRepository, expenseRepository)
    }

    // R-5: Headroom is limit minus spending in budgeted categories
    @Test
    fun `headroom equals total limit minus spending in budgeted categories`() = runTest {
        val month = YearMonth.of(2026, 6)
        val budgets = listOf(
            Budget(1L, categoryId = 1L, monthlyLimit = 5000.0, monthYear = month),
            Budget(2L, categoryId = 2L, monthlyLimit = 3000.0, monthYear = month)
        )
        val expenses = listOf(
            Expense(1L, 2000.0, categoryId = 1L, date = LocalDate.of(2026, 6, 5)),
            Expense(2L, 1000.0, categoryId = 2L, date = LocalDate.of(2026, 6, 10)),
            Expense(3L, 500.0, categoryId = null, date = LocalDate.of(2026, 6, 15)) // uncategorized, excluded
        )
        whenever(budgetRepository.getByMonthYear(month)).thenReturn(flowOf(budgets))
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(expenses))

        useCase(month).collect { headroom ->
            // limit = 8000, spent in budgeted cats = 3000, headroom = 5000
            assertEquals(5000.0, headroom, 0.001)
        }
    }

    // R-5: Negative headroom when spending exceeds limits
    @Test
    fun `headroom is negative when spending exceeds budgets`() = runTest {
        val month = YearMonth.of(2026, 6)
        val budgets = listOf(
            Budget(1L, categoryId = 1L, monthlyLimit = 1000.0, monthYear = month)
        )
        val expenses = listOf(
            Expense(1L, 1500.0, categoryId = 1L, date = LocalDate.of(2026, 6, 5))
        )
        whenever(budgetRepository.getByMonthYear(month)).thenReturn(flowOf(budgets))
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(expenses))

        useCase(month).collect { headroom ->
            assertEquals(-500.0, headroom, 0.001)
        }
    }

    // R-5: Zero headroom when no budgets exist
    @Test
    fun `headroom is zero when no budgets`() = runTest {
        val month = YearMonth.of(2026, 6)
        whenever(budgetRepository.getByMonthYear(month)).thenReturn(flowOf(emptyList()))
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(emptyList()))

        useCase(month).collect { headroom ->
            assertEquals(0.0, headroom, 0.001)
        }
    }
}
