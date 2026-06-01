package com.example.expense_tracker.domain.usecase.loan

import java.util.Locale
import kotlin.math.ceil
import kotlin.random.Random

data class RepaymentIdea(
    val title: String,
    val detail: String
)

/**
 * Produces a fresh set of repayment suggestions for a loan based on its remaining balance.
 * Combines computed payoff scenarios with shuffled strategy tips, so each invocation
 * (i.e. each time the user opens a loan) yields a slightly different list.
 */
class GenerateRepaymentIdeasUseCase(private val random: Random = Random.Default) {

    operator fun invoke(currentBalance: Double): List<RepaymentIdea> {
        if (currentBalance <= 0.0) {
            return listOf(
                RepaymentIdea(
                    title = "Loan cleared",
                    detail = "There's no remaining balance on this loan. Keep it up!"
                )
            )
        }

        val ideas = mutableListOf<RepaymentIdea>()

        val horizons = HORIZON_OPTIONS.shuffled(random).take(2).sorted()
        horizons.forEach { months ->
            val monthly = ceil(currentBalance / months)
            ideas += RepaymentIdea(
                title = "Clear it in $months months",
                detail = "Set aside about ${formatAmount(monthly)} every month to be debt-free in $months months."
            )
        }

        val extra = EXTRA_PAYMENT_STEPS.shuffled(random).first()
        val monthsWithExtra = ceil(currentBalance / extra).toInt()
        ideas += RepaymentIdea(
            title = "Add ${formatAmount(extra)} a month",
            detail = "Putting an extra ${formatAmount(extra)} toward this loan each month would clear the current " +
                "balance in about $monthsWithExtra month(s)."
        )

        ideas += STRATEGY_TIPS.shuffled(random).take(2)

        return ideas
    }

    private fun formatAmount(value: Double): String =
        "₹" + String.format(Locale.US, "%,.0f", value)

    companion object {
        private val HORIZON_OPTIONS = listOf(6, 9, 12, 18, 24)
        private val EXTRA_PAYMENT_STEPS = listOf(500.0, 1000.0, 2000.0, 2500.0, 5000.0)

        private val STRATEGY_TIPS = listOf(
            RepaymentIdea(
                "Avalanche the interest",
                "Throw every spare rupee at your highest-interest debt while paying minimums on the rest."
            ),
            RepaymentIdea(
                "Snowball for momentum",
                "If this is your smallest loan, clear it first for a quick win that keeps you motivated."
            ),
            RepaymentIdea(
                "Round up every payment",
                "Round each repayment up to the nearest ₹1,000 — the extra quietly eats into the principal."
            ),
            RepaymentIdea(
                "Direct your windfalls",
                "Send bonuses, tax refunds and cash gifts straight to this loan instead of spending them."
            ),
            RepaymentIdea(
                "Pay biweekly",
                "Split your monthly payment in half and pay every two weeks; you'll make one extra payment a year."
            ),
            RepaymentIdea(
                "Redirect a subscription",
                "Cancel one unused subscription and route that amount to this loan every month."
            ),
            RepaymentIdea(
                "Automate on payday",
                "Schedule an automatic transfer the day you get paid so repayment happens before you spend."
            ),
            RepaymentIdea(
                "Make a lump-sum dent",
                "Sell items you no longer use and put the proceeds toward a one-time balance reduction."
            )
        )
    }
}
