package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.ai.ILlmInferenceHelper
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.usecase.loan.AddLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.DeleteLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.EditLoanUseCase
import com.example.expense_tracker.domain.usecase.loan.GenerateRepaymentIdeasUseCase
import com.example.expense_tracker.domain.usecase.loan.GetLoansUseCase
import com.example.expense_tracker.domain.usecase.loan.RepaymentIdea
import com.example.expense_tracker.domain.usecase.loan.UpdateLoanBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

// R-3: LLM loading state machine for the Loans screen AI chat panel
sealed class LlmState {
    // R-3: Initial state — LLM not yet started (model not downloaded)
    object Idle : LlmState()
    // R-3: Model is initializing; show spinner, hide chat input
    object Loading : LlmState()
    // R-3: Model loaded successfully; show chat input
    object Ready : LlmState()
    // R-3: Awaiting streamed response; disable send button
    object Generating : LlmState()
    // R-3: Initialization or inference failed; expose error message
    data class Error(val message: String) : LlmState()
}

// R-3: Represents a single message in the loan AI chat panel
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

// R-3: UI state for the Loans screen including CRUD list and LLM chat state
data class LoanUiState(
    val loans: List<Loan> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val llmState: LlmState = LlmState.Idle,
    val chatMessages: List<ChatMessage> = emptyList(),
    val streamingResponse: String = ""
)

// R-3: ViewModel for loan management; owns LlmInferenceHelper lifecycle and exposes StateFlow<LoanUiState>
@HiltViewModel
class LoanViewModel @Inject constructor(
    private val getLoansUseCase: GetLoansUseCase,
    private val addLoanUseCase: AddLoanUseCase,
    private val editLoanUseCase: EditLoanUseCase,
    private val deleteLoanUseCase: DeleteLoanUseCase,
    private val updateLoanBalanceUseCase: UpdateLoanBalanceUseCase,
    private val generateRepaymentIdeasUseCase: GenerateRepaymentIdeasUseCase,
    private val llmInferenceHelper: ILlmInferenceHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    init {
        loadLoans()
        initLlm()
    }

    // R-3: Collects the live loan list from the repository
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

    // R-3: Initializes the on-device LLM on a background thread; updates LlmState accordingly
    fun initLlm() {
        if (!llmInferenceHelper.isModelAvailable()) {
            _uiState.value = _uiState.value.copy(llmState = LlmState.Idle)
            return
        }
        _uiState.value = _uiState.value.copy(llmState = LlmState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                llmInferenceHelper.initialize(_uiState.value.loans)
                _uiState.value = _uiState.value.copy(llmState = LlmState.Ready)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    llmState = LlmState.Error(e.message ?: "Failed to initialize LLM")
                )
            }
        }
    }

    // R-3: Sends a user message to the LLM and streams the response into chatMessages
    fun sendMessage(userMessage: String) {
        val trimmed = userMessage.trim()
        if (trimmed.isBlank() || _uiState.value.llmState !is LlmState.Ready) return

        val updatedMessages = _uiState.value.chatMessages + ChatMessage(trimmed, isUser = true)
        _uiState.value = _uiState.value.copy(
            chatMessages = updatedMessages,
            llmState = LlmState.Generating,
            streamingResponse = ""
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var accumulated = ""
                llmInferenceHelper.generateResponse(trimmed).collect { token ->
                    accumulated += token
                    _uiState.value = _uiState.value.copy(streamingResponse = accumulated)
                }
                val finalMessages = _uiState.value.chatMessages +
                    ChatMessage(accumulated, isUser = false)
                _uiState.value = _uiState.value.copy(
                    chatMessages = finalMessages,
                    llmState = LlmState.Ready,
                    streamingResponse = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    llmState = LlmState.Error(e.message ?: "LLM inference failed")
                )
            }
        }
    }

    // R-3: Adds a new loan via use case; validation is performed inside the use case
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

    // R-3: Edits an existing loan via use case
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

    // R-3: Deletes a loan via use case
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

    // R-3: Updates the balance of a specific loan via use case
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

    // R-3: Returns rule-based repayment ideas for the given loan (non-LLM, always available)
    fun generateRepaymentIdeas(loan: Loan): List<RepaymentIdea> {
        return generateRepaymentIdeasUseCase(loan)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // R-3: Releases native LLM resources when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        llmInferenceHelper.release()
    }
}
