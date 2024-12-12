package mp.verif_ai.domain.model.expert

import kotlinx.serialization.Serializable

@Serializable
data class ExpertReview(
    val id: String = "",
    val expertId: String = "",
    val content: String = "",
    val rating: Int = 0,
    val timestamp: Long = 0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "expertId" to expertId,
        "content" to content,
        "rating" to rating,
        "timestamp" to timestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): ExpertReview {
            return ExpertReview(
                id = map["id"] as? String ?: "",
                expertId = map["expertId"] as? String ?: "",
                content = map["content"] as? String ?: "",
                rating = (map["rating"] as? Number)?.toInt() ?: 0,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0
            )
        }
    }
}
