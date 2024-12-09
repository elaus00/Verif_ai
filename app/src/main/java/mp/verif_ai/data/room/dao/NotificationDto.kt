package mp.verif_ai.data.room.dao

import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.NotificationType
import mp.verif_ai.domain.room.NotificationEntity
import mp.verif_ai.domain.util.JsonUtils.gson
import java.util.UUID

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @Json(name = "type")
    val type: String,

    @Json(name = "type_metadata")
    val typeMetadata: Map<String, String>?, // 타입별 추가 데이터

    @Json(name = "title")
    val title: String,

    @Json(name = "content")
    val content: String,

    @Json(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @Json(name = "is_read")
    val isRead: Boolean = false,

    @Json(name = "priority")
    val priority: Int = 0,

    @Json(name = "user_id")
    val userId: String,

    @Json(name = "group_id")
    val groupId: String? = null,

    @Json(name = "deep_link")
    val deepLink: String? = null,

    @Json(name = "metadata")
    val metadata: Map<String, Any>? = null
) {
    fun toEntity(): NotificationEntity = NotificationEntity(
        id = id,
        type = type.lowercase(),
        typeMetadata = typeMetadata?.let { gson.toJson(it) } ?: "{}",
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        priority = priority,
        userId = userId,
        groupId = groupId,
        deepLink = deepLink ?: "",
        metadata = metadata
    )
}

// NotificationEntity의 toDomain() 수정
fun NotificationEntity.toDomain(): Notification {
    val typeMetadataMap = try {
        gson.fromJson<Map<String, String>>(typeMetadata, mapTypeToken)
    } catch (e: Exception) {
        emptyMap()
    }

    return Notification(
        id = id,
        type = when (type.lowercase()) {
            "reply" -> NotificationType.Reply(
                questionId = typeMetadataMap["questionId"] ?: "",
                replyId = typeMetadataMap["replyId"] ?: "",
                replyContent = typeMetadataMap["replyContent"] ?: ""
            )
            "like" -> NotificationType.Like(
                targetId = typeMetadataMap["targetId"] ?: "",
                targetType = typeMetadataMap["targetType"] ?: ""
            )
            "comment" -> NotificationType.Comment(
                targetId = typeMetadataMap["targetId"] ?: "",
                targetType = typeMetadataMap["targetType"] ?: ""
            )
            else -> NotificationType.System(
                actionType = typeMetadataMap["actionType"] ?: "unknown",
                metadata = typeMetadataMap
            )
        },
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

private val mapTypeToken = object : TypeToken<Map<String, String>>() {}.type


