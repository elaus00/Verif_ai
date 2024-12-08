package mp.verif_ai.presentation.screens.conversation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.PointRepository
import mp.verif_ai.domain.repository.ResponseRepository
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.conversation.factory.ConversationFactory
import mp.verif_ai.presentation.screens.conversation.factory.MessageFactory
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
    private var currentConversation: Conversation? = null

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

    fun startNewConversation() {
        currentConversation = null
        _uiState.value = ConversationUiState.Success(
            messages = emptyList(),
            aiModels = AIModel.entries,
            selectedModel = AIModel.GEMINI_1_5_PRO
        )
    }

    fun loadConversation() {
        viewModelScope.launch {
            try {
                conversationId?.let { id ->
                    conversationRepository.observeConversation(id)
                        .collect { conversation ->
                            currentConversation = conversation
                            updateState { currentState ->
                                currentState.copy(
                                    messages = conversation.messages
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

    fun loadConversationHistory() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading conversation history for user: $userId")
                _uiState.value = ConversationUiState.Loading

                conversationRepository.getConversationHistory(
                    userId = userId,
                    limit = HISTORY_PAGE_SIZE,
                    offset = 0
                ).onSuccess { conversations ->
                    Log.d(TAG, "Loaded ${conversations.size} conversations")
                    updateState { currentState ->
                        currentState.copy(
                            conversations = conversations,
                            filteredConversations = if (currentState.searchQuery.isBlank()) {
                                conversations
                            } else {
                                conversations.filter { conversation ->
                                    conversation.title.contains(currentState.searchQuery, ignoreCase = true) ||
                                            conversation.messages.any { it.content.contains(currentState.searchQuery, ignoreCase = true) }
                                }
                            }
                        )
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Failed to load conversation history", e)
                    handleError("대화 목록을 불러오는데 실패했습니다", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadConversationHistory", e)
                handleError("대화 목록을 불러오는데 실패했습니다", e)
            }
        }
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

                if (currentConversation == null) {
                    // 새 대화 생성
                    val newConversation = ConversationFactory.createNewConversation(
                        userMessage = userMessage,
                        userId = userId
                    )

                    conversationRepository.createConversation(newConversation)
                        .onSuccess {
                            currentConversation = newConversation
                            // UI 업데이트를 위해 사용자 메시지도 추가
                            updateMessages(userMessage)
                            handleAiResponse(inputContent, currentState.selectedModel)
                        }
                        .onFailure { e ->
                            handleError("대화 생성에 실패했습니다", e)
                        }
                } else {
                    // 기존 대화 업데이트
                    val conversation = currentConversation?.copy(
                        messages = currentConversation?.messages.orEmpty() + userMessage,
                        updatedAt = System.currentTimeMillis()
                    ) ?: return@launch

                    conversationRepository.updateConversation(conversation)
                        .onSuccess {
                            currentConversation = conversation
                            // UI 업데이트를 위해 사용자 메시지도 추가
                            updateMessages(userMessage)
                            handleAiResponse(inputContent, currentState.selectedModel)
                        }
                        .onFailure { e ->
                            handleError("메시지 전송에 실패했습니다", e)
                        }
                }
            } catch (e: Exception) {
                handleError("메시지 전송에 실패했습니다", e)
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

    private suspend fun handleAiResponse(userContent: String, model: AIModel?) {
        if (model == null) return

        try {
            setAiResponding(true)
            var aiMessageContent = ""

            responseRepository.getAiResponse(model, userContent)
                .catch { e ->
                    handleError("AI 응답 처리 중 오류가 발생했습니다", e)
                }
                .onCompletion { error ->
                    setAiResponding(false)
                    if (error == null && aiMessageContent.isNotEmpty()) {
                        val aiMessage = MessageFactory.createMessage(
                            content = aiMessageContent,
                            type = SourceType.AI,
                            senderId = "assistant",
                            model = model
                        )

                        val conversation = currentConversation?.copy(
                            messages = currentConversation?.messages.orEmpty() + aiMessage,
                            updatedAt = System.currentTimeMillis()
                        ) ?: return@onCompletion

                        conversationRepository.updateConversation(conversation)
                            .onSuccess {
                                currentConversation = conversation
                                _events.emit(ConversationEvent.AiResponseReceived(aiMessage.id))
                            }
                            .onFailure { e ->
                                handleError("AI 응답 저장에 실패했습니다", e)
                            }
                    }
                }
                .collect { response ->
                    aiMessageContent += response
                    // UI 업데이트용 임시 메시지
                    updateMessages(
                        MessageFactory.createMessage(
                            content = aiMessageContent,
                            type = SourceType.AI,
                            senderId = "assistant",
                            model = model
                        ),
                        isAiMessage = true
                    )
                }
        } catch (e: Exception) {
            handleError("AI 응답 처리 중 오류가 발생했습니다", e)
        }
    }

    fun searchConversations(query: String) {
        viewModelScope.launch {
            updateState { currentState ->
                val filtered = if (query.isBlank()) {
                    currentState.conversations
                } else {
                    currentState.conversations.filter { conversation ->
                        conversation.title.contains(query, ignoreCase = true) ||
                                conversation.messages.any { it.content.contains(query, ignoreCase = true) }
                    }
                }
                currentState.copy(
                    searchQuery = query,
                    filteredConversations = filtered
                )
            }
        }
    }

    private fun setAiResponding(responding: Boolean) {
        updateState { it.copy(isAiResponding = responding) }
    }

    private suspend fun updateMessages(message: Message, isAiMessage: Boolean = false) {
        updateState { state ->
            val updatedMessages = state.messages.toMutableList()

            if (isAiMessage && state.isAiResponding) {
                if (updatedMessages.lastOrNull()?.messageSource?.type == SourceType.AI) {
                    updatedMessages[updatedMessages.lastIndex] = message
                } else {
                    updatedMessages.add(message)
                }
            } else {
                updatedMessages.add(message)
            }

            state.copy(
                messages = updatedMessages,
                isAiResponding = isAiMessage
            )
        }
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

    fun getCurrentConversation(): Conversation? = currentConversation

    fun retry() {
        loadConversation()
    }

    companion object {
        private const val TAG = "ConversationVM"
        private const val HISTORY_PAGE_SIZE = 20
    }
}

sealed class ConversationUiState {
    data object Loading : ConversationUiState()
    data class Success(
        val messages: List<Message> = emptyList(),
        val conversations: List<Conversation> = emptyList(),
        val aiModels: List<AIModel> = emptyList(),
        val selectedModel: AIModel? = null,
        val isAiResponding: Boolean = false,
        val expertReviews: List<ExpertReview> = emptyList(),
        val canRequestExpertReview: Boolean = true,
        val pointBalance: Int = 1000,
        val searchQuery: String = "",
        val filteredConversations: List<Conversation> = emptyList()
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

class NetworkException : Exception("네트워크 연결을 확인해주세요")
class AIServiceException : Exception("AI 서비스 응답 중 오류가 발생했습니다")