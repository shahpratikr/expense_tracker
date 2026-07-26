package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.usecase.dashboard.GetTotalLoanBalanceUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

// PRD Feature 3: UI state for the dashboard — loan balance and investment gain/loss metrics
data class DashboardUiState(
    val totalLoanBalance: Double = 0.0,
    val totalInvestmentGainLossAmount: Double = 0.0,
    val totalInvestmentGainLossPercent: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)

// PRD Feature 3: ViewModel for the dashboard screen — aggregates loan and investment summaries
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTotalLoanBalanceUseCase: GetTotalLoanBalanceUseCase,
    private val getInvestmentGainLossUseCase: GetInvestmentGainLossUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // PRD Feature 3: Load both dashboard metrics concurrently on startup
        loadTotalLoanBalance()
        loadInvestmentGainLoss()
    }

    // PRD Feature 3: Collect total active loan balance across all loans
    fun loadTotalLoanBalance() {
        viewModelScope.launch {
            getTotalLoanBalanceUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load loan balance"
                    )
                }
                .collect { total ->
                    _uiState.value = _uiState.value.copy(totalLoanBalance = total, isLoading = false)
                }
        }
    }

    // PRD Feature 3: Collect total investment portfolio gain/loss
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

    // PRD Feature 3: Clear displayed error message
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
