package mp.verif_ai.presentation.screens.prompt

import mp.verif_ai.domain.model.chat.PromptImport

sealed class ImportPromptUiState {
    object Initial : ImportPromptUiState()
    object Loading : ImportPromptUiState()
    data class Success(val promptImport: PromptImport) : ImportPromptUiState()
    data class QuestionCreated(val questionId: String) : ImportPromptUiState()
    data class Error(val message: String) : ImportPromptUiState()
}