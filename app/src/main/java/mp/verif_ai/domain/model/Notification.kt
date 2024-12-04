package mp.verif_ai.domain.model

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: NotificationType,
    val isRead: Boolean,
    val deepLink: String?,
    val createdAt: Long
)

enum class NotificationType {
    COMMENT,
    LIKE,
    NEW_QUESTION,
    QUESTION,
    ANSWER,
    CHAT,
    POINT,
    SYSTEM
}