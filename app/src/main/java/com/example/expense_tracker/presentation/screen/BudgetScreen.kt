package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.BudgetViewModel
import java.time.YearMonth

@Composable
fun BudgetScreen(viewModel: BudgetViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    uiState.error?.let { message ->
        ErrorDialog(message = message, onDismiss = { viewModel.clearError() })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Budget Management") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add budget")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.budgets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No budgets. Tap + to add one.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Month: ${selectedMonth}", style = MaterialTheme.typography.bodyLarge)
                    }

                    LazyColumn {
                        items(uiState.budgets.filter { it.monthYear == selectedMonth }) { budget ->
                            BudgetItem(
                                budget = budget,
                                onEdit = { showAddDialog = true },
                                onDelete = { viewModel.deleteBudget(budget) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            onConfirm = { categoryId, limit ->
                viewModel.addBudget(categoryId, limit, selectedMonth)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun BudgetItem(budget: Budget, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Category ID: ${budget.categoryId}", style = MaterialTheme.typography.labelSmall)
                Text(
                    "Budget: ₹${String.format("%.2f", budget.monthlyLimit)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddBudgetDialog(onConfirm: (Long, Double) -> Unit, onDismiss: () -> Unit) {
    var categoryId by remember { mutableStateOf(TextFieldValue("")) }
    var limit by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Budget") },
        text = {
            Column {
                TextField(
                    value = categoryId,
                    onValueChange = { categoryId = it },
                    label = { Text("Category ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Monthly Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val catId = categoryId.text.toLongOrNull() ?: 0L
                    val limitAmount = limit.text.toDoubleOrNull() ?: 0.0
                    if (catId > 0 && limitAmount > 0) {
                        onConfirm(catId, limitAmount)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
