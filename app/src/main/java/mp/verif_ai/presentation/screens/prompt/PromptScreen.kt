package mp.verif_ai.presentation.screens.prompt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.viewmodel.prompt.PromptViewModel
import androidx.compose.runtime.Composable
import mp.verif_ai.presentation.screens.prompt.components.AssistantMessageItem
import mp.verif_ai.presentation.screens.prompt.components.ErrorMessageItem
import mp.verif_ai.presentation.screens.prompt.components.UserMessageItem
import mp.verif_ai.presentation.viewmodel.state.ChatUiState

@Composable
fun PromptScreen(
    viewModel: PromptViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                when (message) {
                    is ChatUiState.UserMessage -> UserMessageItem(message)
                    is ChatUiState.AssistantMessage -> AssistantMessageItem(message)
                    is ChatUiState.ErrorMessage -> ErrorMessageItem(message)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Input section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type your message...") },
                enabled = !isLoading
            )

            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendPrompt(userInput)
                        userInput = ""
                    }
                },
                enabled = userInput.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send")
                }
            }
        }
    }
}