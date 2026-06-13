package com.example.expense_tracker.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expense_tracker.domain.model.Loan
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.ln

@Composable
fun PrepaymentCalculatorPanel(
    loan: Loan?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Prepayment Calculator", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (loan == null) {
            Text(
                "Tap a loan card above to calculate prepayment scenarios.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        val monthlyRate = loan.interestRate / 100.0 / 12.0
        val currentTenureMonths = monthsToPayoff(loan.currentBalance, monthlyRate, loan.emi)

        // Summary row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryTile("Remaining balance", formatMoney(loan.currentBalance), Modifier.weight(1f))
            SummaryTile("Interest rate", "${String.format(Locale.US, "%.2f", loan.interestRate)}%", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryTile("Current EMI", formatMoney(loan.emi), Modifier.weight(1f))
            SummaryTile(
                "Remaining tenure",
                if (currentTenureMonths != null) formatTenure(currentTenureMonths) else "—",
                Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Prepayment inputs", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))

        var lumpSumText by remember(loan.id) { mutableStateOf("") }
        var annualPrepaymentText by remember(loan.id) { mutableStateOf("") }
        var emiIncreaseText by remember(loan.id) { mutableStateOf("") }

        OutlinedTextField(
            value = lumpSumText,
            onValueChange = { lumpSumText = it },
            label = { Text("Lump sum prepayment (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = annualPrepaymentText,
            onValueChange = { annualPrepaymentText = it },
            label = { Text("Annual prepayment (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = emiIncreaseText,
            onValueChange = { emiIncreaseText = it },
            label = { Text("EMI increase per month (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        val result by remember(loan.id, lumpSumText, annualPrepaymentText, emiIncreaseText) {
            derivedStateOf {
                calculatePrepayment(
                    balance = loan.currentBalance,
                    monthlyRate = monthlyRate,
                    emi = loan.emi,
                    lumpSum = lumpSumText.toDoubleOrNull() ?: 0.0,
                    annualPrepayment = annualPrepaymentText.toDoubleOrNull() ?: 0.0,
                    emiIncrease = emiIncreaseText.toDoubleOrNull() ?: 0.0
                )
            }
        }

        if (currentTenureMonths != null && loan.emi > 0.0) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Results", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryTile(
                    "New tenure",
                    formatTenure(result.newTenureMonths),
                    Modifier.weight(1f),
                    highlight = true
                )
                SummaryTile(
                    "Years saved",
                    formatTenure((currentTenureMonths - result.newTenureMonths).coerceAtLeast(0)),
                    Modifier.weight(1f),
                    highlight = true
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryTile(
                    "Interest saved",
                    formatMoney((result.baseInterest - result.newInterest).coerceAtLeast(0.0)),
                    Modifier.weight(1f),
                    highlight = true
                )
                SummaryTile(
                    "Total interest (new)",
                    formatMoney(result.newInterest.coerceAtLeast(0.0)),
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private data class PrepaymentResult(
    val newTenureMonths: Int,
    val newInterest: Double,
    val baseInterest: Double
)

private fun calculatePrepayment(
    balance: Double,
    monthlyRate: Double,
    emi: Double,
    lumpSum: Double,
    annualPrepayment: Double,
    emiIncrease: Double
): PrepaymentResult {
    val effectiveLump = lumpSum.coerceIn(0.0, balance)
    val newEmi = (emi + emiIncrease).coerceAtLeast(emi)
    val baseInterest = simulateTotalInterest(balance, monthlyRate, emi, 0.0)
    val newInterest = simulateTotalInterest(
        balance - effectiveLump, monthlyRate, newEmi, annualPrepayment
    )
    val newMonths = simulateMonths(balance - effectiveLump, monthlyRate, newEmi, annualPrepayment)
    return PrepaymentResult(newMonths, newInterest, baseInterest)
}

private fun simulateMonths(
    startBalance: Double,
    monthlyRate: Double,
    emi: Double,
    annualPrepayment: Double
): Int {
    if (startBalance <= 0.0) return 0
    if (emi <= 0.0) return Int.MAX_VALUE / 2
    var remaining = startBalance
    var months = 0
    var monthInYear = 0
    while (remaining > 0 && months < 9600) {
        val interest = remaining * monthlyRate
        remaining -= (emi - interest).coerceAtLeast(0.0)
        months++
        monthInYear++
        if (monthInYear == 12 && annualPrepayment > 0) {
            remaining -= annualPrepayment
            monthInYear = 0
        }
        if (remaining <= 0) break
    }
    return months
}

private fun simulateTotalInterest(
    startBalance: Double,
    monthlyRate: Double,
    emi: Double,
    annualPrepayment: Double
): Double {
    if (startBalance <= 0.0) return 0.0
    if (emi <= 0.0) return 0.0
    var remaining = startBalance
    var totalInterest = 0.0
    var months = 0
    var monthInYear = 0
    while (remaining > 0 && months < 9600) {
        val interest = remaining * monthlyRate
        totalInterest += interest
        remaining -= (emi - interest).coerceAtLeast(0.0)
        months++
        monthInYear++
        if (monthInYear == 12 && annualPrepayment > 0) {
            remaining -= annualPrepayment
            monthInYear = 0
        }
        if (remaining <= 0) break
    }
    return totalInterest
}

private fun monthsToPayoff(balance: Double, monthlyRate: Double, emi: Double): Int? {
    if (balance <= 0.0) return 0
    if (emi <= 0.0) return null
    if (monthlyRate <= 0.0) return ceil(balance / emi).toInt()
    if (emi <= balance * monthlyRate) return null
    val months = ln(emi / (emi - balance * monthlyRate)) / ln(1.0 + monthlyRate)
    return ceil(months).toInt()
}

private fun formatTenure(months: Int): String {
    val y = months / 12
    val m = months % 12
    return when {
        y == 0 -> "${m}m"
        m == 0 -> "${y}y"
        else -> "${y}y ${m}m"
    }
}

private fun formatMoney(amount: Double): String =
    "₹" + String.format(Locale.US, "%,.0f", amount)
