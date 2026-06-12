package com.example.expense_tracker.presentation.component

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expense_tracker.domain.model.Investment

// R-4: Reusable card component displaying invested amount, current value, and gain/loss (₹ and %)
@Composable
fun InvestmentCard(
    investment: Investment,
    onEditClick: (Investment) -> Unit,
    onDeleteClick: (Investment) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = investment.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = investment.assetClass.name.replace('_', ' '),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row {
                    IconButton(onClick = { onEditClick(investment) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit investment")
                    }
                    IconButton(onClick = { onDeleteClick(investment) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete investment")
                    }
                }
            }

            // R-4: Invested amount and current value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Invested: ₹${"%.2f".format(investment.investedAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Current: ₹${"%.2f".format(investment.currentValue)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // R-4: Gain/loss in ₹ and % — green for gain, red for loss
            val gainLossColor = if (investment.gainLossAmount >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            val gainLossSign = if (investment.gainLossAmount >= 0) "+" else ""
            Text(
                text = "P&L: ${gainLossSign}₹${"%.2f".format(investment.gainLossAmount)} " +
                    "(${gainLossSign}${"%.2f".format(investment.gainLossPercent)}%)",
                style = MaterialTheme.typography.bodyMedium,
                color = gainLossColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
