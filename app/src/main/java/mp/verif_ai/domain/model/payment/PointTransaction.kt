package mp.verif_ai.domain.model.payment

data class PointTransaction(
    val id: String,
    val userId: String,
    val amount: Int,
    val type: TransactionType,
    val relatedId: String?, // 관련 메시지나 리뷰 ID
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.COMPLETED
)

enum class TransactionType {
    EXPERT_REVIEW_REQUEST,
    EXPERT_REVIEW_REWARD,
    POINT_PURCHASE,
    POINT_REFUND
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}