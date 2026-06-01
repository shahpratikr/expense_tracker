package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.roundToLong
import kotlin.random.Random

data class RepaymentIdea(
    val title: String,
    val detail: String
)

/**
 * Generates reasonable, math-based repayment suggestions for a loan using its current balance,
 * annual interest rate and EMI (monthly payment). Suggestions are built from standard
 * amortization formulas (months-to-payoff and interest paid), with a couple of scenarios chosen
 * at random so each time the user opens a loan they see a slightly different set.
 */
class GenerateRepaymentIdeasUseCase(private val random: Random = Random.Default) {

    operator fun invoke(loan: Loan): List<RepaymentIdea> {
        val balance = loan.currentBalance
        if (balance <= 0.0) {
            return listOf(
                RepaymentIdea(
                    title = "Loan cleared",
                    detail = "There's no remaining balance on this loan. Keep it up!"
                )
            )
        }

        val emi = loan.emi
        if (emi <= 0.0) {
            return listOf(
                RepaymentIdea(
                    title = "Add your EMI & interest rate",
                    detail = "Edit this loan to enter your current monthly payment (EMI) and interest rate, " +
                        "and you'll get tailored ways to repay it faster and save on interest."
                )
            )
        }

        val monthlyRate = loan.interestRate / 100.0 / 12.0
        val interestPerMonth = balance * monthlyRate

        // EMI must at least exceed the monthly interest, otherwise the balance never reduces.
        if (monthlyRate > 0.0 && emi <= interestPerMonth) {
            val suggested = roundTo(interestPerMonth + balance / 24.0, 100.0)
            return listOf(
                RepaymentIdea(
                    title = "Your EMI is too low",
                    detail = "At ${money(emi)}/month your payment barely covers the ${money(interestPerMonth)} " +
                        "monthly interest, so the balance hardly shrinks. Pay at least about ${money(suggested)}/month " +
                        "to clear it in roughly 2 years."
                )
            )
        }

        val baseMonths = monthsToPayoff(balance, monthlyRate, emi) ?: return emptyList()
        val baseInterest = (emi * baseMonths - balance).coerceAtLeast(0.0)

        val ideas = mutableListOf<RepaymentIdea>()

        ideas += RepaymentIdea(
            title = "Your current pace",
            detail = "Paying ${money(emi)}/month clears this loan in about $baseMonths months, " +
                "with roughly ${money(baseInterest)} paid in interest."
        )

        // Two "increase the EMI" scenarios chosen at random.
        val extraPercents = listOf(0.10, 0.20, 0.30, 0.50).shuffled(random).take(2).sorted()
        extraPercents.forEach { pct ->
            val extra = roundTo(emi * pct, 100.0).coerceAtLeast(100.0)
            val newEmi = emi + extra
            val months = monthsToPayoff(balance, monthlyRate, newEmi) ?: baseMonths
            val interest = (newEmi * months - balance).coerceAtLeast(0.0)
            val monthsSaved = (baseMonths - months).coerceAtLeast(0)
            val interestSaved = (baseInterest - interest).coerceAtLeast(0.0)
            ideas += RepaymentIdea(
                title = "Pay ${money(extra)} more each month",
                detail = "Raising your EMI to ${money(newEmi)} clears the loan in about $months months — " +
                    "around $monthsSaved months sooner and about ${money(interestSaved)} less interest."
            )
        }

        // One lump-sum scenario.
        val lump = roundTo(maxOf(emi * 3, balance * 0.10), 500.0).coerceIn(0.0, balance)
        if (lump > 0.0) {
            val newBalance = (balance - lump).coerceAtLeast(0.0)
            val months = if (newBalance <= 0.0) 0 else monthsToPayoff(newBalance, monthlyRate, emi) ?: baseMonths
            val interest = (emi * months - newBalance).coerceAtLeast(0.0)
            val monthsSaved = (baseMonths - months).coerceAtLeast(0)
            val interestSaved = (baseInterest - interest).coerceAtLeast(0.0)
            ideas += RepaymentIdea(
                title = "Make a one-time ${money(lump)} payment",
                detail = "A lump sum of ${money(lump)} now would finish the loan about $monthsSaved months earlier " +
                    "and save roughly ${money(interestSaved)} in interest."
            )
        }

        return ideas
    }

    /**
     * Number of whole monthly payments needed to clear [balance] at [monthlyRate] paying [payment].
     * Returns null if the payment can never amortize the balance.
     */
    private fun monthsToPayoff(balance: Double, monthlyRate: Double, payment: Double): Int? {
        if (balance <= 0.0) return 0
        if (payment <= 0.0) return null
        if (monthlyRate <= 0.0) return ceil(balance / payment).toInt()
        if (payment <= balance * monthlyRate) return null
        val months = ln(payment / (payment - balance * monthlyRate)) / ln(1.0 + monthlyRate)
        return ceil(months).toInt()
    }

    private fun roundTo(value: Double, step: Double): Double =
        (value / step).roundToLong() * step

    private fun money(value: Double): String =
        "₹" + String.format(Locale.US, "%,.0f", value)
}
