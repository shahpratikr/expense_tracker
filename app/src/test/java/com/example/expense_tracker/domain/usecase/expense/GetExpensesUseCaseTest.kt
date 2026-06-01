package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GetExpensesUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var getExpensesUseCase: GetExpensesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getExpensesUseCase = GetExpensesUseCase(expenseRepository)
    }

    @Test
    fun testGetExpenses() = runTest {
        val mockExpenses = listOf(
            Expense(1L, 100.0, 1L, LocalDate.now()),
            Expense(2L, 200.0, null, LocalDate.now())
        )
        whenever(expenseRepository.getAll()).thenReturn(flowOf(mockExpenses))

        val result = getExpensesUseCase()

        result.collect { expenses ->
            assert(expenses.size == 2)
            assert(expenses[0].id == 1L)
            assert(expenses[1].id == 2L)
        }
    }
}
