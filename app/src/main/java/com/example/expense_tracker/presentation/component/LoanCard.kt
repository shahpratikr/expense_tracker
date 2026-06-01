package com.example.expense_tracker.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expense_tracker.domain.model.Loan
import java.util.Locale

@Composable
fun LoanCard(
    loan: Loan,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateBalance: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(loan.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Balance: ₹${String.format(Locale.US, "%.2f", loan.currentBalance)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (loan.emi > 0.0 || loan.interestRate > 0.0) {
                        Text(
                            "EMI ₹${String.format(Locale.US, "%,.0f", loan.emi)} · " +
                                "${String.format(Locale.US, "%.2f", loan.interestRate)}% p.a.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit loan")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete loan")
                    }
                }
            }

            TextButton(onClick = onUpdateBalance) {
                Text("Update balance")
            }
        }
    }
}
