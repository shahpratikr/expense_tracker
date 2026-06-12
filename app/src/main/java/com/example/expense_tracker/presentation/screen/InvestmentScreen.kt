package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.AssetClass
import com.example.expense_tracker.domain.model.Investment
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.component.InvestmentCard
import com.example.expense_tracker.presentation.viewmodel.InvestmentViewModel
import java.time.LocalDate

// R-4: Investment list screen with asset class filter, CRUD, and portfolio gain/loss summary
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(
    viewModel: InvestmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingInvestment by remember { mutableStateOf<Investment?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Investments") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add investment")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is InvestmentViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is InvestmentViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is InvestmentViewModel.UiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // R-4: Portfolio gain/loss summary
                    val summary = state.gainLossSummary
                    val summaryColor = if (summary.totalGainLossAmount >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    val sign = if (summary.totalGainLossAmount >= 0) "+" else ""
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Portfolio Summary",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Invested: ₹${"%.2f".format(summary.totalInvested)}")
                            Text("Current: ₹${"%.2f".format(summary.totalCurrentValue)}")
                        }
                        Text(
                            text = "Total P&L: ${sign}₹${"%.2f".format(summary.totalGainLossAmount)} " +
                                "(${sign}${"%.2f".format(summary.totalGainLossPercent)}%)",
                            color = summaryColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // R-4: Asset class filter chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedAssetClass == null,
                            onClick = { viewModel.filterByAssetClass(null) },
                            label = { Text("All") }
                        )
                        AssetClass.values().forEach { ac ->
                            FilterChip(
                                selected = state.selectedAssetClass == ac,
                                onClick = { viewModel.filterByAssetClass(ac) },
                                label = { Text(ac.name.replace('_', ' ')) }
                            )
                        }
                    }

                    // R-4: Investment list
                    LazyColumn {
                        if (state.investments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No investments found")
                                }
                            }
                        }
                        items(state.investments) { investment ->
                            InvestmentCard(
                                investment = investment,
                                onEditClick = { editingInvestment = it },
                                onDeleteClick = { viewModel.deleteInvestment(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // R-4: Add investment dialog
    if (showAddDialog) {
        InvestmentFormDialog(
            title = "Add Investment",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, ac, invested, current, date ->
                viewModel.addInvestment(name, ac, invested, current, date)
                showAddDialog = false
            }
        )
    }

    // R-4: Edit investment dialog
    editingInvestment?.let { inv ->
        InvestmentFormDialog(
            title = "Edit Investment",
            initialName = inv.name,
            initialAssetClass = inv.assetClass,
            initialInvested = inv.investedAmount.toString(),
            initialCurrent = inv.currentValue.toString(),
            initialDate = inv.date,
            onDismiss = { editingInvestment = null },
            onConfirm = { name, ac, invested, current, date ->
                viewModel.editInvestment(inv.id, name, ac, invested, current, date)
                editingInvestment = null
            }
        )
    }

    // Error dialog for mutation failures
    errorMessage?.let { msg ->
        ErrorDialog(message = msg, onDismiss = { viewModel.clearError() })
    }
}

// R-4: Reusable add/edit form dialog for investment records
@Composable
private fun InvestmentFormDialog(
    title: String,
    initialName: String = "",
    initialAssetClass: AssetClass = AssetClass.STOCKS,
    initialInvested: String = "",
    initialCurrent: String = "",
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onConfirm: (String, AssetClass, Double, Double, LocalDate) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedAssetClass by remember { mutableStateOf(initialAssetClass) }
    var investedText by remember { mutableStateOf(initialInvested) }
    var currentText by remember { mutableStateOf(initialCurrent) }
    var dateText by remember { mutableStateOf(initialDate.toString()) }
    var formError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                // R-4: Asset class dropdown using filter chips in a scrollable row
                Text("Asset Class", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AssetClass.values().forEach { ac ->
                        FilterChip(
                            selected = selectedAssetClass == ac,
                            onClick = { selectedAssetClass = ac },
                            label = { Text(ac.name.replace('_', ' '), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = investedText,
                    onValueChange = { investedText = it },
                    label = { Text("Invested Amount (₹) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = currentText,
                    onValueChange = { currentText = it },
                    label = { Text("Current Value (₹) *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth()
                )

                formError?.let { err ->
                    Text(text = err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val invested = investedText.toDoubleOrNull()
                val current = currentText.toDoubleOrNull()
                val parsedDate = runCatching { LocalDate.parse(dateText) }.getOrNull()

                when {
                    name.isBlank() -> formError = "Name is required"
                    invested == null || invested <= 0 -> formError = "Invested amount must be > 0"
                    current == null || current < 0 -> formError = "Current value must be >= 0"
                    parsedDate == null -> formError = "Date must be in YYYY-MM-DD format"
                    else -> onConfirm(name, selectedAssetClass, invested, current, parsedDate)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
