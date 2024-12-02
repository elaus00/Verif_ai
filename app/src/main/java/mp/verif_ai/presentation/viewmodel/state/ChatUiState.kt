package mp.verif_ai.presentation.viewmodel.state

sealed interface ChatUiState {
    data class UserMessage(val content: String) : ChatUiState
    data class AssistantMessage(val content: String) : ChatUiState
    data class ErrorMessage(val message: String) : ChatUiState
}