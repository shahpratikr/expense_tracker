package com.example.expense_tracker.domain.usecase.expense

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

class FilterExpensesByDateUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var filterExpensesByDateUseCase: FilterExpensesByDateUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        filterExpensesByDateUseCase = FilterExpensesByDateUseCase(expenseRepository)
    }

    @Test
    fun testFilterExpensesByValidDateRange() = runTest {
        val start = LocalDate.of(2026, 6, 1)
        val end = LocalDate.of(2026, 6, 30)
        val mockExpenses = listOf(Expense(1L, 100.0, 1L, LocalDate.of(2026, 6, 15)))
        whenever(expenseRepository.getByDateRange(any(), any())).thenReturn(flowOf(mockExpenses))

        val result = filterExpensesByDateUseCase(start, end)

        result.collect { expenses ->
            assert(expenses.size == 1)
            assert(expenses[0].id == 1L)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilterExpensesWithStartAfterEnd() = runTest {
        val start = LocalDate.of(2026, 6, 30)
        val end = LocalDate.of(2026, 6, 1)

        filterExpensesByDateUseCase(start, end)
    }
}
