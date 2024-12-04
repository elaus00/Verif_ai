package mp.verif_ai.presentation.screens.conversation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import mp.verif_ai.domain.model.conversation.AIModel
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.MessageSource
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.PointRepository
import mp.verif_ai.presentation.screens.Screen


@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository,
    private val pointRepository: PointRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: String? = savedStateHandle[Screen.ARG_CONVERSATION_ID]
    private val userId: String = checkNotNull(authRepository.getCurrentUser()?.id)

    private val _uiState = MutableStateFlow<ConversationUiState>(ConversationUiState.Loading)
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ConversationEvent>()
    val events: SharedFlow<ConversationEvent> = _events

    init {
        if (conversationId != null) {
            loadConversation()
        } else {
            // 새로운 대화 시작 초기화
            _uiState.value = ConversationUiState.Success(
                messages = emptyList(),
                aiModels = AIModel.entries,
                selectedModel = null
            )
        }
        observeUserPoints(userId)
    }

    private fun loadConversation() {
        viewModelScope.launch {
            try {
                conversationRepository.observeConversation(conversationId.toString())
                    .collect { conversation ->
                        _uiState.update { currentState ->
                            when (currentState) {
                                is ConversationUiState.Success -> currentState.copy(
                                    messages = conversation.messages
                                )
                                else -> ConversationUiState.Success(
                                    messages = conversation.messages,
                                    aiModels = AIModel.entries
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _events.emit(ConversationEvent.ShowError(e.message ?: "대화를 불러오는데 실패했습니다"))
                _uiState.value = ConversationUiState.Error(e.message ?: "대화를 불러오는데 실패했습니다")
            }
        }
    }

    private fun observeUserPoints(userId : String) {
        viewModelScope.launch {
            pointRepository.observeUserPoints(userId).collect { points ->
                _uiState.update { currentState ->
                    if (currentState is ConversationUiState.Success) {
                        currentState.copy(pointBalance = points)
                    } else currentState
                }
            }
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? ConversationUiState.Success ?: return@launch

                // 메시지 전송
                val message = Message.Text(
                    id = UUID.randomUUID().toString(),
                    content = content,
                    senderId = authRepository.getCurrentUser()?.id ?: return@launch,
                    messageSource = MessageSource(type = SourceType.USER)
                )

                conversationRepository.sendMessage(conversationId.toString(), message)

                // AI 응답이 필요한 경우
                if (currentState.selectedModel != null) {
                    _uiState.update {
                        if (it is ConversationUiState.Success) {
                            it.copy(isAiResponding = true)
                        } else it
                    }

                    conversationRepository.getAiResponse(
                        model = currentState.selectedModel,
                        prompt = content
                    ).collect { response ->
                        val aiMessage = Message.Text(
                            id = UUID.randomUUID().toString(),
                            content = response,
                            senderId = "ai",
                            messageSource = MessageSource(
                                type = SourceType.AI,
                                model = currentState.selectedModel
                            )
                        )
                        conversationRepository.sendMessage(conversationId.toString(), aiMessage)
                    }

                    _uiState.update {
                        if (it is ConversationUiState.Success) {
                            it.copy(isAiResponding = false)
                        } else it
                    }
                }
            } catch (e: Exception) {
                _events.emit(ConversationEvent.ShowError(e.message ?: "메시지 전송에 실패했습니다"))
            }
        }
    }

    fun requestExpertReview() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? ConversationUiState.Success ?: return@launch
                val points = pointRepository.getUserPoints().getOrThrow()

                if (points < Adoption.EXPERT_REVIEW_POINTS) {
                    _events.emit(ConversationEvent.InsufficientPoints)
                    return@launch
                }

                conversationRepository.requestExpertReview(
                    conversationId = conversationId.toString(),
                    points = Adoption.EXPERT_REVIEW_POINTS
                ).onSuccess {
                    _events.emit(ConversationEvent.RequestExpertReviewSuccess)
                }.onFailure { e ->
                    _events.emit(ConversationEvent.ShowError(e.message ?: "전문가 검증 요청에 실패했습니다"))
                }
            } catch (e: Exception) {
                _events.emit(ConversationEvent.ShowError(e.message ?: "전문가 검증 요청에 실패했습니다"))
            }
        }
    }

    fun selectAiModel(model: AIModel) {
        _uiState.update { currentState ->
            if (currentState is ConversationUiState.Success) {
                currentState.copy(selectedModel = model)
            } else currentState
        }
    }

    fun retry() {
        loadConversation()
    }
}

sealed class ConversationUiState {
    data object Loading : ConversationUiState()
    data class Success(
        val messages: List<Message>,
        val aiModels: List<AIModel>,
        val selectedModel: AIModel? = null,
        val isAiResponding: Boolean = false,
        val expertReviews: List<ExpertReview> = emptyList(),
        val canRequestExpertReview: Boolean = true,
        val pointBalance: Int = 0
    ) : ConversationUiState()
    data class Error(val message: String) : ConversationUiState()
}

sealed class ConversationEvent {
    data class ShowError(val message: String) : ConversationEvent()
    data class NavigateToExpertProfile(val expertId: String) : ConversationEvent()
    data object RequestExpertReviewSuccess : ConversationEvent()
    data object InsufficientPoints : ConversationEvent()
}