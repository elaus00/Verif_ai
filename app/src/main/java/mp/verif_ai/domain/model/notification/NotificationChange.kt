package mp.verif_ai.domain.model.notification

sealed class NotificationChange {
    data class Created(
        val notification: Notification,
        val timestamp: Long = System.currentTimeMillis()
    ) : NotificationChange()

    data class Updated(
        val notificationId: String,
        val changes: Map<String, Any>,
        val timestamp: Long = System.currentTimeMillis()
    ) : NotificationChange()

    data class Deleted(
        val notificationId: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : NotificationChange()

    data class ReadStatusChanged(
        val notificationId: String,
        val isRead: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    ) : NotificationChange()
}