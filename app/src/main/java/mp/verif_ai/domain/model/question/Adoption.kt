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
        const val EXPERT_REVIEW_POINTS = 0

        fun fromMap(map: Map<String, Any?>): Adoption {
            return Adoption(
                expertId = map["expertId"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                points = (map["points"] as? Number)?.toInt() ?: EXPERT_REVIEW_POINTS
            )
        }
    }
}