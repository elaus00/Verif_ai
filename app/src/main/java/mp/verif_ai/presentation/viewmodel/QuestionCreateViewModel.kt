package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuestionCreateUiState(
    val title: String = "",
    val titleError: String? = null,
    val content: String = "",
    val contentError: String? = null,
    val suggestedTags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isValid: Boolean = false
)

class QuestionCreateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuestionCreateUiState())
    val uiState: StateFlow<QuestionCreateUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = validateTitle(title),
                isValid = validateForm(title, it.content)
            )
        }
    }

    fun updateContent(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                contentError = validateContent(content),
                isValid = validateForm(it.title, content)
            )
        }

        // AI 태그 추천 (디바운스 처리)
        viewModelScope.launch {
            delay(500)
            suggestTags(content)
        }
    }

    fun submitQuestion(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: API 호출
                val questionId = "temp_id"
                onSuccess(questionId)
            } catch (e: Exception) {
                // TODO: 에러 처리
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun saveAsDraft() {
        viewModelScope.launch {
            // TODO: 임시저장 구현
        }
    }

    private fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "제목을 입력해주세요"
            title.length < 5 -> "제목은 5자 이상이어야 합니다"
            title.length > 100 -> "제목은 100자를 초과할 수 없습니다"
            else -> null
        }
    }

    private fun validateContent(content: String): String? {
        return when {
            content.isBlank() -> "내용을 입력해주세요"
            content.length < 10 -> "내용은 10자 이상이어야 합니다"
            content.length > 1000 -> "내용은 1000자를 초과할 수 없습니다"
            else -> null
        }
    }

    private fun validateForm(title: String, content: String): Boolean {
        return validateTitle(title) == null && validateContent(content) == null
    }

    private suspend fun suggestTags(content: String) {
        if (content.length >= 20) {
            // TODO: AI 태그 추천 API 호출
            _uiState.update {
                it.copy(suggestedTags = listOf("AI", "머신러닝", "데이터사이언스"))
            }
        }
    }
}