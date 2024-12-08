package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationUiState

@Composable
fun ConversationHistoryPage(
    uiState: ConversationUiState,
    searchQuery: String,
    onConversationClick: (String) -> Unit
) {
    when (uiState) {
        is ConversationUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ConversationUiState.Success -> {
            val conversations = if (searchQuery.isBlank()) {
                uiState.conversations
            } else {
                uiState.filteredConversations
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = conversations.size,
                    key = { index -> conversations[index].id },
                    contentType = { "conversation" }
                ) { index ->
                    ConversationHistoryItem(
                        conversation = conversations[index],
                        searchQuery = searchQuery,
                        onClick = { onConversationClick(conversations[index].id) }
                    )
                }
            }
        }
        is ConversationUiState.Error -> {
            ErrorContent(
                message = uiState.message,
                onRetry = { /* 재시도 */ }
            )
        }
    }
}

@Composable
fun ConversationDetailPage(
    uiState: ConversationUiState,
    onSendMessage: (String) -> Unit,
    onRequestExpertReview: () -> Unit,
    onModelSelect: (AIModel) -> Unit
) {
    var userInput by remember { mutableStateOf("") }

    when (uiState) {
        is ConversationUiState.Success -> {
            Column {
                ConversationContent(
                    messages = uiState.messages,
                    canRequestExpertReview = uiState.canRequestExpertReview,
                    pointBalance = uiState.pointBalance,
                    onRequestExpertReview = onRequestExpertReview,
                    modifier = Modifier.weight(1f)
                )

                ChatBottomBar(
                    userInput = userInput,
                    onUserInputChange = { userInput = it },
                    onSendMessage = {
                        if (userInput.isNotBlank()) {
                            onSendMessage(userInput)
                            userInput = ""
                        }
                    },
                    onVoiceRecognition = { /* 음성 인식 */ },
                    onAddClick = { /* 첨부 */ },
                    models = uiState.aiModels,
                    selectedModel = uiState.selectedModel,
                    onModelSelect = onModelSelect
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}