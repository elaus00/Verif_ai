package mp.verif_ai.presentation.viewmodel.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.prompt.UserPrompt
import mp.verif_ai.domain.repository.PromptRepository
import mp.verif_ai.presentation.viewmodel.state.ChatUiState
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatUiState>>(emptyList())
    val messages: StateFlow<List<ChatUiState>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendPrompt(content: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val userPrompt = UserPrompt(
                id = UUID.randomUUID().toString(),
                content = content
            )

            // Add user message immediately
            _messages.update { currentMessages ->
                currentMessages + ChatUiState.UserMessage(content)
            }

            // Start collecting assistant's response
            try {
                promptRepository.sendPrompt(userPrompt)
                    .collect { response ->
                        _messages.update { currentMessages ->
                            val lastMessage = currentMessages.lastOrNull()
                            if (lastMessage is ChatUiState.AssistantMessage) {
                                // Update existing assistant message
                                currentMessages.dropLast(1) + ChatUiState.AssistantMessage(
                                    lastMessage.content + response.content
                                )
                            } else {
                                // Add new assistant message
                                currentMessages + ChatUiState.AssistantMessage(response.content)
                            }
                        }
                    }
            } catch (e: Exception) {
                // Handle error
                _messages.update { currentMessages ->
                    currentMessages + ChatUiState.ErrorMessage(e.message ?: "Unknown error occurred")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}