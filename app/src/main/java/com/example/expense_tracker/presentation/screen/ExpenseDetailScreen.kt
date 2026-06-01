package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.presentation.component.CategorySelector
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.CategoryViewModel
import com.example.expense_tracker.presentation.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ExpenseDetailScreen(
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    expenseId: Long? = null
) {
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()
    
    var amount by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var dateStr by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(expenseId) {
        if (expenseId != null) {
            val existing = expenseViewModel.getExpenseById(expenseId)
            if (existing != null) {
                amount = String.format(Locale.US, "%.2f", existing.amount)
                selectedCategoryId = existing.categoryId
                dateStr = existing.date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }
    }

    if (expenseUiState.error != null) {
        ErrorDialog(
            message = expenseUiState.error!!,
            onDismiss = { expenseViewModel.clearError() }
        )
    }

    if (errorMessage != null) {
        ErrorDialog(
            message = errorMessage!!,
            onDismiss = { errorMessage = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId == null) "Add Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = dateStr,
                onValueChange = { dateStr = it },
                label = { Text("Date (yyyy-MM-dd)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Text("Category", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
            CategorySelector(
                categories = categoryUiState.categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { selectedCategoryId = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    try {
                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue == null || amountValue <= 0) {
                            errorMessage = "Amount must be greater than 0"
                            return@Button
                        }
                        val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                        if (expenseId == null) {
                            expenseViewModel.addExpense(amountValue, selectedCategoryId, date)
                        } else {
                            expenseViewModel.editExpense(expenseId, amountValue, selectedCategoryId, date)
                        }
                        onBackClick()
                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Invalid input"
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            ) {
                Text(if (expenseId == null) "Add" else "Update")
            }
        }
    }
}
