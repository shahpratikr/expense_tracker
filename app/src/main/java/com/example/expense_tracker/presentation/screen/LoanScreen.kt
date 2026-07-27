package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.component.LoanCard
import com.example.expense_tracker.presentation.component.PrepaymentCalculatorPanel
import com.example.expense_tracker.presentation.viewmodel.LoanViewModel
import java.time.LocalDate
import java.util.Locale
import kotlin.math.pow

// PRD Feature 1: Loans screen — CRUD list, auto-recalculation on load, and prepayment calculator
@Composable
fun LoanScreen(viewModel: LoanViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var loanToEdit by remember { mutableStateOf<Loan?>(null) }
    var loanToUpdateBalance by remember { mutableStateOf<Loan?>(null) }

    val selectedLoan = uiState.loans.find { it.id == uiState.selectedLoanId }

    uiState.error?.let { message ->
        ErrorDialog(message = message, onDismiss = { viewModel.clearError() })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Loans") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add loan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.loans.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No loans. Tap + to add one.")
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(uiState.loans) { loan ->
                                LoanCard(
                                    loan = loan,
                                    onEdit = { loanToEdit = loan },
                                    onDelete = { viewModel.deleteLoan(loan) },
                                    onUpdateBalance = { loanToUpdateBalance = loan },
                                    onClick = { viewModel.selectLoan(loan) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            Divider()

            PrepaymentCalculatorPanel(
                loan = selectedLoan,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }

    if (showAddDialog) {
        AddLoanFormDialog(
            onConfirm = { name, balance, rate, emiAmount, startDate, emiDay, lastBalanceUpdateDate ->
                viewModel.addLoan(name, balance, rate, emiAmount, startDate, emiDay, lastBalanceUpdateDate)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    loanToEdit?.let { loan ->
        LoanFormDialog(
            title = "Edit Loan",
            initialName = loan.name,
            initialBalance = String.format(Locale.US, "%.2f", loan.currentBalance),
            initialInterestRate = String.format(Locale.US, "%.2f", loan.interestRate),
            initialEmiAmount = String.format(Locale.US, "%.2f", loan.emiAmount),
            initialLoanStartDate = loan.loanStartDate.toString(),
            initialEmiDayOfMonth = loan.emiDayOfMonth.toString(),
            onConfirm = { name, balance, rate, emiAmount, startDate, emiDay ->
                viewModel.editLoan(
                    loan.copy(
                        name = name,
                        currentBalance = balance,
                        interestRate = rate,
                        emiAmount = emiAmount,
                        loanStartDate = startDate,
                        emiDayOfMonth = emiDay
                    )
                )
                loanToEdit = null
            },
            onDismiss = { loanToEdit = null }
        )
    }

    loanToUpdateBalance?.let { loan ->
        UpdateBalanceDialog(
            loanName = loan.name,
            initialBalance = String.format(Locale.US, "%.2f", loan.currentBalance),
            onConfirm = { newBalance ->
                viewModel.updateLoanBalance(loan.id, newBalance)
                loanToUpdateBalance = null
            },
            onDismiss = { loanToUpdateBalance = null }
        )
    }
}

// Standard EMI formula: P * r * (1+r)^n / ((1+r)^n - 1), used to suggest an EMI from principal/tenure/rate
private fun calculateStandardEmi(principal: Double, interestRate: Double, tenureMonths: Int): Double {
    if (principal <= 0 || tenureMonths <= 0) return 0.0
    val monthlyRate = interestRate / 12.0 / 100.0
    if (monthlyRate <= 0.0) return principal / tenureMonths
    val factor = (1 + monthlyRate).pow(tenureMonths)
    return principal * monthlyRate * factor / (factor - 1)
}

// Result of simulating principal amortization up to today: the derived balance, and the last EMI
// date that was actually applied (must become the loan's lastBalanceUpdateDate so
// RecalculateLoanBalancesUseCase doesn't replay these same cycles again on the next screen load).
private data class DerivedBalance(val balance: Double, val lastAppliedDate: LocalDate)

// Derives current balance from the original principal by applying one amortization cycle for every
// EMI date elapsed since loanStartDate, mirroring RecalculateLoanBalancesUseCase's cycle math.
// Returns null if the EMI amount can't cover accrued interest (negative amortization).
private fun deriveCurrentBalanceFromPrincipal(
    principal: Double,
    interestRate: Double,
    emiAmount: Double,
    loanStartDate: LocalDate,
    emiDayOfMonth: Int,
    today: LocalDate = LocalDate.now()
): DerivedBalance? {
    val monthlyRate = interestRate / 12.0 / 100.0
    var balance = principal
    var lastAppliedDate = loanStartDate
    var cursor = loanStartDate.withDayOfMonth(minOf(emiDayOfMonth, loanStartDate.lengthOfMonth()))
    if (!cursor.isAfter(loanStartDate)) {
        cursor = cursor.plusMonths(1)
    }
    while (!cursor.isAfter(today)) {
        val emiDate = cursor.withDayOfMonth(minOf(emiDayOfMonth, cursor.lengthOfMonth()))
        if (emiDate.isAfter(loanStartDate) && !emiDate.isAfter(today)) {
            val interest = balance * monthlyRate
            if (emiAmount <= interest) return null
            balance = (balance - (emiAmount - interest)).coerceAtLeast(0.0)
            lastAppliedDate = emiDate
        }
        cursor = cursor.plusMonths(1)
    }
    return DerivedBalance(balance, lastAppliedDate)
}

// PRD Feature 1 enhancement: Add-loan form collects total loan amount + loan period instead of a
// direct balance entry; EMI is auto-suggested from those inputs (still user-editable) and the
// starting current balance is derived by simulating elapsed EMI cycles since the start date.
@Composable
fun AddLoanFormDialog(
    onConfirm: (String, Double, Double, Double, LocalDate, Int, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var totalLoanAmount by remember { mutableStateOf(TextFieldValue("")) }
    var loanPeriodMonths by remember { mutableStateOf(TextFieldValue("")) }
    var interestRate by remember { mutableStateOf(TextFieldValue("")) }
    var emiAmount by remember { mutableStateOf(TextFieldValue("")) }
    var loanStartDate by remember { mutableStateOf(TextFieldValue(LocalDate.now().toString())) }
    var emiDayOfMonth by remember { mutableStateOf(TextFieldValue("1")) }
    var lastSuggestedEmi by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(totalLoanAmount.text, loanPeriodMonths.text, interestRate.text) {
        val principal = totalLoanAmount.text.toDoubleOrNull()
        val tenure = loanPeriodMonths.text.toIntOrNull()
        val rate = interestRate.text.toDoubleOrNull()
        if (principal != null && principal > 0 && tenure != null && tenure > 0 && rate != null && rate > 0) {
            val suggested = String.format(Locale.US, "%.2f", calculateStandardEmi(principal, rate, tenure))
            // Only overwrite the EMI field if the user hasn't typed their own override since the last suggestion
            if (emiAmount.text.isBlank() || emiAmount.text == lastSuggestedEmi) {
                emiAmount = TextFieldValue(suggested)
            }
            lastSuggestedEmi = suggested
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Loan") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = totalLoanAmount,
                    onValueChange = { totalLoanAmount = it },
                    label = { Text("Total Loan Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = loanPeriodMonths,
                    onValueChange = { loanPeriodMonths = it },
                    label = { Text("Loan Period (months)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Interest Rate (% per year)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = emiAmount,
                    onValueChange = { emiAmount = it },
                    label = { Text("EMI amount (auto-calculated, editable)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = loanStartDate,
                    onValueChange = { loanStartDate = it },
                    label = { Text("Loan start date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = emiDayOfMonth,
                    onValueChange = { emiDayOfMonth = it },
                    label = { Text("EMI date (day of month, 1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    val trimmedName = name.text.trim()
                    val principal = totalLoanAmount.text.toDoubleOrNull()
                    val tenure = loanPeriodMonths.text.toIntOrNull()
                    val rate = interestRate.text.toDoubleOrNull()
                    val emi = emiAmount.text.toDoubleOrNull()
                    val startDate = runCatching { LocalDate.parse(loanStartDate.text.trim()) }.getOrNull()
                    val emiDay = emiDayOfMonth.text.toIntOrNull()
                    when {
                        trimmedName.isBlank() -> validationError = "Name is required"
                        principal == null || principal <= 0 -> validationError = "Enter a valid total loan amount"
                        tenure == null || tenure <= 0 -> validationError = "Enter a valid loan period"
                        rate == null || rate <= 0 -> validationError = "Enter a valid interest rate"
                        emi == null || emi <= 0 -> validationError = "Enter a valid EMI amount"
                        startDate == null -> validationError = "Enter a valid loan start date"
                        emiDay == null || emiDay !in 1..31 -> validationError = "EMI day must be between 1 and 31"
                        else -> {
                            val derived = deriveCurrentBalanceFromPrincipal(principal, rate, emi, startDate, emiDay)
                            if (derived == null) {
                                validationError = "EMI amount is too low to cover accrued interest at this rate"
                            } else {
                                onConfirm(trimmedName, derived.balance, rate, emi, startDate, emiDay, derived.lastAppliedDate)
                            }
                        }
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

// PRD Feature 1: Add/edit loan form — name, balance, interest rate, EMI amount, start date, EMI day
@Composable
fun LoanFormDialog(
    title: String,
    initialName: String,
    initialBalance: String,
    initialInterestRate: String,
    initialEmiAmount: String,
    initialLoanStartDate: String,
    initialEmiDayOfMonth: String,
    onConfirm: (String, Double, Double, Double, LocalDate, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var balance by remember { mutableStateOf(TextFieldValue(initialBalance)) }
    var interestRate by remember { mutableStateOf(TextFieldValue(initialInterestRate)) }
    var emiAmount by remember { mutableStateOf(TextFieldValue(initialEmiAmount)) }
    var loanStartDate by remember { mutableStateOf(TextFieldValue(initialLoanStartDate)) }
    var emiDayOfMonth by remember { mutableStateOf(TextFieldValue(initialEmiDayOfMonth)) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("Current Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Interest Rate (% per year)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = emiAmount,
                    onValueChange = { emiAmount = it },
                    label = { Text("EMI amount (fixed monthly payment)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = loanStartDate,
                    onValueChange = { loanStartDate = it },
                    label = { Text("Loan start date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = emiDayOfMonth,
                    onValueChange = { emiDayOfMonth = it },
                    label = { Text("EMI date (day of month, 1-31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    val trimmedName = name.text.trim()
                    val balanceAmount = balance.text.toDoubleOrNull()
                    val rate = interestRate.text.toDoubleOrNull()
                    val emi = emiAmount.text.toDoubleOrNull()
                    val startDate = runCatching { LocalDate.parse(loanStartDate.text.trim()) }.getOrNull()
                    val emiDay = emiDayOfMonth.text.toIntOrNull()
                    when {
                        trimmedName.isBlank() -> validationError = "Name is required"
                        balanceAmount == null || balanceAmount < 0 -> validationError = "Enter a valid balance"
                        rate == null || rate <= 0 -> validationError = "Enter a valid interest rate"
                        emi == null || emi <= 0 -> validationError = "Enter a valid EMI amount"
                        startDate == null -> validationError = "Enter a valid loan start date"
                        emiDay == null || emiDay !in 1..31 -> validationError = "EMI day must be between 1 and 31"
                        else -> onConfirm(trimmedName, balanceAmount, rate, emi, startDate, emiDay)
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

// PRD Feature 1: Manual balance override/correction dialog
@Composable
fun UpdateBalanceDialog(
    loanName: String,
    initialBalance: String,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var balance by remember { mutableStateOf(TextFieldValue(initialBalance)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Balance") },
        text = {
            Column {
                Text(loanName)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("New Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val balanceAmount = balance.text.toDoubleOrNull() ?: 0.0
                    if (balanceAmount >= 0) {
                        onConfirm(balanceAmount)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
