package mp.verif_ai.domain.model.payment

import kotlinx.serialization.Serializable


@Serializable
data class Point(
    val userId: String,
    val balance: Int = 0,
    val totalEarned: Int = 0,      // 총 획득한 포인트
    val totalSpent: Int = 0,       // 총 사용한 포인트
    val lastUpdated: Long = System.currentTimeMillis(),
    val history: List<PointTransaction> = emptyList()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "balance" to balance,
        "totalEarned" to totalEarned,
        "totalSpent" to totalSpent,
        "lastUpdated" to lastUpdated
    )

    companion object {
        const val INITIAL_BALANCE = 0
        const val MIN_BALANCE = 0
        const val MAX_BALANCE = 10000000  // 1천만 포인트

        fun fromMap(map: Map<String, Any?>): Point = Point(
            userId = map["userId"] as? String ?: "",
            balance = (map["balance"] as? Number)?.toInt() ?: INITIAL_BALANCE,
            totalEarned = (map["totalEarned"] as? Number)?.toInt() ?: 0,
            totalSpent = (map["totalSpent"] as? Number)?.toInt() ?: 0,
            lastUpdated = (map["lastUpdated"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}
