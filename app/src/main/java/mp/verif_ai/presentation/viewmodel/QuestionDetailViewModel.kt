package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.repository.QuestionRepository
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionDetailState>(QuestionDetailState.Loading)
    val uiState: StateFlow<QuestionDetailState> = _uiState.asStateFlow()

    fun loadQuestion(questionId: String) {
        viewModelScope.launch {
            _uiState.value = QuestionDetailState.Loading
            try {
                questionRepository.getQuestion(questionId)
                    .onSuccess { question ->
                        _uiState.value = QuestionDetailState.Success(question)
                    }
                    .onFailure { error ->
                        _uiState.value = QuestionDetailState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                _uiState.value = QuestionDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteQuestion() {
        viewModelScope.launch {
            val currentQuestion = (uiState.value as? QuestionDetailState.Success)?.question ?: return@launch

            try {
                // 실제로 삭제하지 않고 상태만 DELETED로 변경
                val updatedQuestion = currentQuestion.copy(status = QuestionStatus.DELETED)
                questionRepository.updateQuestion(updatedQuestion)  // 이 메서드는 repository에 추가 필요
                    .onSuccess {
                        _uiState.value = QuestionDetailState.Success(updatedQuestion)
                    }
                    .onFailure { error ->
                        _uiState.value = QuestionDetailState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                _uiState.value = QuestionDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed interface QuestionDetailState {
    object Loading : QuestionDetailState
    data class Success(override val question: Question) : QuestionDetailState
    data class Error(override val errorMessage: String) : QuestionDetailState

    val question: Question?
        get() = when (this) {
            is Success -> question
            else -> null
        }

    val errorMessage: String?
        get() = when (this) {
            is Error -> errorMessage
            else -> null
        }
}