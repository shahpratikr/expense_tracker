package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IBudgetRepository
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.YearMonth

class GetBudgetWithSpentUseCaseTest {
    @Mock
    private lateinit var budgetRepository: IBudgetRepository

    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var getBudgetWithSpentUseCase: GetBudgetWithSpentUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getBudgetWithSpentUseCase = GetBudgetWithSpentUseCase(budgetRepository, expenseRepository)
    }

    @Test
    fun testGetBudgetWithSpent() = runTest {
        val categoryId = 1L
        val month = YearMonth.now()
        val budget = Budget(1L, categoryId, 500.0, month)
        val expenses = listOf(
            Expense(1L, 100.0, categoryId, month.atDay(1)),
            Expense(2L, 150.0, categoryId, month.atDay(5))
        )

        whenever(budgetRepository.getByCategoryAndMonth(categoryId, month)).thenReturn(budget)
        whenever(expenseRepository.getAll()).thenReturn(flowOf(expenses))

        val result = getBudgetWithSpentUseCase(categoryId, month)

        assert(result != null)
        assert(result?.budget?.id == 1L)
        assert(result?.spent == 250.0)
    }

    @Test
    fun testGetBudgetWithSpentReturnsNullWhenNoBudget() = runTest {
        val categoryId = 1L
        val month = YearMonth.now()

        whenever(budgetRepository.getByCategoryAndMonth(categoryId, month)).thenReturn(null)

        val result = getBudgetWithSpentUseCase(categoryId, month)

        assert(result == null)
    }
}
