package mp.verif_ai.domain.model

data class Point(
    val id: String,
    val userId: String,
    val type: PointType,
    val amount: Int,
    val description: String,
    val relatedId: String?, // questionId, answerId, paymentId etc.
    val createdAt: Long
)

enum class PointType { EARN, SPEND, TRANSFER }