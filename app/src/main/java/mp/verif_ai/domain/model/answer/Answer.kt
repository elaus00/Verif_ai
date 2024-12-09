package mp.verif_ai.domain.model.answer

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Answer(
    val id: String = UUID.randomUUID().toString(),
    val questionId: String = "",           // 질문 ID 추가
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val expertId: String? = null,          // 전문가 ID 추가
    val isExpertAnswer: Boolean = false,
    val helpfulCount: Int = 0,
    val commentCount: Int = 0,
    val isVerified: Boolean = false,
    val isAdopted: Boolean = false,        // 채택 여부 추가
    val adoptedAt: Long? = null,           // 채택 시간 추가
    val references: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val reportCount: Int = 0,              // 신고 횟수 추가
    val isReported: Boolean = false,       // 신고 여부 추가
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
        const val MAX_CONTENT_LENGTH = 5000    // 답변 최대 길이
        const val MAX_REFERENCES = 10          // 최대 참조 문헌 수
        const val MAX_ATTACHMENTS = 5          // 최대 첨부 파일 수
        const val REPORT_THRESHOLD = 5         // 신고 임계값

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