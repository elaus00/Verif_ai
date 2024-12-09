package mp.verif_ai.presentation.screens.answer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.answer.AnswerStatus
import mp.verif_ai.domain.usecase.answer.AdoptAnswerUseCase
import mp.verif_ai.domain.usecase.answer.CreateAnswerUseCase
import mp.verif_ai.domain.usecase.answer.GetAnswerUseCase
import mp.verif_ai.domain.usecase.answer.GetAnswersForQuestionUseCase
import mp.verif_ai.domain.usecase.answer.GetExpertAnswersUseCase
import mp.verif_ai.domain.usecase.answer.UpdateAnswerStatusUseCase
import mp.verif_ai.domain.usecase.answer.UpdateAnswerUseCase
import javax.inject.Inject

@HiltViewModel
class AnswerViewModel @Inject constructor(
    private val createAnswerUseCase: CreateAnswerUseCase,
    private val getAnswerUseCase: GetAnswerUseCase,
    private val getAnswersForQuestionUseCase: GetAnswersForQuestionUseCase,
    private val getExpertAnswersUseCase: GetExpertAnswersUseCase,
    private val updateAnswerUseCase: UpdateAnswerUseCase,
    private val updateAnswerStatusUseCase: UpdateAnswerStatusUseCase,
    private val adoptAnswerUseCase: AdoptAnswerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnswerUiState>(AnswerUiState.Initial)
    val uiState: StateFlow<AnswerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AnswerEvent>()
    val events = _events.asSharedFlow()

    private val _answersForQuestion = MutableStateFlow<List<Answer>>(emptyList())
    val answersForQuestion: StateFlow<List<Answer>> = _answersForQuestion.asStateFlow()

    private val _expertAnswers = MutableStateFlow<List<Answer>>(emptyList())
    val expertAnswers: StateFlow<List<Answer>> = _expertAnswers.asStateFlow()

    fun createAnswer(answer: Answer) {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading
            createAnswerUseCase(answer)
                .onSuccess { answerId ->
                    _events.emit(AnswerEvent.AnswerCreated(answerId))
                    _uiState.value = AnswerUiState.Initial
                }
                .onFailure { e ->
                    _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to create answer"))
                    _uiState.value = AnswerUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun getAnswer(answerId: String) {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading
            getAnswerUseCase(answerId)
                .onSuccess { answer ->
                    _uiState.value = AnswerUiState.Success(answer)
                }
                .onFailure { e ->
                    _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to get answer"))
                    _uiState.value = AnswerUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun getAnswersForQuestion(questionId: String) {
        viewModelScope.launch {
            try {
                getAnswersForQuestionUseCase(questionId).collect { answers ->
                    _answersForQuestion.value = answers
                }
            } catch (e: Exception) {
                _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to get answers"))
            }
        }
    }

    fun getExpertAnswers(expertId: String, limit: Int = 10) {
        viewModelScope.launch {
            try {
                getExpertAnswersUseCase(expertId, limit).collect { answers ->
                    _expertAnswers.value = answers
                }
            } catch (e: Exception) {
                _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to get expert answers"))
            }
        }
    }

    fun updateAnswer(answer: Answer) {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading
            updateAnswerUseCase(answer)
                .onSuccess {
                    _events.emit(AnswerEvent.AnswerUpdated)
                    _uiState.value = AnswerUiState.Success(answer)
                }
                .onFailure { e ->
                    _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to update answer"))
                    _uiState.value = AnswerUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun updateAnswerStatus(answerId: String, status: AnswerStatus) {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading
            updateAnswerStatusUseCase(answerId, status)
                .onSuccess {
                    _events.emit(AnswerEvent.AnswerUpdated)
                }
                .onFailure { e ->
                    _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to update answer status"))
                    _uiState.value = AnswerUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    fun adoptAnswer(answerId: String, questionId: String) {
        viewModelScope.launch {
            _uiState.value = AnswerUiState.Loading
            adoptAnswerUseCase(answerId, questionId)
                .onSuccess {
                    _events.emit(AnswerEvent.AnswerAdopted)
                }
                .onFailure { e ->
                    _events.emit(AnswerEvent.ShowError(e.message ?: "Failed to adopt answer"))
                    _uiState.value = AnswerUiState.Error(e.message ?: "Unknown error")
                }
        }
    }
}