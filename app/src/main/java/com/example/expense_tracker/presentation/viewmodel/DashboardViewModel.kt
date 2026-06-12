package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.usecase.dashboard.GetBudgetHeadroomUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetMonthlySpendingUseCase
import com.example.expense_tracker.domain.usecase.dashboard.GetTotalLoanBalanceUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

// R-5: UI state for the dashboard — all four financial summary metrics
data class DashboardUiState(
    val totalMonthlySpending: Double = 0.0,
    val totalLoanBalance: Double = 0.0,
    val totalInvestmentGainLossAmount: Double = 0.0,
    val totalInvestmentGainLossPercent: Double = 0.0,
    val budgetHeadroom: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

// R-5: ViewModel for the dashboard screen — aggregates all four financial domain summaries
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getMonthlySpendingUseCase: GetMonthlySpendingUseCase,
    private val getTotalLoanBalanceUseCase: GetTotalLoanBalanceUseCase,
    private val getInvestmentGainLossUseCase: GetInvestmentGainLossUseCase,
    private val getBudgetHeadroomUseCase: GetBudgetHeadroomUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // R-5: Load all four dashboard metrics concurrently on startup
        loadMonthlySpending()
        loadTotalLoanBalance()
        loadInvestmentGainLoss()
        loadBudgetHeadroom()
    }

    // R-5: Collect total monthly expense spending
    fun loadMonthlySpending() {
        viewModelScope.launch {
            getMonthlySpendingUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load spending"
                    )
                }
                .collect { total ->
                    _uiState.value = _uiState.value.copy(
                        totalMonthlySpending = total,
                        isLoading = false
                    )
                }
        }
    }

    // R-5: Collect total active loan balance across all loans
    fun loadTotalLoanBalance() {
        viewModelScope.launch {
            getTotalLoanBalanceUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to load loan balance"
                    )
                }
                .collect { total ->
                    _uiState.value = _uiState.value.copy(totalLoanBalance = total)
                }
        }
    }

    // R-5: Collect total investment portfolio gain/loss
    fun loadInvestmentGainLoss() {
        viewModelScope.launch {
            getInvestmentGainLossUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to load investment data"
                    )
                }
                .collect { summary ->
                    _uiState.value = _uiState.value.copy(
                        totalInvestmentGainLossAmount = summary.totalGainLossAmount,
                        totalInvestmentGainLossPercent = summary.totalGainLossPercent
                    )
                }
        }
    }

    // R-5: Collect remaining budget headroom for the current month
    fun loadBudgetHeadroom() {
        viewModelScope.launch {
            getBudgetHeadroomUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Failed to load budget headroom"
                    )
                }
                .collect { headroom ->
                    _uiState.value = _uiState.value.copy(budgetHeadroom = headroom)
                }
        }
    }

    // R-5: Clear displayed error message
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
