package com.example.expense_tracker.ai

import com.example.expense_tracker.domain.model.Loan
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

// R-3: Unit tests for LlmInferenceHelper — only tests pure logic (system prompt builder, model availability)
class LlmInferenceHelperTest {

    // R-3: buildSystemPrompt includes all loan names and balances
    @Test
    fun `buildSystemPrompt contains loan names and balances`() {
        val helper = buildHelperWithNoContext()
        val loans = listOf(
            Loan(id = 1, name = "Home Loan", currentBalance = 2500000.0,
                interestRate = 8.5, emi = 22000.0, createdAt = LocalDate.now()),
            Loan(id = 2, name = "Car Loan", currentBalance = 350000.0,
                interestRate = 0.0, emi = 0.0, createdAt = LocalDate.now())
        )
        val prompt = helper.buildSystemPrompt(loans)
        assertTrue("Prompt should contain 'Home Loan'", prompt.contains("Home Loan"))
        assertTrue("Prompt should contain '2500000.0'", prompt.contains("2500000.0"))
        assertTrue("Prompt should contain 'Car Loan'", prompt.contains("Car Loan"))
    }

    // R-3: buildSystemPrompt marks missing interest rate as unknown
    @Test
    fun `buildSystemPrompt marks missing interest rate as unknown`() {
        val helper = buildHelperWithNoContext()
        val loans = listOf(
            Loan(id = 1, name = "Personal Loan", currentBalance = 50000.0,
                interestRate = 0.0, emi = 0.0, createdAt = LocalDate.now())
        )
        val prompt = helper.buildSystemPrompt(loans)
        assertTrue("Prompt should note unknown interest rate",
            prompt.contains("interest rate unknown"))
    }

    // R-3: buildSystemPrompt includes interest rate when present
    @Test
    fun `buildSystemPrompt includes interest rate when positive`() {
        val helper = buildHelperWithNoContext()
        val loans = listOf(
            Loan(id = 1, name = "SBI Loan", currentBalance = 100000.0,
                interestRate = 10.5, emi = 2000.0, createdAt = LocalDate.now())
        )
        val prompt = helper.buildSystemPrompt(loans)
        assertTrue("Prompt should include rate", prompt.contains("10.5% p.a."))
    }

    // R-3: buildSystemPrompt includes EMI when present
    @Test
    fun `buildSystemPrompt includes emi when positive`() {
        val helper = buildHelperWithNoContext()
        val loans = listOf(
            Loan(id = 1, name = "HDFC Loan", currentBalance = 75000.0,
                interestRate = 12.0, emi = 3000.0, createdAt = LocalDate.now())
        )
        val prompt = helper.buildSystemPrompt(loans)
        assertTrue("Prompt should include EMI", prompt.contains("3000.0/month"))
    }

    // R-3: buildSystemPrompt works with an empty loan list
    @Test
    fun `buildSystemPrompt works with empty list`() {
        val helper = buildHelperWithNoContext()
        val prompt = helper.buildSystemPrompt(emptyList())
        assertTrue("Prompt should still contain currency instruction", prompt.contains("INR"))
    }

    /**
     * Constructs a LlmInferenceHelper without a real Context by bypassing the constructor via
     * reflection so that the test does not depend on Android framework classes.
     */
    private fun buildHelperWithNoContext(): LlmInferenceHelper {
        // We only test internal pure functions so we pass a mock context.
        val mockContext = mock(android.content.Context::class.java)
        val mockFilesDir = java.io.File(System.getProperty("java.io.tmpdir")!!)
        whenever(mockContext.filesDir).thenReturn(mockFilesDir)
        return LlmInferenceHelper(mockContext)
    }
}
