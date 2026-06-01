package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetCategoriesUseCaseTest {
    @Mock
    private lateinit var categoryRepository: IExpenseCategoryRepository

    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    }

    @Test
    fun testGetCategories() = runTest {
        val mockCategories = listOf(
            ExpenseCategory(1L, "Food", true),
            ExpenseCategory(2L, "Custom", false)
        )
        whenever(categoryRepository.getAll()).thenReturn(flowOf(mockCategories))

        val result = getCategoriesUseCase()

        result.collect { categories ->
            assert(categories.size == 2)
            assert(categories[0].name == "Food")
            assert(categories[1].name == "Custom")
        }
    }
}
