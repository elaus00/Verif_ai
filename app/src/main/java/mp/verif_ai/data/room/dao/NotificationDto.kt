package mp.verif_ai.data.room.dao

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.NotificationType
import mp.verif_ai.domain.room.NotificationEntity
import java.util.UUID

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @Json(name = "type")
    val type: String,

    @Json(name = "type_metadata")
    val typeMetadata: Map<String, Any>?,

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
        typeMetadata = gson.toJson(typeMetadata),
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

    companion object {
        private val gson = Gson()
    }
}

fun NotificationEntity.toDomain(): Notification {
    val typeMetadataMap = try {
        gson.fromJson<Map<String, Any>>(typeMetadata, mapType)
    } catch (e: Exception) {
        emptyMap()
    }

    val notificationType = when (type.lowercase()) {
        "answer" -> NotificationType.Answer(
            questionId = (typeMetadataMap["questionId"] as? String) ?: "",
            answerId = (typeMetadataMap["answerId"] as? String) ?: "",
            answerContent = (typeMetadataMap["answerContent"] as? String) ?: "",
            expertId = (typeMetadataMap["expertId"] as? String) ?: "",
            expertName = (typeMetadataMap["expertName"] as? String) ?: ""
        )
        "adoption" -> NotificationType.Adoption(
            questionId = (typeMetadataMap["questionId"] as? String) ?: "",
            answerId = (typeMetadataMap["answerId"] as? String) ?: "",
            points = (typeMetadataMap["points"] as? Double)?.toInt() ?: 0
        )
        "comment" -> NotificationType.Comment(
            targetId = (typeMetadataMap["targetId"] as? String) ?: "",
            targetType = (typeMetadataMap["targetType"] as? String) ?: "",
            commentId = (typeMetadataMap["commentId"] as? String) ?: "",
            commentContent = (typeMetadataMap["commentContent"] as? String) ?: ""
        )
        "like" -> NotificationType.Like(
            targetId = (typeMetadataMap["targetId"] as? String) ?: "",
            targetType = (typeMetadataMap["targetType"] as? String) ?: "",
            userId = (typeMetadataMap["userId"] as? String) ?: "",
            userName = (typeMetadataMap["userName"] as? String) ?: ""
        )
        "point" -> NotificationType.Point(
            amount = (typeMetadataMap["amount"] as? Double)?.toInt() ?: 0,
            type = (typeMetadataMap["type"] as? String) ?: "",
            reason = (typeMetadataMap["reason"] as? String) ?: ""
        )
        else -> NotificationType.System(
            actionType = (typeMetadataMap["actionType"] as? String) ?: "unknown",
            metadata = typeMetadataMap.mapValues { it.value.toString() }
        )
    }

    return Notification(
        id = id,
        type = notificationType,
        title = title,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        priority = priority,
        userId = userId,
        groupId = groupId,
        deepLink = deepLink,
        metadata = metadata?.mapValues { it.value.toString() }
    )
}

private val gson = Gson()
private val mapType = object : TypeToken<Map<String, String>>() {}.type