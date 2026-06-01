package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AddCategoryUseCaseTest {
    @Mock
    private lateinit var categoryRepository: IExpenseCategoryRepository

    private lateinit var addCategoryUseCase: AddCategoryUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addCategoryUseCase = AddCategoryUseCase(categoryRepository)
    }

    @Test
    fun testAddCategoryWithValidName() = runTest {
        whenever(categoryRepository.add(any())).thenReturn(1L)

        val result = addCategoryUseCase("Groceries")

        assert(result == 1L)
        verify(categoryRepository).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddCategoryWithBlankName() = runTest {
        addCategoryUseCase("   ")
    }
}
