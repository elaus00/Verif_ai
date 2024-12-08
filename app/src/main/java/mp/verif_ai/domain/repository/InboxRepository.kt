package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.Notification

interface InboxRepository {
    suspend fun getNotifications(): Flow<List<Notification>> // 변경된 반환 타입
    suspend fun insertMockData()
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: String)
    fun observeNotifications(): Flow<List<Notification>>
    suspend fun getUnreadCount(): Int
}