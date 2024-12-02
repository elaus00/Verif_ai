package mp.verif_ai.presentation.screens.prompt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.screens.prompt.components.AssistantMessageItem
import mp.verif_ai.presentation.screens.prompt.components.ErrorMessageItem
import mp.verif_ai.presentation.screens.prompt.components.UserMessageItem
import mp.verif_ai.presentation.viewmodel.prompt.PromptDetailViewModel
import mp.verif_ai.presentation.viewmodel.state.ChatUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDetailScreen(
    promptId: String,
    viewModel: PromptDetailViewModel = hiltViewModel()
) {
    val promptDetail by viewModel.promptDetail.collectAsState()

    LaunchedEffect(promptId) {
        viewModel.loadPromptDetail(promptId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prompt Detail") },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back */ }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Prompt content
            promptDetail?.let { detail ->
                items(detail.messages) { message ->
                    when (message) {
                        is ChatUiState.UserMessage -> UserMessageItem(message)
                        is ChatUiState.AssistantMessage -> AssistantMessageItem(message)
                        is ChatUiState.ErrorMessage -> ErrorMessageItem(message)
                        is ChatUiState.AssistantMessage -> TODO()
                        is ChatUiState.ErrorMessage -> TODO()
                        is ChatUiState.UserMessage -> TODO()
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}