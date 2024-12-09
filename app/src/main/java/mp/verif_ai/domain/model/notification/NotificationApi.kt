package mp.verif_ai.domain.model.notification

import mp.verif_ai.data.room.dao.NotificationDto

interface NotificationApi {
    suspend fun getNotifications(userId: String, page: Int, pageSize: Int): List<NotificationDto>
    suspend fun markAsRead(notificationIds: List<String>)
    suspend fun markAllAsRead()
    suspend fun deleteNotifications(notificationIds: List<String>)
    suspend fun clearAllNotifications(userId: String)
    suspend fun getChangesSince(timestamp: Long): List<NotificationChange>
    suspend fun applyChanges(changes: List<NotificationChange>)
}