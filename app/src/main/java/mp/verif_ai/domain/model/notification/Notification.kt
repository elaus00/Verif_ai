package mp.verif_ai.domain.model.notification

data class Notification(
    val id: String = "",
    val type: NotificationType = NotificationType.REPLY,
    val questionId: String = "",  // 연관된 Question ID
    val questionTitle: String = "", // Question 제목
    val actorId: String = "",     // 액션을 취한 사용자 ID
    val actorName: String = "",   // 액션을 취한 사용자 이름
    val receiverId: String = "",  // 알림을 받는 사용자 ID
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType {
    REPLY,    // 답변
    COMMENT,  // 댓글
    UPVOTE    // 좋아요
}