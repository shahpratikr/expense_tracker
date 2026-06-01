package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.domain.usecase.expense.AddExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.DeleteExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.EditExpenseUseCase
import com.example.expense_tracker.domain.usecase.expense.FilterExpensesByDateUseCase
import com.example.expense_tracker.domain.usecase.expense.GetExpenseByIdUseCase
import com.example.expense_tracker.domain.usecase.expense.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val editExpenseUseCase: EditExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val filterExpensesByDateUseCase: FilterExpensesByDateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getExpensesUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
                .collect { expenses ->
                    _uiState.value = _uiState.value.copy(
                        expenses = expenses,
                        isLoading = false
                    )
                }
        }
    }

    fun addExpense(amount: Double, categoryId: Long?, date: LocalDate) {
        viewModelScope.launch {
            try {
                addExpenseUseCase(amount, categoryId, date)
                loadExpenses()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to add expense"
                )
            }
        }
    }

    fun editExpense(id: Long, amount: Double, categoryId: Long?, date: LocalDate) {
        viewModelScope.launch {
            try {
                editExpenseUseCase(id, amount, categoryId, date)
                loadExpenses()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to edit expense"
                )
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                deleteExpenseUseCase(expense)
                loadExpenses()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to delete expense"
                )
            }
        }
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return getExpenseByIdUseCase(id)
    }

    fun filterByDateRange(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                filterExpensesByDateUseCase(startDate, endDate)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error"
                        )
                    }
                    .collect { expenses ->
                        _uiState.value = _uiState.value.copy(
                            expenses = expenses,
                            isLoading = false
                        )
                    }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to filter expenses"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
