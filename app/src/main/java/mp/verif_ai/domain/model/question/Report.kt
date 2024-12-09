package mp.verif_ai.domain.model.question

import java.util.UUID

data class Report(
    val id: String = UUID.randomUUID().toString(),
    val reporterId: String = "",          // 신고자 ID
    val targetId: String = "",            // 신고 대상 ID
    val targetType: ReportTargetType = ReportTargetType.QUESTION,
    val reason: String = "",              // 신고 사유
    val status: ReportStatus = ReportStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReportTargetType {
    QUESTION,
    ANSWER,
    COMMENT
}

enum class ReportStatus {
    PENDING,    // 검토 대기
    ACCEPTED,   // 승인됨
    REJECTED    // 거절됨
}