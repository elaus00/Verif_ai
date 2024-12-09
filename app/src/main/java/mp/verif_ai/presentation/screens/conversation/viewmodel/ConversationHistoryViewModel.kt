package mp.verif_ai.presentation.screens.conversation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository

@HiltViewModel
class ConversationHistoryViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationHistoryUiState>(ConversationHistoryUiState.Loading)
    val uiState: StateFlow<ConversationHistoryUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUser()?.id
                    ?: throw IllegalStateException("사용자 정보를 찾을 수 없습니다")

                conversationRepository.getConversationHistory(
                    userId = userId,
                    limit = HISTORY_PAGE_SIZE,
                    offset = 0
                ).onSuccess { conversations ->
                    _uiState.value = ConversationHistoryUiState.Success(
                        conversations = conversations
                    )
                }.onFailure { e ->
                    _uiState.value = ConversationHistoryUiState.Error(
                        e.message ?: "대화 목록을 불러오는데 실패했습니다"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ConversationHistoryUiState.Error(
                    message = e.message ?: "대화 목록을 불러오는데 실패했습니다"
                )
            }
        }
    }

    fun retry() {
        loadConversations()
    }

    companion object {
        private const val HISTORY_PAGE_SIZE = 20
    }
}

sealed class ConversationHistoryUiState {
    data object Loading : ConversationHistoryUiState()
    data class Success(
        val conversations: List<Conversation>
    ) : ConversationHistoryUiState()
    data class Error(val message: String) : ConversationHistoryUiState()
}