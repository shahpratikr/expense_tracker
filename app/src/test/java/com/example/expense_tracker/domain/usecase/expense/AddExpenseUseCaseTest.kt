package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class AddExpenseUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var addExpenseUseCase: AddExpenseUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        addExpenseUseCase = AddExpenseUseCase(expenseRepository)
    }

    @Test
    fun testAddExpenseWithValidInputs() = runTest {
        whenever(expenseRepository.add(any())).thenReturn(1L)

        val result = addExpenseUseCase(amount = 50.0, categoryId = 1L, date = LocalDate.now())

        assert(result == 1L)
        verify(expenseRepository).add(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddExpenseWithZeroAmount() = runTest {
        addExpenseUseCase(amount = 0.0, categoryId = null, date = LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddExpenseWithNegativeAmount() = runTest {
        addExpenseUseCase(amount = -10.0, categoryId = null, date = LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAddExpenseWithFutureDate() = runTest {
        addExpenseUseCase(amount = 10.0, categoryId = null, date = LocalDate.now().plusDays(1))
    }
}
