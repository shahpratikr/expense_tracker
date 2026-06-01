package com.example.expense_tracker.domain.usecase.dashboard

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.YearMonth

class GetMonthlySpendingUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var getMonthlySpendingUseCase: GetMonthlySpendingUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getMonthlySpendingUseCase = GetMonthlySpendingUseCase(expenseRepository)
    }

    @Test
    fun testMonthlySpendingSumsExpenseAmounts() = runTest {
        val month = YearMonth.of(2026, 6)
        val expenses = listOf(
            Expense(1L, 100.0, null, LocalDate.of(2026, 6, 1)),
            Expense(2L, 50.5, 1L, LocalDate.of(2026, 6, 15)),
            Expense(3L, 24.5, 2L, LocalDate.of(2026, 6, 30))
        )
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(expenses))

        val result = getMonthlySpendingUseCase(month)

        result.collect { total ->
            assert(total == 175.0)
        }
    }

    @Test
    fun testMonthlySpendingIsZeroWhenNoExpenses() = runTest {
        val month = YearMonth.of(2026, 6)
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(emptyList()))

        val result = getMonthlySpendingUseCase(month)

        result.collect { total ->
            assert(total == 0.0)
        }
    }
}
