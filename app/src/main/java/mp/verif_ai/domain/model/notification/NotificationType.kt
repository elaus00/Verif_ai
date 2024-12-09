package mp.verif_ai.domain.model.notification

sealed class NotificationType(val type: String) {
    data class Reply(
        val questionId: String,
        val replyId: String,
        val replyContent: String
    ) : NotificationType("reply")

    data class Like(
        val targetId: String,
        val targetType: String
    ) : NotificationType("like")

    data class System(
        val actionType: String,
        val metadata: Map<String, String>
    ) : NotificationType("system")

    data class Comment(
        val targetId: String,
        val targetType: String
    ) : NotificationType("comment")
}
