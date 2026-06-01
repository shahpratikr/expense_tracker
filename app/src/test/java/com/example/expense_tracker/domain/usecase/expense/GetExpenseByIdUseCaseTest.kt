package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate

class GetExpenseByIdUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var getExpenseByIdUseCase: GetExpenseByIdUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getExpenseByIdUseCase = GetExpenseByIdUseCase(expenseRepository)
    }

    @Test
    fun testReturnsExpenseWhenFound() = runTest {
        val expense = Expense(id = 7L, amount = 42.0, categoryId = 1L, date = LocalDate.now())
        whenever(expenseRepository.getById(7L)).thenReturn(expense)

        val result = getExpenseByIdUseCase(7L)

        assert(result == expense)
    }

    @Test
    fun testReturnsNullWhenNotFound() = runTest {
        whenever(expenseRepository.getById(99L)).thenReturn(null)

        val result = getExpenseByIdUseCase(99L)

        assert(result == null)
    }
}
