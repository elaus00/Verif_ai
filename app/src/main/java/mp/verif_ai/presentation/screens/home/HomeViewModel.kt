package mp.verif_ai.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationType
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.QuestionRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val questionRepository: QuestionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recentConversations = MutableStateFlow<List<Conversation>>(emptyList())
    val recentConversations: StateFlow<List<Conversation>> = _recentConversations

    private val _trendingQuestions = MutableStateFlow<List<TrendingQuestion>>(emptyList())
    val trendingQuestions: StateFlow<List<TrendingQuestion>> = _trendingQuestions

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id

            // Load AI conversations
            conversationRepository.getConversationHistory(userId.toString(), limit = 5, offset = 0)
                .getOrNull()
                ?.filter { it.type == ConversationType.AI_CHAT }
                ?.let { conversations ->
                    _recentConversations.value = conversations
                }

            // Load trending questions
            questionRepository.getTrendingQuestions(limit = 5)
                .collect { questions ->
                    _trendingQuestions.value = questions
                }
        }
    }
}