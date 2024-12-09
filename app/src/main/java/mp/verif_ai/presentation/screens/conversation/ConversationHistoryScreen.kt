package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationHistoryUiState
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationHistoryViewModel
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationUiState

@Composable
fun ConversationHistoryScreen(
    onConversationClick: (String) -> Unit,
    viewModel: ConversationHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                CustomSnackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ConversationHistoryUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }
            is ConversationHistoryUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.conversations) { conversation ->
                        ConversationHistoryItem(
                            conversation = conversation,
                            onClick = { onConversationClick(conversation.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is ConversationHistoryUiState.Error -> {
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
private fun ConversationHistoryItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = conversation.messages.lastOrNull()?.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(conversation.updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}