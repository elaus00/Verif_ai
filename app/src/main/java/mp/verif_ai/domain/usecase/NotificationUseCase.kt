package mp.verif_ai.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Inject

class MarkNotificationsAsReadUseCase @Inject constructor(
    private val repository: InboxRepository
) {
    suspend operator fun invoke(notificationIds: List<String>) {
        repository.markAsRead(notificationIds)
    }
}

class GetGroupedNotificationsUseCase @Inject constructor(
    private val repository: InboxRepository
) {
    operator fun invoke(groupId: String): Flow<List<Notification>> =
        repository.getGroupedNotifications(groupId)
}

class GetNotificationsUseCase @Inject constructor(
    private val inboxRepository: InboxRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(pageSize: Int): Flow<List<Notification>> = flow {
        // 현재 사용자 ID 가져오기
        val userId = authRepository.getCurrentUser()?.id

        // 페이징된 알림 목록 조회
        emitAll(inboxRepository.getNotifications(userId.toString(), 0, pageSize))
    }
}

class DeleteNotificationsUseCase @Inject constructor(
    private val inboxRepository: InboxRepository
) {
    suspend operator fun invoke(notificationIds: List<String>) {
        inboxRepository.deleteNotifications(notificationIds)
    }
}

class GetUnreadCountUseCase @Inject constructor(
    private val inboxRepository: InboxRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Int> = flow {
        val userId = authRepository.getCurrentUser()?.id
        emitAll(inboxRepository.getUnreadCount(userId.toString()))
    }
}

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val inboxRepository: InboxRepository
) {
    suspend operator fun invoke() {
        inboxRepository.markAllAsRead()
    }
}

class ClearAllNotificationsUseCase @Inject constructor(
    private val inboxRepository: InboxRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        val userId = authRepository.getCurrentUser()?.id
        inboxRepository.clearAllNotifications(userId.toString())
    }
}
