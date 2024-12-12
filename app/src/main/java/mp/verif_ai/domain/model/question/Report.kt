package mp.verif_ai.domain.model.report

import java.util.UUID

data class Report(
    val id: String = UUID.randomUUID().toString(),
    val targetId: String,
    val targetType: ReportTargetType,
    val reason: ReportReason,
    val additionalComment: String,
    val reporterId: String,
    val status: ReportStatus = ReportStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolvedBy: String? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "targetId" to targetId,
        "targetType" to targetType.name,
        "reason" to reason.name,
        "additionalComment" to additionalComment,
        "reporterId" to reporterId,
        "status" to status.name,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "resolvedAt" to resolvedAt,
        "resolvedBy" to resolvedBy
    )
}

enum class ReportTargetType {
    QUESTION,
    ANSWER,
    COMMENT,
    USER
}

enum class ReportReason {
    SPAM,
    HARASSMENT,
    HATE_SPEECH,
    INAPPROPRIATE_CONTENT,
    MISINFORMATION,
    COPYRIGHT_VIOLATION,
    OTHER;

    fun getDisplayName(): String = when(this) {
        SPAM -> "스팸"
        HARASSMENT -> "괴롭힘"
        HATE_SPEECH -> "혐오 발언"
        INAPPROPRIATE_CONTENT -> "부적절한 콘텐츠"
        MISINFORMATION -> "허위 정보"
        COPYRIGHT_VIOLATION -> "저작권 침해"
        OTHER -> "기타"
    }
}

enum class ReportStatus {
    PENDING,    // 검토 대기중
    REVIEWING,  // 검토중
    RESOLVED,   // 해결됨
    DISMISSED,  // 기각됨
    ESCALATED   // 상위 검토 필요
}