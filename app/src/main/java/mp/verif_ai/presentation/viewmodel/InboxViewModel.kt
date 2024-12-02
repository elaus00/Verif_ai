package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val inboxRepository: InboxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InboxUiState>(InboxUiState.Loading)
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    // 목 데이터 사용 여부 플래그
    private val useMockData = true

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            inboxRepository.insertMockData() // Insert mock data initially
            inboxRepository.getNotifications().collect { notifications ->
                _uiState.value = if (notifications.isEmpty()) {
                    InboxUiState.Empty
                } else {
                    InboxUiState.Success(notifications)
                }
//            _uiState.value = InboxUiState.Loading
//            try {
//                val notifications = if (useMockData) {
//                    // 목 데이터 생성 (필요한 모든 매개변수 포함)
//                    listOf(
//                        Notification(
//                            id = "1",
//                            title = "What is MVVM?",
//                            content = "Alice replied to your question.",
//                            isRead = false,
//                            createdAt = System.currentTimeMillis(),
//                            deepLink = "https://example.com/question/1",
//                            type = "reply",
//                            userId = "user_1"
//                        ),
//                        Notification(
//                            id = "2",
//                            title = "How to implement Room DB?",
//                            content = "Bob commented on your question.",
//                            isRead = true,
//                            createdAt = System.currentTimeMillis() - 3600000,
//                            deepLink = "https://example.com/question/2",
//                            type = "comment",
//                            userId = "user_2"
//                        ),
//                        Notification(
//                            id = "3",
//                            title = "What is Clean Architecture?",
//                            content = "Charlie upvoted your question.",
//                            isRead = true,
//                            createdAt = System.currentTimeMillis() - 7200000,
//                            deepLink = "https://example.com/question/3",
//                            type = "upvote",
//                            userId = "user_3"
//                        )
//                    )
//                } else {
//                    // 실제 데이터 가져오기
//                    inboxRepository.getNotifications()
//                }
//
//            } catch (e: Exception) {
//                _uiState.value = InboxUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
//            }
        }
    }

//    fun loadNotifications() {
//        viewModelScope.launch {
//            _uiState.value = InboxUiState.Loading
//            try {
//                val notifications = inboxRepository.getNotifications()
//                _uiState.value = if (notifications.isEmpty()) {
//                    InboxUiState.Empty
//                } else {
//                    InboxUiState.Success(notifications)
//                }
//            } catch (e: Exception) {
//                _uiState.value = InboxUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
//            }
//        }
//    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                inboxRepository.markAsRead(notificationId)
                // Reload notifications to reflect changes
                loadNotifications()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

sealed class InboxUiState {
    data object Loading : InboxUiState()
    data object Empty : InboxUiState()
    data class Success(val notifications: List<Notification>) : InboxUiState()
    data class Error(val message: String) : InboxUiState()
}

data class NotificationItem(
    val id: String,
    val questionId: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)}