package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

// R-1: Tests for custom category renaming
class RenameCategoryUseCaseTest {
    @Mock
    private lateinit var categoryRepository: IExpenseCategoryRepository

    private lateinit var renameCategoryUseCase: RenameCategoryUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        renameCategoryUseCase = RenameCategoryUseCase(categoryRepository)
    }

    @Test
    fun testRenameCustomCategory() = runTest {
        val category = ExpenseCategory(id = 10L, name = "Groceries", isPredefined = false)

        renameCategoryUseCase(category, "Food Shopping")

        verify(categoryRepository).update(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRenamePredefinedCategoryThrows() = runTest {
        val category = ExpenseCategory(id = 1L, name = "Food", isPredefined = true)

        renameCategoryUseCase(category, "Meals")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRenameWithBlankNameThrows() = runTest {
        val category = ExpenseCategory(id = 10L, name = "Groceries", isPredefined = false)

        renameCategoryUseCase(category, "   ")
    }
}
