package com.example.expense_tracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.usecase.category.AddCategoryUseCase
import com.example.expense_tracker.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categories: List<ExpenseCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getCategoriesUseCase()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error"
                    )
                }
                .collect { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        isLoading = false
                    )
                }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            try {
                addCategoryUseCase(name)
                loadCategories()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = exception.message ?: "Failed to add category"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
