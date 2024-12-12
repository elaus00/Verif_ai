package mp.verif_ai.domain.model.question

import mp.verif_ai.domain.model.answer.Answer

data class TrendingQuestion(
    val id: String,
    val title: String,
    val viewCount: Int,
    val commentCount: Int,
    val status: QuestionStatus = QuestionStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val authorId: String = "",
    val authorName: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val content: String = "",
    val aiConversationId: String? = null,
    val answers: List<Answer> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val selectedAnswerId: String? = null,
    val points: Int = Adoption.EXPERT_REVIEW_POINTS,
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
        "comments" to comments.map { it.toMap() },
        "selectedAnswerId" to selectedAnswerId,
        "status" to status.name,
        "points" to points,
        "viewCount" to viewCount,
        "commentCount" to commentCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}