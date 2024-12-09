package mp.verif_ai.domain.model.notification

sealed class NotificationType {
    data class Reply(
        val questionId: String,
        val replyId: String,
        val replyContent: String
    ) : NotificationType()

    data class Like(
        val targetId: String,
        val targetType: String
    ) : NotificationType()

    data class Comment(
        val targetId: String,
        val targetType: String
    ) : NotificationType()

    data class System(
        val actionType: String,
        val metadata: Map<String, String>
    ) : NotificationType()
}