package com.example.expense_tracker.presentation.screen

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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.Budget
import com.example.expense_tracker.domain.model.ExpenseCategory
import com.example.expense_tracker.domain.usecase.budget.BudgetWithSpent
import com.example.expense_tracker.presentation.component.CategorySelector
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.BudgetViewModel
import com.example.expense_tracker.presentation.viewmodel.CategoryViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthLabelFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

private fun formatCurrency(value: Double): String = "₹" + String.format(Locale.US, "%.2f", value)

@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryUiState by categoryViewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MonthSelector(
                month = selectedMonth,
                onPrevious = { selectedMonth = selectedMonth.minusMonths(1) },
                onNext = { selectedMonth = selectedMonth.plusMonths(1) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            val budgetsForMonth = uiState.budgets.filter { it.budget.monthYear == selectedMonth }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    budgetsForMonth.isEmpty() -> {
                        Text(
                            "No budgets for this month. Tap + to add one.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(budgetsForMonth) { budgetWithSpent ->
                                val categoryName = categoryUiState.categories
                                    .find { it.id == budgetWithSpent.budget.categoryId }?.name
                                    ?: "Unknown category"
                                BudgetItem(
                                    budgetWithSpent = budgetWithSpent,
                                    categoryName = categoryName,
                                    onEdit = { editingBudget = budgetWithSpent.budget },
                                    onDelete = { viewModel.deleteBudget(budgetWithSpent.budget) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        BudgetDialog(
            title = "Add Budget",
            categories = categoryUiState.categories,
            initialCategoryId = null,
            initialLimit = "",
            categoryEditable = true,
            onConfirm = { categoryId, limit ->
                viewModel.addBudget(categoryId, limit, selectedMonth)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingBudget?.let { budget ->
        BudgetDialog(
            title = "Edit Budget",
            categories = categoryUiState.categories,
            initialCategoryId = budget.categoryId,
            initialLimit = String.format(Locale.US, "%.2f", budget.monthlyLimit),
            categoryEditable = false,
            onConfirm = { _, limit ->
                viewModel.editBudget(budget.copy(monthlyLimit = limit))
                editingBudget = null
            },
            onDismiss = { editingBudget = null }
        )
    }
}

@Composable
fun MonthSelector(month: YearMonth, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(month.format(monthLabelFormatter), style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
fun BudgetItem(
    budgetWithSpent: BudgetWithSpent,
    categoryName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val limit = budgetWithSpent.budget.monthlyLimit
    val spent = budgetWithSpent.spent
    val overBudget = spent > limit
    val fraction = if (limit > 0) (spent / limit).coerceIn(0.0, 1.0).toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = fraction,
                modifier = Modifier.fillMaxWidth(),
                color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Spent ${formatCurrency(spent)} of ${formatCurrency(limit)}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            if (overBudget) {
                Text(
                    text = "Over budget by ${formatCurrency(spent - limit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun BudgetDialog(
    title: String,
    categories: List<ExpenseCategory>,
    initialCategoryId: Long?,
    initialLimit: String,
    categoryEditable: Boolean,
    onConfirm: (Long, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    var limit by remember { mutableStateOf(initialLimit) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                if (categoryEditable) {
                    CategorySelector(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = { selectedCategoryId = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val name = categories.find { it.id == selectedCategoryId }?.name ?: "Unknown category"
                    Text("Category: $name", style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Monthly Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
                validationError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val categoryId = selectedCategoryId
                    val limitAmount = limit.toDoubleOrNull()
                    when {
                        categoryId == null -> validationError = "Please select a category"
                        limitAmount == null || limitAmount <= 0 -> validationError = "Limit must be greater than 0"
                        else -> onConfirm(categoryId, limitAmount)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
