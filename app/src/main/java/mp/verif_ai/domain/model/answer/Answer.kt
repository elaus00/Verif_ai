package mp.verif_ai.domain.model.answer

import kotlinx.serialization.Serializable
import mp.verif_ai.domain.model.question.Comment
import java.util.UUID

@Serializable
data class Answer(
    val id: String = UUID.randomUUID().toString(),
    val questionId: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val expertId: String? = null,
    val isExpertAnswer: Boolean = false,
    val helpfulCount: Int = 0,
    val commentCount: Int = 0,
    val comments: List<Comment> = emptyList(),
    val isVerified: Boolean = false,
    val isAdopted: Boolean = false,
    val adoptedAt: Long? = null,
    val references: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val reportCount: Int = 0,
    val isReported: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: AnswerStatus = AnswerStatus.ACTIVE
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "questionId" to questionId,
        "content" to content,
        "authorId" to authorId,
        "authorName" to authorName,
        "expertId" to expertId,
        "isExpertAnswer" to isExpertAnswer,
        "helpfulCount" to helpfulCount,
        "commentCount" to commentCount,
        "comments" to comments,
        "isVerified" to isVerified,
        "isAdopted" to isAdopted,
        "adoptedAt" to adoptedAt,
        "references" to references,
        "attachments" to attachments,
        "reportCount" to reportCount,
        "isReported" to isReported,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "status" to status.name
    )

    companion object {
        const val MAX_CONTENT_LENGTH = 5000
        const val MAX_REFERENCES = 10
        const val MAX_ATTACHMENTS = 5
        const val REPORT_THRESHOLD = 5

        fun fromMap(map: Map<String, Any?>): Answer = Answer(
            id = map["id"] as? String ?: UUID.randomUUID().toString(),
            questionId = map["questionId"] as? String ?: "",
            content = map["content"] as? String ?: "",
            authorId = map["authorId"] as? String ?: "",
            authorName = map["authorName"] as? String ?: "",
            expertId = map["expertId"] as? String,
            isExpertAnswer = map["isExpertAnswer"] as? Boolean ?: false,
            helpfulCount = (map["helpfulCount"] as? Number)?.toInt() ?: 0,
            commentCount = (map["commentCount"] as? Number)?.toInt() ?: 0,
            comments = (map["comments"] as? List<Map<String, Any?>>)?.map { Comment.fromMap(it) } ?: emptyList(),
            isVerified = map["isVerified"] as? Boolean ?: false,
            isAdopted = map["isAdopted"] as? Boolean ?: false,
            adoptedAt = (map["adoptedAt"] as? Number)?.toLong(),
            references = (map["references"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            attachments = (map["attachments"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            reportCount = (map["reportCount"] as? Number)?.toInt() ?: 0,
            isReported = map["isReported"] as? Boolean ?: false,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            status = AnswerStatus.fromString(map["status"] as? String ?: "")
        )
    }
}

@Serializable
enum class AnswerStatus {
    ACTIVE,     // 활성 상태
    DELETED,    // 삭제됨
    HIDDEN,     // 숨겨짐 (신고 등으로 인해)
    PENDING;    // 검토 대기중

    companion object {
        fun fromString(value: String): AnswerStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            ACTIVE
        }
    }
}