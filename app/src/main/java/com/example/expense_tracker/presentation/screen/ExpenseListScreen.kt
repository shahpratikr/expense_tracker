package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.Expense
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.component.ExpenseItem
import com.example.expense_tracker.presentation.viewmodel.CategoryViewModel
import com.example.expense_tracker.presentation.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseListScreen(
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    onExpenseClick: (Expense) -> Unit = {},
    onAddExpenseClick: () -> Unit = {}
) {
    val expenseUiState by expenseViewModel.uiState.collectAsState()
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    if (expenseUiState.error != null) {
        ErrorDialog(
            message = expenseUiState.error!!,
            onDismiss = { expenseViewModel.clearError() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Expenses") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpenseClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (yyyy-MM-dd)") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (yyyy-MM-dd)") },
                    modifier = Modifier.weight(1f)
                )
            }
            if (startDate.isNotBlank() && endDate.isNotBlank()) {
                try {
                    val start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    val end = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    expenseViewModel.filterByDateRange(start, end)
                } catch (e: Exception) {
                    // Invalid date format, ignore
                }
            }

            if (expenseUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (expenseUiState.expenses.isEmpty()) {
                Text("No expenses found", modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(expenseUiState.expenses) { expense ->
                        val categoryName = categoryUiState.categories.find { it.id == expense.categoryId }?.name ?: "Uncategorized"
                        ExpenseItem(
                            expense = expense,
                            categoryName = categoryName,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onExpenseClick(expense) }
                        )
                    }
                }
            }
        }
    }
}
