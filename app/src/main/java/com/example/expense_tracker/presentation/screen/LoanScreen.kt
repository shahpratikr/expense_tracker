package com.example.expense_tracker.presentation.screen

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import android.net.Uri
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.domain.model.Loan
import com.example.expense_tracker.domain.usecase.loan.RepaymentIdea
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.component.LoanCard
import com.example.expense_tracker.presentation.component.LoanChatPanel
import com.example.expense_tracker.presentation.viewmodel.LlmState
import com.example.expense_tracker.presentation.viewmodel.LoanViewModel
import java.io.File
import java.util.Locale

// R-3: URL for the one-time DeepSeek-R1-Distill-Qwen-1.5B model download (~1.8 GB, no auth required)
private const val MODEL_DOWNLOAD_URL =
    "https://huggingface.co/litert-community/DeepSeek-R1-Distill-Qwen-1.5B/resolve/main/deepseek_q8_ekv1280.task"

// R-3: Loan management screen — loan CRUD list plus LLM-powered repayment chat panel
@Composable
fun LoanScreen(viewModel: LoanViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var loanToEdit by remember { mutableStateOf<Loan?>(null) }
    var loanToUpdateBalance by remember { mutableStateOf<Loan?>(null) }
    var loanToView by remember { mutableStateOf<Loan?>(null) }

    val context = LocalContext.current
    val modelFile = remember { File(context.getExternalFilesDir(null) ?: context.filesDir, "deepseek_q8_ekv1280.task") }

    // R-3: Track download progress for the model download indicator
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadId by remember { mutableStateOf<Long?>(null) }
    var downloadError by remember { mutableStateOf<String?>(null) }

    // R-3: Register a BroadcastReceiver for DownloadManager completion events
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(id)
                    val cursor = dm.query(query)
                    var succeeded = false
                    var reason = ""
                    if (cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            succeeded = true
                        } else {
                            val col = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                            reason = if (col >= 0) "Error code: ${cursor.getInt(col)}" else "Unknown error"
                        }
                    }
                    cursor.close()
                    isDownloading = false
                    downloadProgress = 0f
                    downloadId = null
                    if (succeeded) {
                        viewModel.initLlm()
                    } else {
                        downloadError = "Download failed. $reason"
                    }
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

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
            // R-3: Loan list section
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
                                    onClick = { loanToView = loan }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            Divider()

            // R-3: Model download section — shown when model is not yet on device
            if (uiState.llmState is LlmState.Idle && !modelFile.exists()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (isDownloading) {
                        Text(
                            "Downloading AI model...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (downloadProgress > 0f) {
                            LinearProgressIndicator(
                                progress = downloadProgress,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    } else {
                        downloadError?.let { err ->
                            Text(
                                err,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            if (downloadError != null)
                                "Tap below to retry the download (~1.8 GB, requires Wi-Fi recommended)."
                            else
                                "Enable AI repayment advice by downloading the AI model (~1.8 GB). This is a one-time download.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                downloadError = null
                                // R-3: Trigger one-time model download via Android DownloadManager
                                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE)
                                    as DownloadManager
                                val request = DownloadManager.Request(Uri.parse(MODEL_DOWNLOAD_URL))
                                    .setTitle("Gemma 3 AI Model")
                                    .setDescription("Downloading on-device AI model for loan advice")
                                    .setDestinationInExternalFilesDir(
                                        context, null, "deepseek_q8_ekv1280.task"
                                    )
                                    .setNotificationVisibility(
                                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                                    )
                                    .setAllowedNetworkTypes(
                                        DownloadManager.Request.NETWORK_WIFI or
                                            DownloadManager.Request.NETWORK_MOBILE
                                    )
                                    .setAllowedOverRoaming(false)
                                downloadId = dm.enqueue(request)
                                isDownloading = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Download AI Model")
                        }
                    }
                }
            }

            // R-3: LLM chat panel — visible when model is loading, ready, generating, or in error
            if (uiState.llmState !is LlmState.Idle || modelFile.exists()) {
                LoanChatPanel(
                    llmState = uiState.llmState,
                    chatMessages = uiState.chatMessages,
                    streamingResponse = uiState.streamingResponse,
                    onSendMessage = { viewModel.sendMessage(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

    if (showAddDialog) {
        LoanFormDialog(
            title = "Add Loan",
            initialName = "",
            initialBalance = "",
            initialInterestRate = "",
            initialEmi = "",
            onConfirm = { name, balance, rate, emi ->
                viewModel.addLoan(name, balance, rate, emi)
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
            initialInterestRate = if (loan.interestRate > 0.0) String.format(Locale.US, "%.2f", loan.interestRate) else "",
            initialEmi = if (loan.emi > 0.0) String.format(Locale.US, "%.2f", loan.emi) else "",
            onConfirm = { name, balance, rate, emi ->
                viewModel.editLoan(
                    loan.copy(name = name, currentBalance = balance, interestRate = rate, emi = emi)
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

    loanToView?.let { loan ->
        val ideas = remember(loan) { viewModel.generateRepaymentIdeas(loan) }
        RepaymentIdeasDialog(
            loan = loan,
            ideas = ideas,
            onDismiss = { loanToView = null }
        )
    }
}

// R-3: Dialog showing rule-based repayment ideas for a selected loan
@Composable
fun RepaymentIdeasDialog(
    loan: Loan,
    ideas: List<RepaymentIdea>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(loan.name) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Remaining balance", style = MaterialTheme.typography.labelMedium)
                Text(
                    "₹${String.format(Locale.US, "%.2f", loan.currentBalance)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (loan.emi > 0.0 || loan.interestRate > 0.0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "EMI ₹${String.format(Locale.US, "%,.0f", loan.emi)} · " +
                            "Rate ${String.format(Locale.US, "%.2f", loan.interestRate)}% p.a.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Ideas to repay earlier", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                ideas.forEach { idea ->
                    Text(idea.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        idea.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// R-3: Form dialog for adding or editing a loan with all fields
@Composable
fun LoanFormDialog(
    title: String,
    initialName: String,
    initialBalance: String,
    initialInterestRate: String,
    initialEmi: String,
    onConfirm: (String, Double, Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var balance by remember { mutableStateOf(TextFieldValue(initialBalance)) }
    var interestRate by remember { mutableStateOf(TextFieldValue(initialInterestRate)) }
    var emi by remember { mutableStateOf(TextFieldValue(initialEmi)) }
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
                    value = emi,
                    onValueChange = { emi = it },
                    label = { Text("Current EMI (monthly payment)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    val rate = interestRate.text.ifBlank { "0" }.toDoubleOrNull()
                    val emiAmount = emi.text.ifBlank { "0" }.toDoubleOrNull()
                    when {
                        trimmedName.isBlank() -> validationError = "Name is required"
                        balanceAmount == null || balanceAmount < 0 -> validationError = "Enter a valid balance"
                        rate == null || rate < 0 -> validationError = "Enter a valid interest rate"
                        emiAmount == null || emiAmount < 0 -> validationError = "Enter a valid EMI"
                        else -> onConfirm(trimmedName, balanceAmount, rate, emiAmount)
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

// R-3: Dialog for manually updating the current balance of a loan after a payment
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
