package mp.verif_ai.presentation.screens.conversation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.MessageSource
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.PointRepository
import mp.verif_ai.domain.service.AIModel
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

    fun sendMessage(inputContent: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value as? ConversationUiState.Success ?: return@launch

                // 사용자 메시지 생성
                val userMessage = Message.Text(
                    id = UUID.randomUUID().toString(),
                    content = inputContent,
                    senderId = userId,
                    messageSource = MessageSource(type = SourceType.USER)
                )

                // UI 상태 업데이트 (사용자 메시지 추가)
                _uiState.update { state ->
                    if (state is ConversationUiState.Success) {
                        state.copy(
                            messages = state.messages + userMessage
                        )
                    } else state
                }

                // 메시지를 DB에 저장
                conversationRepository.sendMessage(conversationId.toString(), userMessage)

                // AI 응답이 필요한 경우
                if (currentState.selectedModel != null) {
                    _uiState.update { state ->
                        if (state is ConversationUiState.Success) {
                            state.copy(isAiResponding = true)
                        } else state
                    }

                    var aiMessageContent = ""
                    conversationRepository.getAiResponse(
                        model = currentState.selectedModel,
                        prompt = inputContent
                    ).collect { response ->
                        aiMessageContent += response

                        // AI 메시지 생성 및 업데이트
                        val aiMessage = Message.Text(
                            id = UUID.randomUUID().toString(),
                            content = aiMessageContent,
                            senderId = "ai",
                            messageSource = MessageSource(
                                type = SourceType.AI,
                                model = currentState.selectedModel
                            )
                        )

                        // UI 상태 업데이트 (AI 메시지 추가/업데이트)
                        _uiState.update { state ->
                            if (state is ConversationUiState.Success) {
                                val updatedMessages = state.messages.toMutableList()
                                if (state.isAiResponding) {
                                    // 마지막 메시지가 AI 메시지면 업데이트, 아니면 추가
                                    if (updatedMessages.lastOrNull()?.messageSource?.type == SourceType.AI) {
                                        updatedMessages[updatedMessages.lastIndex] = aiMessage
                                    } else {
                                        updatedMessages.add(aiMessage)
                                    }
                                }
                                state.copy(
                                    messages = updatedMessages,
                                    isAiResponding = true
                                )
                            } else state
                        }
                    }

                    // AI 응답 완료 후 상태 업데이트
                    _uiState.update { state ->
                        if (state is ConversationUiState.Success) {
                            state.copy(isAiResponding = false)
                        } else state
                    }

                    // 최종 AI 메시지를 DB에 저장
                    val finalAiMessage = Message.Text(
                        id = UUID.randomUUID().toString(),
                        content = aiMessageContent,
                        senderId = "ai",
                        messageSource = MessageSource(
                            type = SourceType.AI,
                            model = currentState.selectedModel
                        )
                    )
                    conversationRepository.sendMessage(conversationId.toString(), finalAiMessage)
                }
            } catch (e: Exception) {
                _events.emit(ConversationEvent.ShowError(e.message ?: "메시지 전송에 실패했습니다"))
            }
        }
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
                                    aiModels = AIModel.entries,
                                    selectedModel = AIModel.GEMINI_1_5_PRO
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
        val selectedModel: AIModel?,
        val isAiResponding: Boolean = false,
        val expertReviews: List<ExpertReview> = emptyList(),
        val canRequestExpertReview: Boolean = true,
        val pointBalance: Int = 1000
    ) : ConversationUiState()
    data class Error(val message: String) : ConversationUiState()
}

sealed class ConversationEvent {
    data class ShowError(val message: String) : ConversationEvent()
    data class NavigateToExpertProfile(val expertId: String) : ConversationEvent()
    data object RequestExpertReviewSuccess : ConversationEvent()
    data object InsufficientPoints : ConversationEvent()
}