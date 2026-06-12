package com.example.expense_tracker.ai

import android.content.Context
import com.example.expense_tracker.domain.model.Loan
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File

// R-3: Interface for LLM inference, allowing mocking in unit tests
interface ILlmInferenceHelper {
    // R-3: Returns true if the model file has been downloaded to local storage
    fun isModelAvailable(): Boolean

    // R-3: Initializes the on-device LLM; throws if initialization fails
    fun initialize(loans: List<Loan>)

    // R-3: Streams response tokens for the given user message as a Flow<String>
    fun generateResponse(userMessage: String): Flow<String>

    // R-3: Releases native LLM resources; must be called when the owning ViewModel is cleared
    fun release()
}

// R-3: Wraps MediaPipe LlmInference for on-device loan repayment Q&A using Gemma 3 1B INT4
//      Model is loaded from internal storage; initialization is graceful if model not yet downloaded.
class LlmInferenceHelper(private val context: Context) : ILlmInferenceHelper {

    // R-3: Local path where the Gemma 3 1B INT4 model binary is stored after one-time download
    val modelPath: String = context.filesDir.absolutePath + "/gemma3-1b-it-int4.bin"

    private var llmInference: Any? = null   // com.google.mediapipe.tasks.genai.llminference.LlmInference
    private var systemPrompt: String = ""

    // R-3: Checks whether the model binary exists in app internal storage
    override fun isModelAvailable(): Boolean = File(modelPath).exists()

    // R-3: Initializes LlmInference from the local model file and builds the system prompt from loan data
    override fun initialize(loans: List<Loan>) {
        if (!isModelAvailable()) {
            throw IllegalStateException("Model file not found at $modelPath. Download it first.")
        }
        systemPrompt = buildSystemPrompt(loans)
        try {
            // Use reflection so the class compiles even when MediaPipe jar is absent at test time.
            // At runtime on a real device the jar is present and this succeeds.
            val optionsBuilderClass = Class.forName(
                "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions"
            )
            val builderMethod = optionsBuilderClass.getMethod("builder")
            val builder = builderMethod.invoke(null)

            builder.javaClass.getMethod("setModelPath", String::class.java)
                .invoke(builder, modelPath)
            builder.javaClass.getMethod("setMaxTokens", Int::class.java)
                .invoke(builder, 1024)
            builder.javaClass.getMethod("setTopK", Int::class.java)
                .invoke(builder, 40)
            builder.javaClass.getMethod("setTemperature", Float::class.java)
                .invoke(builder, 0.7f)
            builder.javaClass.getMethod("setRandomSeed", Int::class.java)
                .invoke(builder, 101)

            val options = builder.javaClass.getMethod("build").invoke(builder)

            val llmClass = Class.forName(
                "com.google.mediapipe.tasks.genai.llminference.LlmInference"
            )
            llmInference = llmClass
                .getMethod("createFromOptions", Context::class.java, optionsBuilderClass)
                .invoke(null, context, options)
        } catch (e: ClassNotFoundException) {
            throw UnsupportedOperationException(
                "MediaPipe LlmInference not available on this build target", e
            )
        }
    }

    // R-3: Sends userMessage to the LLM and returns streamed response tokens as Flow<String>
    override fun generateResponse(userMessage: String): Flow<String> = callbackFlow {
        val inference = llmInference
            ?: throw IllegalStateException("LLM not initialized. Call initialize() first.")

        val prompt = "$systemPrompt\n\nUser: $userMessage\nAssistant:"

        // Resolve the listener interface at runtime to avoid compile-time dependency on MediaPipe.
        val listenerClass = Class.forName(
            "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceResultListener"
        )
        val listenerProxy = java.lang.reflect.Proxy.newProxyInstance(
            listenerClass.classLoader,
            arrayOf(listenerClass)
        ) { _, method, args ->
            when (method.name) {
                "onResult" -> {
                    val partialResult = args[0] as? String ?: ""
                    val done = args[1] as? Boolean ?: false
                    trySend(partialResult)
                    if (done) close()
                }
                "onError" -> {
                    val error = args[0] as? Exception
                        ?: RuntimeException("LLM inference error")
                    close(error)
                }
            }
            null
        }

        inference.javaClass
            .getMethod("generateResponseAsync", String::class.java, listenerClass)
            .invoke(inference, prompt, listenerProxy)

        awaitClose { /* nothing to cancel for MediaPipe async call */ }
    }

    // R-3: Releases the LlmInference object and its native resources
    override fun release() {
        try {
            llmInference?.javaClass?.getMethod("close")?.invoke(llmInference)
        } catch (_: Exception) { /* best-effort */ }
        llmInference = null
    }

    // R-3: Builds system prompt from live loan data so LLM answers only with real user figures
    internal fun buildSystemPrompt(loans: List<Loan>): String {
        val loanSummary = loans.joinToString("\n") { loan ->
            val rate = if (loan.interestRate > 0.0) "@ ${loan.interestRate}% p.a."
                       else "(interest rate unknown)"
            val emi = if (loan.emi > 0.0) ", min payment ₹${loan.emi}/month" else ""
            "- ${loan.name}: ₹${loan.currentBalance} balance $rate$emi"
        }
        return """
You are a personal loan repayment advisor for an Indian user. Currency is ₹ (INR).
The user has the following loans:
$loanSummary

Answer only questions about loan repayment strategies using this data.
If interest rate is missing for a loan, ask the user to provide it for accurate calculations.
Be concise. Do not invent figures not present in the loan data above.
        """.trimIndent()
    }
}
