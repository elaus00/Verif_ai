package mp.verif_ai.domain.model.notification

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val priority: Int,
    val userId: String,
    val groupId: String?, // For grouping related notifications
    val deepLink: String,
    val metadata: Map<String, Any>?
)

