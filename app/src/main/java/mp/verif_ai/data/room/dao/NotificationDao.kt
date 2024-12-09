package mp.verif_ai.data.room.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.NotificationChange
import mp.verif_ai.domain.model.notification.NotificationType
import mp.verif_ai.domain.room.NotificationEntity

@Dao
interface NotificationDao {
    @Transaction
    @Query("""
        SELECT * FROM notifications 
        WHERE userId = :userId 
        ORDER BY 
            CASE WHEN isRead = 0 THEN 0 ELSE 1 END,
            timestamp DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPagedNotifications(
        userId: String,
        limit: Int,
        offset: Int
    ): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE groupId = :groupId")
    fun getGroupedNotifications(groupId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = :isRead WHERE id IN (:ids)")
    suspend fun updateReadStatus(ids: List<String>, isRead: Boolean)

    @Query("DELETE FROM notifications WHERE id IN (:ids)")
    suspend fun deleteNotifications(ids: List<String>)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCountByUserId(userId: String): Flow<Int>

    @Query("SELECT * FROM notifications WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getNotificationsByGroupId(groupId: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = :isRead WHERE id IN (:notificationIds)")
    suspend fun updateNotificationsReadStatus(notificationIds: List<String>, isRead: Boolean)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id IN (:notificationIds)")
    suspend fun deleteNotificationsByIds(notificationIds: List<String>)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllNotificationsByUserId(userId: String)

    @Query("SELECT * FROM notifications WHERE timestamp > :timestamp")
    suspend fun getChangesSince(timestamp: Long): List<NotificationEntity>

    @Query("SELECT id FROM notifications")
    suspend fun getAllNotificationIds(): List<String>

    @Transaction
    suspend fun applyChanges(changes: List<NotificationChange>) {
        // 변경사항 적용 로직
    }

    @Upsert  // Room 2.5.0 이상에서 사용 가능
    suspend fun upsertNotifications(notifications: List<NotificationEntity>)

    // 단일 알림에 대한 upsert
    @Upsert
    suspend fun upsertNotification(notification: NotificationEntity)

    @Transaction
    suspend fun syncNotifications(notifications: List<NotificationEntity>) {
        val existingIds = getAllNotificationIds()
        val incomingIds = notifications.map { it.id }

        // 삭제된 알림 제거
        val idsToDelete = existingIds - incomingIds.toSet()
        if (idsToDelete.isNotEmpty()) {
            deleteNotificationsByIds(idsToDelete.toList())
        }

        // 새로운/업데이트된 알림 upsert
        upsertNotifications(notifications)
    }

}


fun Notification.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp,
    isRead = isRead,
    type = when (type) {
        is NotificationType.Reply -> "REPLY"
        is NotificationType.Like -> "LIKE"
        is NotificationType.System -> "SYSTEM"
        else -> "COMMENT"
    },
    priority = 0,
    userId = userId,
    groupId = null,
    deepLink = "",
    metadata = null,
    typeMetadata = TODO()
)
