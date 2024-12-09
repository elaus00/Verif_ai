package mp.verif_ai.data.room.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.NotificationChange
import mp.verif_ai.domain.model.notification.NotificationType
import mp.verif_ai.domain.room.NotificationEntity
import mp.verif_ai.domain.util.JsonUtils.gson

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

fun Notification.toEntity(): NotificationEntity {
    val (typeString, metadata) = when (val notificationType = type) {
        is NotificationType.Answer -> "ANSWER" to mapOf(
            "questionId" to notificationType.questionId,
            "answerId" to notificationType.answerId,
            "answerContent" to notificationType.answerContent,
            "expertId" to notificationType.expertId,
            "expertName" to notificationType.expertName
        )
        is NotificationType.Adoption -> "ADOPTION" to mapOf(
            "questionId" to notificationType.questionId,
            "answerId" to notificationType.answerId,
            "points" to notificationType.points.toString()
        )
        is NotificationType.Comment -> "COMMENT" to mapOf(
            "targetId" to notificationType.targetId,
            "targetType" to notificationType.targetType,
            "commentId" to notificationType.commentId,
            "commentContent" to notificationType.commentContent
        )
        is NotificationType.Like -> "LIKE" to mapOf(
            "targetId" to notificationType.targetId,
            "targetType" to notificationType.targetType,
            "userId" to notificationType.userId,
            "userName" to notificationType.userName
        )
        is NotificationType.Point -> "POINT" to mapOf(
            "amount" to notificationType.amount.toString(),
            "type" to notificationType.type,
            "reason" to notificationType.reason
        )
        is NotificationType.System -> "SYSTEM" to notificationType.metadata
    }

    return NotificationEntity(
        id = id,
        type = typeString,
        typeMetadata = gson.toJson(metadata),  // Converters에서 정의한 gson 사용
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        priority = priority,
        userId = userId,
        groupId = groupId,
        deepLink = deepLink,
        metadata = metadata
    )
}
