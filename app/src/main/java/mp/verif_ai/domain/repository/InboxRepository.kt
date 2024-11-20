package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.Notification

interface InboxRepository {
    suspend fun getNotifications(): List<Notification>
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: String)
    fun observeNotifications(): Flow<List<Notification>>
    suspend fun getUnreadCount(): Int
    suspend fun clearAllNotifications(userId: String)
    suspend fun getUnreadCount(userId: String): Flow<Int>
}