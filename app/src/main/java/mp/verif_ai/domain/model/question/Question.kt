package mp.verif_ai.domain.model.question

import mp.verif_ai.domain.model.conversation.Answer
import java.util.UUID

data class Question(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val content: String = "",
    val aiConversationId: String? = null,
    val authorId: String = "",
    val authorName: String = "",  // 작성자 이름 추가
    val answers: List<Answer> = emptyList(), // 답변 목록 추가
    val selectedAnswerId: String? = null, // 채택된 답변 ID 추가
    val status: QuestionStatus = QuestionStatus.OPEN,
    val points: Int = Adoption.EXPERT_REVIEW_POINTS,
    val viewCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "category" to category,
        "tags" to tags,
        "content" to content,
        "aiConversationId" to aiConversationId,
        "authorId" to authorId,
        "authorName" to authorName,
        "answers" to answers.map { it.toMap() },
        "selectedAnswerId" to selectedAnswerId,
        "status" to status.name,
        "points" to points,
        "viewCount" to viewCount,
        "commentCount" to commentCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

enum class QuestionStatus {
    OPEN,      // 답변 받기 가능
    CLOSED,    // 답변 채택 완료
    IN_PROGRESS, // 답변 중
    EXPIRED,   // 기간 만료
    DELETED    // 삭제됨
}