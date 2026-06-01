package com.example.expense_tracker.domain.usecase.loan

import org.junit.Test
import kotlin.random.Random

class GenerateRepaymentIdeasUseCaseTest {

    private val useCase = GenerateRepaymentIdeasUseCase(Random(42))

    @Test
    fun testReturnsClearedMessageWhenNoBalance() {
        val ideas = useCase(0.0)

        assert(ideas.size == 1)
        assert(ideas.first().title == "Loan cleared")
    }

    @Test
    fun testReturnsClearedMessageWhenNegativeBalance() {
        val ideas = useCase(-100.0)

        assert(ideas.size == 1)
        assert(ideas.first().title == "Loan cleared")
    }

    @Test
    fun testGeneratesScenariosAndTipsForPositiveBalance() {
        val ideas = useCase(120000.0)

        // 2 payoff horizons + 1 extra-payment nudge + 2 strategy tips
        assert(ideas.size == 5)
        assert(ideas.all { it.title.isNotBlank() && it.detail.isNotBlank() })
    }

    @Test
    fun testIdeasAreDistinct() {
        val ideas = useCase(50000.0)
        val titles = ideas.map { it.title }

        assert(titles.toSet().size == titles.size)
    }
}
