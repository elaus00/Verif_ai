package mp.verif_ai.domain.model.question

import kotlinx.serialization.Serializable

@Serializable
data class Adoption(
    val expertId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val points: Int = EXPERT_REVIEW_POINTS
) {
    fun toMap(): Map<String, Any> = mapOf(
        "expertId" to expertId,
        "timestamp" to timestamp,
        "points" to points
    )

    companion object {
        const val EXPERT_REVIEW_POINTS = 100    // 전문가가 받는 기본 포인트
        const val QUESTION_BASE_POINTS = 50     // 질문 등록 시 필요한 최소 포인트
        const val MIN_POINTS = 50               // 설정 가능한 최소 포인트
        const val MAX_POINTS = 1000             // 설정 가능한 최대 포인트
        const val REPORT_THRESHOLD = 5          // 신고 임계값

        fun fromMap(map: Map<String, Any?>): Adoption {
            return Adoption(
                expertId = map["expertId"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                points = (map["points"] as? Number)?.toInt() ?: EXPERT_REVIEW_POINTS
            )
        }
    }
}