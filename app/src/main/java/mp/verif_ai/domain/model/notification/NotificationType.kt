package mp.verif_ai.domain.model.notification

import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationType {
    data class Answer(
        val questionId: String,
        val answerId: String,
        val answerContent: String,
        val expertId: String,
        val expertName: String
    ) : NotificationType()

    data class Adoption(
        val questionId: String,
        val answerId: String,
        val points: Int
    ) : NotificationType()

    data class Comment(
        val targetId: String,
        val targetType: String,  // "QUESTION" or "ANSWER"
        val commentId: String,
        val commentContent: String
    ) : NotificationType()

    data class Like(
        val targetId: String,
        val targetType: String,  // "ANSWER" or "COMMENT"
        val userId: String,
        val userName: String
    ) : NotificationType()

    data class Point(
        val amount: Int,
        val type: String,  // "EARNED" or "SPENT"
        val reason: String
    ) : NotificationType()

    data class System(
        val actionType: String,
        val metadata: Map<String, String>
    ) : NotificationType()
}