package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.expense_tracker.presentation.component.ErrorDialog
import com.example.expense_tracker.presentation.viewmodel.DashboardViewModel
import java.util.Locale

// PRD Feature 3: Dashboard screen displaying loan and investment summary metrics — each tappable
@Composable
fun DashboardScreen(
    onLoanClick: () -> Unit = {},
    onInvestmentClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // PRD Feature 3: Error handling UI — show dialog for any dashboard data loading errors
    uiState.error?.let { message ->
        ErrorDialog(message = message, onDismiss = { viewModel.clearError() })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // PRD Feature 3: Total loan balance metric — tappable, navigates to loans screen
                    DashboardMetricCard(
                        title = "Total Loan Balance",
                        value = "₹${String.format(Locale.US, "%.2f", uiState.totalLoanBalance)}",
                        subtitle = "Tap to manage loans",
                        onClick = onLoanClick
                    )

                    // PRD Feature 3: Investment gain/loss metric — tappable, navigates to investments screen
                    val gainLossSign = if (uiState.totalInvestmentGainLossAmount >= 0) "+" else ""
                    val gainLossColor = if (uiState.totalInvestmentGainLossAmount >= 0)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.error
                    DashboardMetricCard(
                        title = "Investment Gain / Loss",
                        value = "$gainLossSign₹${String.format(Locale.US, "%.2f", uiState.totalInvestmentGainLossAmount)}" +
                            " (${gainLossSign}${String.format(Locale.US, "%.1f", uiState.totalInvestmentGainLossPercent)}%)",
                        subtitle = "Tap to view investments",
                        valueColor = gainLossColor,
                        onClick = onInvestmentClick
                    )
                }
            }
        }
    }
}

// PRD Feature 3: Reusable metric card composable — displays a labelled financial figure, tappable for navigation
@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
