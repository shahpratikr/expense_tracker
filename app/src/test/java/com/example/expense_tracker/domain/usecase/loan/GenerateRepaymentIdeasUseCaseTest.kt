package com.example.expense_tracker.domain.usecase.loan

import com.example.expense_tracker.domain.model.Loan
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class GenerateRepaymentIdeasUseCaseTest {

    private val useCase = GenerateRepaymentIdeasUseCase(Random(42))

    private fun loan(balance: Double, rate: Double = 0.0, emi: Double = 0.0) = Loan(
        id = 1L,
        name = "Test Loan",
        currentBalance = balance,
        createdAt = LocalDate.now(),
        interestRate = rate,
        emi = emi
    )

    @Test
    fun testReturnsClearedMessageWhenNoBalance() {
        val ideas = useCase(loan(balance = 0.0))

        assert(ideas.size == 1)
        assert(ideas.first().title == "Loan cleared")
    }

    @Test
    fun testPromptsForEmiAndRateWhenMissing() {
        val ideas = useCase(loan(balance = 100000.0, rate = 0.0, emi = 0.0))

        assert(ideas.size == 1)
        assert(ideas.first().title == "Add your EMI & interest rate")
    }

    @Test
    fun testWarnsWhenEmiBelowMonthlyInterest() {
        // 12% p.a. on 100000 = 1000/month interest; EMI of 500 can never amortize.
        val ideas = useCase(loan(balance = 100000.0, rate = 12.0, emi = 500.0))

        assert(ideas.size == 1)
        assert(ideas.first().title == "Your EMI is too low")
    }

    @Test
    fun testGeneratesAmortizationIdeasForValidLoan() {
        val ideas = useCase(loan(balance = 100000.0, rate = 12.0, emi = 5000.0))

        // current pace + two extra-payment scenarios + one lump-sum scenario
        assert(ideas.size >= 3)
        assert(ideas.first().title == "Your current pace")
        assert(ideas.all { it.title.isNotBlank() && it.detail.isNotBlank() })
    }

    @Test
    fun testZeroInterestUsesSimpleDivision() {
        val ideas = useCase(loan(balance = 12000.0, rate = 0.0, emi = 1000.0))

        assert(ideas.isNotEmpty())
        assert(ideas.first().title == "Your current pace")
        // 12000 / 1000 = 12 months
        assert(ideas.first().detail.contains("12 months"))
    }
}
