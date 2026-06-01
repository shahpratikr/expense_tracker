package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.time.LocalDate

class EditExpenseUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var editExpenseUseCase: EditExpenseUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        editExpenseUseCase = EditExpenseUseCase(expenseRepository)
    }

    @Test
    fun testEditExpenseWithValidInputs() = runTest {
        editExpenseUseCase(id = 1L, amount = 75.0, categoryId = 2L, date = LocalDate.now())

        verify(expenseRepository).update(any())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditExpenseWithZeroAmount() = runTest {
        editExpenseUseCase(id = 1L, amount = 0.0, categoryId = null, date = LocalDate.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testEditExpenseWithFutureDate() = runTest {
        editExpenseUseCase(id = 1L, amount = 10.0, categoryId = null, date = LocalDate.now().plusDays(1))
    }
}
