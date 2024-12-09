package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationDetailViewModel
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationUiState

// Java imports
import java.text.DateFormat
import java.util.Date

@Composable
fun ConversationDetailScreen(
    conversationId: String,
    viewModel: ConversationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(conversationId) {
        viewModel.loadConversation()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                CustomSnackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ConversationUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            is ConversationUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.messages) { message ->
                        MessageItem(message = message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is ConversationUiState.Error -> {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        actionLabel = "Dismiss"
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageItem(message: Message) {
    val isUserMessage = message.senderId == "currentUserId" // 실제 구현시 ViewModel에서 제공

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUserMessage)
                    MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatDate(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(timestamp))
}