package mp.verif_ai.presentation.screens.conversation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.MessageSource
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.PointRepository
import mp.verif_ai.domain.repository.ResponseRepository
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.presentation.screens.Screen
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository,
    private val pointRepository: PointRepository,
    private val responseRepository: ResponseRepository,
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
            initializeNewConversation()
        }
        observeUserPoints(userId)
    }

    private fun initializeNewConversation() {
        _uiState.value = ConversationUiState.Success(
            messages = emptyList(),
            aiModels = AIModel.entries,
            selectedModel = AIModel.GEMINI_1_5_PRO
        )
    }

    fun sendMessage(inputContent: String) {
        viewModelScope.launch {
            try {
                val currentState = getCurrentStateOrNull() ?: return@launch

                val userMessage = MessageFactory.createMessage(
                    content = inputContent,
                    type = SourceType.USER,
                    senderId = userId
                )

                // 메시지 저장 시도
                conversationRepository.sendMessage(conversationId.toString(), userMessage)
                    .onSuccess { messageId ->
                        // UI 업데이트 및 이벤트 발생
                        updateMessages(userMessage)
                        _events.emit(ConversationEvent.MessageSent(messageId))

                        // AI 응답 처리
                        currentState.selectedModel?.let { model ->
                            handleAiResponse(inputContent, model)
                        }
                    }
                    .onFailure { e ->
                        handleError("메시지 전송에 실패했습니다", e)
                    }

            } catch (e: Exception) {
                handleError("메시지 전송에 실패했습니다", e)
            }
        }
    }

    private fun loadConversation() {
        viewModelScope.launch {
            try {
                conversationRepository.observeConversation(conversationId.toString())
                    .collect { conversation ->
                        updateState { currentState ->
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
                handleError("대화를 불러오는데 실패했습니다", e)
                _uiState.value = ConversationUiState.Error("대화를 불러오는데 실패했습니다")
            }
        }
    }

    private fun observeUserPoints(userId: String) {
        viewModelScope.launch {
            pointRepository.observeUserPoints(userId).collect { points ->
                updateState { it.copy(pointBalance = points) }
            }
        }
    }

    fun requestExpertReview() {
        viewModelScope.launch {
            try {
                val currentState = getCurrentStateOrNull() ?: return@launch
                val points = pointRepository.getUserPoints().getOrThrow()

                if (points < Adoption.EXPERT_REVIEW_POINTS) {
                    _events.emit(ConversationEvent.InsufficientPoints)
                    return@launch
                }

                responseRepository.requestExpertReview(
                    conversationId = conversationId.toString(),
                    points = Adoption.EXPERT_REVIEW_POINTS
                ).onSuccess {
                    _events.emit(ConversationEvent.RequestExpertReviewSuccess)
                }.onFailure { e ->
                    handleError("전문가 검증 요청에 실패했습니다", e)
                }
            } catch (e: Exception) {
                handleError("전문가 검증 요청에 실패했습니다", e)
            }
        }
    }

    fun selectAiModel(model: AIModel) {
        updateState { it.copy(selectedModel = model) }
    }

    private fun getCurrentStateOrNull(): ConversationUiState.Success? {
        val currentState = _uiState.value as? ConversationUiState.Success
        if (currentState == null) {
            Log.e("ConversationVM", "Failed to get current state: Invalid state")
        }
        return currentState
    }

    private suspend fun handleUserMessage(content: String) {
        Log.d("ConversationVM", "Creating user message with content: ${content.take(50)}...")

        val userMessage = MessageFactory.createMessage(
            content = content,
            type = SourceType.USER,
            senderId = userId
        )

        updateMessages(userMessage)
        saveMessageToDatabase(userMessage)
        _events.emit(ConversationEvent.MessageSent(userMessage.id))
    }

    private suspend fun updateMessages(message: Message, isAiMessage: Boolean = false) {
        updateState { state ->
            val updatedMessages = state.messages.toMutableList()

            if (isAiMessage && state.isAiResponding) {
                if (updatedMessages.lastOrNull()?.messageSource?.type == SourceType.AI) {
                    Log.d("ConversationVM", "Updating existing AI message")
                    updatedMessages[updatedMessages.lastIndex] = message
                } else {
                    Log.d("ConversationVM", "Adding new AI message")
                    updatedMessages.add(message)
                }
            } else {
                Log.d("ConversationVM", "Adding new message")
                updatedMessages.add(message)
            }

            state.copy(
                messages = updatedMessages,
                isAiResponding = isAiMessage
            )
        }
    }

    private suspend fun saveMessageToDatabase(message: Message) {
        Log.d("ConversationVM", "Saving message to DB for conversation: $conversationId")
        conversationRepository.sendMessage(conversationId.toString(), message)
    }

    private suspend fun handleAiResponse(userContent: String, model: AIModel) {
        try {
            setAiResponding(true)

            var aiMessageContent = ""
            var currentAiMessage: Message.Text? = null

            responseRepository.getAiResponse(model, userContent)
                .catch { e ->
                    handleError("AI 응답 처리 중 오류가 발생했습니다", e)
                }
                .onCompletion { error ->
                    setAiResponding(false)
                    if (error == null && currentAiMessage != null) {
                        // 최종 AI 메시지 저장
                        conversationRepository.sendMessage(conversationId.toString(), currentAiMessage!!)
                            .onSuccess { messageId ->
                                _events.emit(ConversationEvent.AiResponseReceived(messageId))
                            }
                            .onFailure { e ->
                                handleError("AI 응답 저장에 실패했습니다", e)
                            }
                    }
                }
                .collect { response ->
                    aiMessageContent += response

                    currentAiMessage = MessageFactory.createMessage(
                        content = aiMessageContent,
                        type = SourceType.AI,
                        senderId = "ai",
                        model = model
                    )
                    updateMessages(currentAiMessage, isAiMessage = true)
                }
        } catch (e: Exception) {
            handleError("AI 응답 처리 중 오류가 발생했습니다", e)
        }
    }
    private fun setAiResponding(responding: Boolean) {
        updateState { it.copy(isAiResponding = responding) }
    }

    private fun updateState(update: (ConversationUiState.Success) -> ConversationUiState) {
        _uiState.update { state ->
            if (state is ConversationUiState.Success) {
                update(state)
            } else state
        }
    }

    private suspend fun handleError(message: String, error: Throwable) {
        Log.e("ConversationVM", message, error)
        val errorMessage = when (error) {
            is IllegalStateException -> "대화 상태가 유효하지 않습니다"
            is NetworkException -> "네트워크 연결을 확인해주세요"
            is AIServiceException -> "AI 서비스 응답 중 오류가 발생했습니다"
            else -> error.message ?: message
        }
        _events.emit(ConversationEvent.ShowError(errorMessage))
    }

    fun retry() {
        loadConversation()
    }
}

private object MessageFactory {
    fun createMessage(
        content: String,
        type: SourceType,
        senderId: String,
        model: AIModel? = null
    ): Message.Text = Message.Text(
        id = UUID.randomUUID().toString(),
        content = content,
        senderId = senderId,
        messageSource = MessageSource(
            type = type,
            model = model
        )
    )
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
    data class MessageSent(val messageId: String) : ConversationEvent()
    data class AiResponseReceived(val messageId: String) : ConversationEvent()
    data object RequestExpertReviewSuccess : ConversationEvent()
    data object InsufficientPoints : ConversationEvent()
}

// Custom Exceptions
class NetworkException : Exception("네트워크 연결을 확인해주세요")
class AIServiceException : Exception("AI 서비스 응답 중 오류가 발생했습니다")