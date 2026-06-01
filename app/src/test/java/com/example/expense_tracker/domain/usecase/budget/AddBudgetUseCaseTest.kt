package com.example.expense_tracker.domain.usecase.budget

import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.repository.IBudgetRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.YearMonth

class AddBudgetUseCaseTest {
    @Mock
    private lateinit var budgetRepository: IBudgetRepository

    private lateinit var addBudgetUseCase: AddBudgetUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addBudgetUseCase = AddBudgetUseCase(budgetRepository)
    }

    @Test
    fun testAddBudgetWithValidInputs() = runTest {
        val categoryId = 1L
        val limit = 500.0
        val month = YearMonth.now()

        whenever(budgetRepository.getByCategoryAndMonth(categoryId, month)).thenReturn(null)
        whenever(budgetRepository.add(any())).thenReturn(1L)

        val result = addBudgetUseCase(categoryId, limit, month)

        assert(result == 1L)
        verify(budgetRepository).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddBudgetRejectsDuplicateCategoryAndMonth() = runTest {
        val categoryId = 1L
        val limit = 500.0
        val month = YearMonth.now()

        whenever(budgetRepository.getByCategoryAndMonth(categoryId, month))
            .thenReturn(Budget(id = 1L, categoryId = categoryId, monthlyLimit = 200.0, monthYear = month))

        addBudgetUseCase(categoryId, limit, month)
    }

    @Test
    fun testAddBudgetDoesNotInsertWhenDuplicate() = runTest {
        val categoryId = 1L
        val month = YearMonth.now()

        whenever(budgetRepository.getByCategoryAndMonth(categoryId, month))
            .thenReturn(Budget(id = 1L, categoryId = categoryId, monthlyLimit = 200.0, monthYear = month))

        runCatching { addBudgetUseCase(categoryId, 500.0, month) }

        verify(budgetRepository, never()).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddBudgetWithZeroLimit() = runTest {
        val categoryId = 1L
        val limit = 0.0
        val month = YearMonth.now()

        addBudgetUseCase(categoryId, limit, month)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddBudgetWithNegativeLimit() = runTest {
        val categoryId = 1L
        val limit = -100.0
        val month = YearMonth.now()

        addBudgetUseCase(categoryId, limit, month)
    }
}
