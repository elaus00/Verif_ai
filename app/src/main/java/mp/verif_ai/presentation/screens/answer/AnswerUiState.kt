package mp.verif_ai.presentation.screens.answer

import mp.verif_ai.domain.model.answer.Answer

sealed class AnswerUiState {
    data object Initial : AnswerUiState()
    data object Loading : AnswerUiState()
    data class Success(
        val answer: Answer? = null,
        val questionAnswers: List<Answer> = emptyList(),
        val expertAnswers: List<Answer> = emptyList(),
        val isAdoptionInProgress: Boolean = false
    ) : AnswerUiState()
    data class Error(val message: String) : AnswerUiState()
}

sealed class AnswerEvent {
    data class ShowError(val message: String) : AnswerEvent()
    data class AnswerCreated(val answerId: String) : AnswerEvent()
    data object AnswerUpdated : AnswerEvent()
    data object AnswerAdopted : AnswerEvent()
    data class NavigateToAnswer(val answerId: String) : AnswerEvent()
    data class ShowSnackbar(val message: String) : AnswerEvent()
    data object DismissSnackbar : AnswerEvent()
}