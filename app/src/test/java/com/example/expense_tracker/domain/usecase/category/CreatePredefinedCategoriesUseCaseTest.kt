package com.example.expense_tracker.domain.usecase.category

import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.repository.IExpenseCategoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreatePredefinedCategoriesUseCaseTest {
    @Mock
    private lateinit var categoryRepository: IExpenseCategoryRepository

    private lateinit var createPredefinedCategoriesUseCase: CreatePredefinedCategoriesUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        createPredefinedCategoriesUseCase = CreatePredefinedCategoriesUseCase(categoryRepository)
    }

    @Test
    fun testSeedsPredefinedCategoriesWhenNoneExist() = runTest {
        whenever(categoryRepository.getPredefined()).thenReturn(flowOf(emptyList()))

        createPredefinedCategoriesUseCase()

        verify(categoryRepository, times(6)).add(any())
    }

    @Test
    fun testDoesNotSeedWhenPredefinedAlreadyExist() = runTest {
        whenever(categoryRepository.getPredefined())
            .thenReturn(flowOf(listOf(ExpenseCategory(1L, "Food", true))))

        createPredefinedCategoriesUseCase()

        verify(categoryRepository, never()).add(any())
    }
}
