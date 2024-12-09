package mp.verif_ai.domain.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: Int = PRIORITY_NORMAL,
    val userId: String,
    val groupId: String? = null,
    val deepLink: String = "",
    val metadata: Map<String, String>? = null
) {
    companion object {
        const val PRIORITY_LOW = 0
        const val PRIORITY_NORMAL = 1
        const val PRIORITY_HIGH = 2
    }
}