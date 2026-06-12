package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.domain.usecase.investment.AddInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.DeleteInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.EditInvestmentUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentGainLossUseCase
import com.example.expense_tracker.domain.usecase.investment.GetInvestmentsUseCase
import com.example.expense_tracker.domain.usecase.investment.UpdateInvestmentValueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// R-4: ViewModel for investment tracking screen — exposes immutable StateFlow<UiState>
@HiltViewModel
class InvestmentViewModel @Inject constructor(
    private val getInvestmentsUseCase: GetInvestmentsUseCase,
    private val addInvestmentUseCase: AddInvestmentUseCase,
    private val editInvestmentUseCase: EditInvestmentUseCase,
    private val deleteInvestmentUseCase: DeleteInvestmentUseCase,
    private val updateInvestmentValueUseCase: UpdateInvestmentValueUseCase,
    private val getInvestmentGainLossUseCase: GetInvestmentGainLossUseCase
) : ViewModel() {

    // R-4: Sealed state for the investment list screen
    sealed class UiState {
        object Loading : UiState()
        data class Success(
            val investments: List<Investment>,
            val gainLossSummary: GetInvestmentGainLossUseCase.GainLossSummary,
            val selectedAssetClass: AssetClass? = null
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    // R-4: Tracks which asset class filter is active (null = show all)
    private val _selectedAssetClass = MutableStateFlow<AssetClass?>(null)
    val selectedAssetClass: StateFlow<AssetClass?> = _selectedAssetClass

    // R-4: Combines investments list with gain/loss summary into a single UiState
    val uiState: StateFlow<UiState> = combine(
        getInvestmentsUseCase(),
        getInvestmentGainLossUseCase(),
        _selectedAssetClass
    ) { investments, gainLoss, filter ->
        val filtered = if (filter == null) investments else investments.filter { it.assetClass == filter }
        UiState.Success(
            investments = filtered,
            gainLossSummary = gainLoss,
            selectedAssetClass = filter
        ) as UiState
    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    // R-4: User action — add a new investment
    fun addInvestment(
        name: String,
        assetClass: AssetClass,
        investedAmount: Double,
        currentValue: Double,
        date: LocalDate
    ) {
        viewModelScope.launch {
            try {
                addInvestmentUseCase(name, assetClass, investedAmount, currentValue, date)
            } catch (e: Exception) {
                // Error surfaced via uiState; individual action errors emitted to errorMessage
                _errorMessage.value = e.message
            }
        }
    }

    // R-4: User action — edit an existing investment
    fun editInvestment(
        id: Long,
        name: String,
        assetClass: AssetClass,
        investedAmount: Double,
        currentValue: Double,
        date: LocalDate
    ) {
        viewModelScope.launch {
            try {
                editInvestmentUseCase(id, name, assetClass, investedAmount, currentValue, date)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    // R-4: User action — delete an investment
    fun deleteInvestment(investment: Investment) {
        viewModelScope.launch {
            try {
                deleteInvestmentUseCase(investment)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    // R-4: User action — update just the current value of an investment
    fun updateCurrentValue(id: Long, newValue: Double) {
        viewModelScope.launch {
            try {
                updateInvestmentValueUseCase(id, newValue)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    // R-4: User action — filter list by asset class (null = show all)
    fun filterByAssetClass(assetClass: AssetClass?) {
        _selectedAssetClass.value = assetClass
    }

    // Error state for mutation operations
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // R-4: Dismiss error
    fun clearError() {
        _errorMessage.value = null
    }
}
