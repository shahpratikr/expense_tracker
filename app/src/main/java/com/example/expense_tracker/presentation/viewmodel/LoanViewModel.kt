package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
import com.example.expense_tracker.domain.usecase.loan.UpdateLoanBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoanUiState(
    val loans: List<Loan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLoanId: Long? = null
)

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val getLoansUseCase: GetLoansUseCase,
    private val addLoanUseCase: AddLoanUseCase,
    private val editLoanUseCase: EditLoanUseCase,
    private val deleteLoanUseCase: DeleteLoanUseCase,
    private val updateLoanBalanceUseCase: UpdateLoanBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        loadLoans()
    }

    fun loadLoans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getLoansUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
                .collect { loans ->
                    _uiState.value = _uiState.value.copy(
                        loans = loans,
                        isLoading = false
                    )
                }
        }
    }

    fun selectLoan(loan: Loan) {
        val newId = if (_uiState.value.selectedLoanId == loan.id) null else loan.id
        _uiState.value = _uiState.value.copy(selectedLoanId = newId)
    }

    fun addLoan(name: String, currentBalance: Double, interestRate: Double, emi: Double) {
        viewModelScope.launch {
            try {
                addLoanUseCase(name, currentBalance, interestRate, emi)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to add loan"
                )
            }
        }
    }

    fun editLoan(loan: Loan) {
        viewModelScope.launch {
            try {
                editLoanUseCase(loan)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to edit loan"
                )
            }
        }
    }

    fun deleteLoan(loan: Loan) {
        viewModelScope.launch {
            try {
                deleteLoanUseCase(loan)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to delete loan"
                )
            }
        }
    }

    fun updateLoanBalance(loanId: Long, newBalance: Double) {
        viewModelScope.launch {
            try {
                updateLoanBalanceUseCase(loanId, newBalance)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to update balance"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
