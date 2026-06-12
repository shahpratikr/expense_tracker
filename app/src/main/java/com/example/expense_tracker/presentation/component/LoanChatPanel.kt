package com.example.expense_tracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.expense_tracker.presentation.viewmodel.ChatMessage
import com.example.expense_tracker.presentation.viewmodel.LlmState

// R-3: Chat panel composable for the Loans screen LLM repayment assistant
//      Appears below the loan list; shows spinner while model loads, chat when ready
@Composable
fun LoanChatPanel(
    llmState: LlmState,
    chatMessages: List<ChatMessage>,
    streamingResponse: String,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom whenever a new message arrives or streaming updates
    LaunchedEffect(chatMessages.size, streamingResponse) {
        val itemCount = chatMessages.size + if (streamingResponse.isNotEmpty()) 1 else 0
        if (itemCount > 0) listState.animateScrollToItem(itemCount - 1)
    }

    Column(modifier = modifier) {
        // R-3: Section header
        Text(
            text = "AI Repayment Assistant",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        when (llmState) {
            is LlmState.Idle -> {
                // R-3: Model not downloaded — show informational prompt
                Text(
                    text = "Download the Gemma model to enable AI repayment advice.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            is LlmState.Loading -> {
                // R-3: Model initializing — show spinner, hide chat input
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(
                        "Loading AI model...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is LlmState.Error -> {
                // R-3: Initialization or inference failure
                Text(
                    text = "AI unavailable: ${llmState.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            is LlmState.Ready, is LlmState.Generating -> {
                // R-3: Chat UI — message history + streaming token + input row
                val allMessages = chatMessages +
                    if (streamingResponse.isNotEmpty())
                        listOf(ChatMessage(streamingResponse, isUser = false))
                    else emptyList()

                if (allMessages.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(allMessages) { message ->
                            ChatBubble(message = message)
                        }
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                    }
                }

                // R-3: Input row — send button disabled while Generating
                val isGenerating = llmState is LlmState.Generating
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask about your loans...") },
                        modifier = Modifier.weight(1f),
                        enabled = !isGenerating,
                        maxLines = 3
                    )
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = !isGenerating && inputText.isNotBlank()
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = "Send message")
                        }
                    }
                }
            }
        }
    }
}

// R-3: Individual chat bubble; user messages right-aligned, assistant messages left-aligned
@Composable
private fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    val textColor = if (message.isUser)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
