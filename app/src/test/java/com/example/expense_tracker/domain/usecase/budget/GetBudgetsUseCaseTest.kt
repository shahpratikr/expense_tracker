package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.YearMonth

class GetBudgetsUseCaseTest {
    @Mock
    private lateinit var budgetRepository: IBudgetRepository

    private lateinit var getBudgetsUseCase: GetBudgetsUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getBudgetsUseCase = GetBudgetsUseCase(budgetRepository)
    }

    @Test
    fun testGetBudgets() = runTest {
        val mockBudgets = listOf(
            Budget(1L, 1L, 500.0, YearMonth.now()),
            Budget(2L, 2L, 1000.0, YearMonth.now())
        )

        whenever(budgetRepository.getAll()).thenReturn(flowOf(mockBudgets))

        val result = getBudgetsUseCase()

        result.collect { budgets ->
            assert(budgets.size == 2)
            assert(budgets[0].id == 1L)
            assert(budgets[1].id == 2L)
        }
    }
}
