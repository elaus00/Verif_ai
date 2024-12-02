package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                inboxRepository.observeNotifications()
                    .catch { e ->
                        _uiState.value = InboxUiState.Error(e.message ?: "Unknown error")
                    }
                    .collect { notifications ->
                        _uiState.value = InboxUiState.Success(notifications)
                    }
            } catch (e: Exception) {
                _uiState.value = InboxUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearAll(userId: String) {
        viewModelScope.launch {
            try {
                inboxRepository.clearAllNotifications(userId)
            } catch (e: Exception) {
                _uiState.value = InboxUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                inboxRepository.markAsRead(notificationId)
            } catch (e: Exception) {
                _uiState.value = InboxUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class InboxUiState {
    object Loading : InboxUiState()
    data class Success(val notifications: List<Notification>) : InboxUiState()
    data class Error(val message: String) : InboxUiState()
}
