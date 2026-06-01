package com.example.expense_tracker.domain.usecase.expense

import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.repository.IExpenseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import java.time.LocalDate

class DeleteExpenseUseCaseTest {
    @Mock
    private lateinit var expenseRepository: IExpenseRepository

    private lateinit var deleteExpenseUseCase: DeleteExpenseUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        deleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository)
    }

    @Test
    fun testDeleteExpense() = runTest {
        val expense = Expense(id = 1L, amount = 25.0, categoryId = 1L, date = LocalDate.now())

        deleteExpenseUseCase(expense)

        verify(expenseRepository).delete(expense)
    }
}
