package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

// R-1: Tests for custom category deletion
class DeleteCategoryUseCaseTest {
    @Mock
    private lateinit var categoryRepository: IExpenseCategoryRepository

    private lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        deleteCategoryUseCase = DeleteCategoryUseCase(categoryRepository)
    }

    @Test
    fun testDeleteCustomCategory() = runTest {
        val category = ExpenseCategory(id = 10L, name = "Groceries", isPredefined = false)

        deleteCategoryUseCase(category)

        verify(categoryRepository).delete(category)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDeletePredefinedCategoryThrows() = runTest {
        val category = ExpenseCategory(id = 1L, name = "Food", isPredefined = true)

        deleteCategoryUseCase(category)
    }
}
