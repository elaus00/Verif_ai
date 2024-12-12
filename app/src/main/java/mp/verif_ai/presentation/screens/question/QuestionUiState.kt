package mp.verif_ai.presentation.screens.question

import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.TrendingQuestion

sealed class QuestionUiState {
    data object Initial : QuestionUiState()
    data object Loading : QuestionUiState()
    data class Success(
        val question: Question? = null,
        val trendingQuestions: List<TrendingQuestion> = emptyList(),
        val myQuestions: List<Question> = emptyList()
    ) : QuestionUiState()
    data class Error(val message: String) : QuestionUiState()
}

sealed class QuestionEvent {
    data class ShowError(val message: String) : QuestionEvent()
    data class QuestionCreated(val questionId: String) : QuestionEvent()
    data object QuestionUpdated : QuestionEvent()
    data class NavigateToQuestion(val questionId: String) : QuestionEvent()
    data class ShowSnackbar(val message: String) : QuestionEvent()
    data object DismissSnackbar : QuestionEvent()
}
