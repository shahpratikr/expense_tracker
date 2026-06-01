package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import java.time.YearMonth

class DeleteBudgetUseCaseTest {
    @Mock
    private lateinit var budgetRepository: IBudgetRepository

    private lateinit var deleteBudgetUseCase: DeleteBudgetUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        deleteBudgetUseCase = DeleteBudgetUseCase(budgetRepository)
    }

    @Test
    fun testDeleteBudget() = runTest {
        val budget = Budget(
            id = 1L,
            categoryId = 1L,
            monthlyLimit = 500.0,
            monthYear = YearMonth.now()
        )

        deleteBudgetUseCase(budget)

        verify(budgetRepository).delete(budget)
    }
}
