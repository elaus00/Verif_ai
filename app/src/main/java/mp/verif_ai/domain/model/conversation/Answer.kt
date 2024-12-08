package mp.verif_ai.domain.model.conversation

import java.util.UUID

data class Answer(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val authorId: String,
    val authorName: String,
    val isExpertAnswer: Boolean = false,
    val helpfulCount: Int = 0,
    val commentCount: Int = 0,
    val isVerified: Boolean = false,
    val references: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: AnswerStatus = AnswerStatus.ACTIVE
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "content" to content,
        "authorId" to authorId,
        "authorName" to authorName,
        "isExpertAnswer" to isExpertAnswer,
        "helpfulCount" to helpfulCount,
        "commentCount" to commentCount,
        "isVerified" to isVerified,
        "references" to references,
        "attachments" to attachments,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "status" to status.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Answer = Answer(
            id = map["id"] as? String ?: UUID.randomUUID().toString(),
            content = map["content"] as? String ?: "",
            authorId = map["authorId"] as? String ?: "",
            authorName = map["authorName"] as? String ?: "",
            isExpertAnswer = map["isExpertAnswer"] as? Boolean ?: false,
            helpfulCount = (map["helpfulCount"] as? Number)?.toInt() ?: 0,
            commentCount = (map["commentCount"] as? Number)?.toInt() ?: 0,
            isVerified = map["isVerified"] as? Boolean ?: false,
            references = (map["references"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            attachments = (map["attachments"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            status = AnswerStatus.fromString(map["status"] as? String ?: "")
        )
    }
}

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