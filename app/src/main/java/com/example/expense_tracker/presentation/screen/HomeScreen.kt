package com.example.expense_tracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onExpenseListClick: () -> Unit = {},
    onCategoryManagementClick: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onLoanClick: () -> Unit = {},
    // R-4: Navigate to investment tracking
    onInvestmentClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Finance Tracker") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onExpenseListClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Expenses")
            }

            Button(
                onClick = onCategoryManagementClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Categories")
            }

            Button(
                onClick = onDashboardClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Dashboard")
            }

            Button(
                onClick = onBudgetClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Budgets")
            }

            Button(
                onClick = onLoanClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Loans")
            }

            // R-4: Investments navigation button
            Button(
                onClick = onInvestmentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Investments")
            }
        }
    }
}
