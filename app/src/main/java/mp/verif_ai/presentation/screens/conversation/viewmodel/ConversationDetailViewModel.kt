package mp.verif_ai.presentation.screens.conversation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.presentation.screens.Screen

@HiltViewModel
class ConversationDetailViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String? = savedStateHandle[Screen.ARG_CONVERSATION_ID]

    private val _uiState = MutableStateFlow<ConversationUiState>(ConversationUiState.Loading)
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    init {
        loadConversation()
    }

    fun loadConversation() {
        viewModelScope.launch {
            try {
                conversationRepository.observeConversation(conversationId.toString())
                    .collect { conversation ->
                        _uiState.value = ConversationUiState.Success(
                            messages = conversation.messages,
                            aiModels = emptyList()
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = ConversationUiState.Error(e.message ?: "대화를 불러오는데 실패했습니다")
            }
        }
    }

    fun retry() {
        loadConversation()
    }
}
