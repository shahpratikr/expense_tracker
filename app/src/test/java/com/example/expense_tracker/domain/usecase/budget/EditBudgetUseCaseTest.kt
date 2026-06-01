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

class EditBudgetUseCaseTest {
    @Mock
    private lateinit var budgetRepository: IBudgetRepository

    private lateinit var editBudgetUseCase: EditBudgetUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        editBudgetUseCase = EditBudgetUseCase(budgetRepository)
    }

    @Test
    fun testEditBudgetWithValidInputs() = runTest {
        val budget = Budget(
            id = 1L,
            categoryId = 1L,
            monthlyLimit = 600.0,
            monthYear = YearMonth.now()
        )

        editBudgetUseCase(budget)

        verify(budgetRepository).update(budget)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditBudgetWithZeroLimit() = runTest {
        val budget = Budget(
            id = 1L,
            categoryId = 1L,
            monthlyLimit = 0.0,
            monthYear = YearMonth.now()
        )

        editBudgetUseCase(budget)
    }
}
