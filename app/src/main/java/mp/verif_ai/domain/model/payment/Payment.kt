package mp.verif_ai.domain.model.payment

data class Payment(
    val id: String,
    val userId: String,
    val type: PaymentType,
    val amount: Double,
    val status: PaymentStatus,
    val metadata: Map<String, Any>?,
    val createdAt: Long,
    val updatedAt: Long
)

enum class PaymentType {
    POINT_PURCHASE,
    POINT_REFUND,
    REWARD_PAYMENT
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}