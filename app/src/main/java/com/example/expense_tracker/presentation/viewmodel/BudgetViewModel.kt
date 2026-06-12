package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.usecase.budget.AddBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.BudgetWithSpent
import com.example.expense_tracker.domain.usecase.budget.DeleteBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.EditBudgetUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetsUseCase
import com.example.expense_tracker.domain.usecase.budget.GetBudgetWithSpentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

// R-2: ViewModel for budget management screen — exposes immutable StateFlow<BudgetUiState>
data class BudgetUiState(
    val budgets: List<BudgetWithSpent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val editBudgetUseCase: EditBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val getBudgetWithSpentUseCase: GetBudgetWithSpentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadBudgets()
    }

    fun loadBudgets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getBudgetsUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
                .collect { budgets ->
                    val budgetsWithSpent = budgets.map { budget ->
                        val spent = getBudgetWithSpentUseCase(budget.categoryId, budget.monthYear)?.spent ?: 0.0
                        BudgetWithSpent(budget = budget, spent = spent)
                    }
                    _uiState.value = _uiState.value.copy(
                        budgets = budgetsWithSpent,
                        isLoading = false
                    )
                }
        }
    }

    fun addBudget(categoryId: Long, monthlyLimit: Double, monthYear: YearMonth) {
        viewModelScope.launch {
            try {
                addBudgetUseCase(categoryId, monthlyLimit, monthYear)
                loadBudgets()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to add budget"
                )
            }
        }
    }

    fun editBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                editBudgetUseCase(budget)
                loadBudgets()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to edit budget"
                )
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            try {
                deleteBudgetUseCase(budget)
                loadBudgets()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to delete budget"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
