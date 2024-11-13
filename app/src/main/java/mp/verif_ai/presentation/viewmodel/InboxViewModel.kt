package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.Notification
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val inboxRepository: InboxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InboxUiState>(InboxUiState.Loading)
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = InboxUiState.Loading
            try {
                val notifications = inboxRepository.getNotifications()
                _uiState.value = if (notifications.isEmpty()) {
                    InboxUiState.Empty
                } else {
                    InboxUiState.Success(notifications)
                }
            } catch (e: Exception) {
                _uiState.value = InboxUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
            }
        }
    }

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
)