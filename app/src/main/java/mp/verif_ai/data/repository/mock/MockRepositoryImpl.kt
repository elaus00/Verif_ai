package mp.verif_ai.data.repository.mock

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mp.verif_ai.domain.model.Notification
import mp.verif_ai.domain.model.NotificationType
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockInboxRepositoryImpl @Inject constructor() : InboxRepository {
    private val mockNotifications = mutableListOf<Notification>().apply {
        add(
            Notification(
                id = "1",
                userId = "currentUser",
                title = "New Answer Received",
                content = "David has answered your question 'What is the best programming language?'",
                type = NotificationType.ANSWER,
                isRead = false,
                deepLink = "question/q1",
                createdAt = System.currentTimeMillis() - (6 * 60 * 60 * 1000) // 6 hours ago
            )
        )
        add(
            Notification(
                id = "2",
                userId = "currentUser",
                title = "New Chat Message",
                content = "Sarah has sent you a new message.",
                type = NotificationType.CHAT,
                isRead = false,
                deepLink = "chat/sarah",
                createdAt = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000) // 3 days ago
            )
        )
        add(
            Notification(
                id = "3",
                userId = "currentUser",
                title = "Points Earned",
                content = "You earned 50 points because Seoyoung accepted your answer.",
                type = NotificationType.POINT,
                isRead = false,
                deepLink = "point/history",
                createdAt = System.currentTimeMillis() - (22 * 24 * 60 * 60 * 1000) // 22 days ago
            )
        )
        add(
            Notification(
                id = "4",
                userId = "currentUser",
                title = "New Question in Your Field",
                content = "A new question about 'Machine Learning Basics' has been posted in your area of expertise.",
                type = NotificationType.QUESTION,
                isRead = false,
                deepLink = "question/q4",
                createdAt = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000) // 1 day ago
            )
        )
        add(
            Notification(
                id = "5",
                userId = "currentUser",
                title = "System Update",
                content = "We've updated our privacy policy. Please review the changes.",
                type = NotificationType.SYSTEM,
                isRead = false,
                deepLink = "settings/privacy",
                createdAt = System.currentTimeMillis() - (12 * 60 * 60 * 1000) // 12 hours ago
            )
        )
    }

    override suspend fun getNotifications(): List<Notification> = mockNotifications

    override suspend fun markAsRead(notificationId: String) {
        val index = mockNotifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            mockNotifications[index] = mockNotifications[index].copy(isRead = true)
        }
    }

    override suspend fun markAllAsRead() {
        mockNotifications.replaceAll { it.copy(isRead = true) }
    }

    override suspend fun deleteNotification(notificationId: String) {
        mockNotifications.removeAll { it.id == notificationId }
    }

    override fun observeNotifications(): Flow<List<Notification>> = flow {
        emit(mockNotifications)
    }

    override suspend fun getUnreadCount(): Int =
        mockNotifications.count { !it.isRead }

    suspend fun clearAllNotifications(userId: String) {
        mockNotifications.clear()
    }

    suspend fun getUnreadCount(userId: String): Flow<Int> = flow {
        emit(mockNotifications.count { !it.isRead })
    }
}