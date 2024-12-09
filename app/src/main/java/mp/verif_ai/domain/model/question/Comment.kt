package mp.verif_ai.domain.model.question

import java.util.UUID

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val parentId: String = "",            // 질문 ID 또는 답변 ID
    val parentType: CommentParentType = CommentParentType.QUESTION,
    val reportCount: Int = 0,
    val isReported: Boolean = false,
    val status: CommentStatus = CommentStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class CommentParentType {
    QUESTION,
    ANSWER
}

enum class CommentStatus {
    ACTIVE,
    DELETED,
    HIDDEN
}