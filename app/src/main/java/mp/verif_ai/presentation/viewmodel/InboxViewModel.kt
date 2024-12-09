package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.SwipeAction
import mp.verif_ai.domain.usecase.DeleteNotificationsUseCase
import mp.verif_ai.domain.usecase.GetNotificationsUseCase
import mp.verif_ai.domain.usecase.MarkNotificationsAsReadUseCase
import javax.inject.Inject


@HiltViewModel
class InboxViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markAsReadUseCase: MarkNotificationsAsReadUseCase,
    private val deleteNotificationsUseCase: DeleteNotificationsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<InboxUiState>(InboxUiState.Loading)
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    private val currentPage = MutableStateFlow(0)
    private val isLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                currentPage,
                getNotificationsUseCase(PAGE_SIZE)
            ) { page, notifications ->
                notifications
            }.collect { notifications ->
                _uiState.value = when {
                    notifications.isEmpty() && currentPage.value == 0 -> InboxUiState.Empty
                    else -> InboxUiState.Success(notifications)
                }
            }
        }
    }

    fun loadMore() {
        if (!isLoading.value) {
            viewModelScope.launch {
                isLoading.value = true
                currentPage.value += 1
                isLoading.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            currentPage.value = 0
            // Trigger recomposition
        }
    }

    fun onNotificationSwiped(notification: Notification, action: SwipeAction) {
        viewModelScope.launch {
            when (action) {
                SwipeAction.READ -> markAsReadUseCase(listOf(notification.id))
                SwipeAction.DELETE -> deleteNotificationsUseCase(listOf(notification.id))
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
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
