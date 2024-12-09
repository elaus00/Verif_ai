package mp.verif_ai.domain.model.payment

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Payment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val amount: Int,                // 결제 금액
    val pointAmount: Int,           // 지급될 포인트 양
    val method: PaymentMethod,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val merchantUid: String? = null, // PG사 주문번호
    val receiptUrl: String? = null,  // 영수증 URL
    val metadata: Map<String, String> = emptyMap(),  // 추가 데이터
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "amount" to amount,
        "pointAmount" to pointAmount,
        "method" to method.name,
        "status" to status.name,
        "merchantUid" to merchantUid,
        "receiptUrl" to receiptUrl,
        "metadata" to metadata,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "completedAt" to completedAt
    )

    companion object {
        const val MIN_PAYMENT_AMOUNT = 1000     // 최소 결제 금액
        const val MAX_PAYMENT_AMOUNT = 1000000  // 최대 결제 금액
        const val POINT_CONVERSION_RATE = 1     // 1원 = 1포인트

        fun fromMap(map: Map<String, Any?>): Payment = Payment(
            id = map["id"] as? String ?: UUID.randomUUID().toString(),
            userId = map["userId"] as? String ?: "",
            amount = (map["amount"] as? Number)?.toInt() ?: 0,
            pointAmount = (map["pointAmount"] as? Number)?.toInt() ?: 0,
            method = PaymentMethod.fromString(map["method"] as? String ?: ""),
            status = PaymentStatus.fromString(map["status"] as? String ?: ""),
            merchantUid = map["merchantUid"] as? String,
            receiptUrl = map["receiptUrl"] as? String,
            metadata = (map["metadata"] as? Map<*, *>)?.mapNotNull {
                if (it.key is String && it.value is String) {
                    it.key.toString() to it.value.toString()
                } else null
            }?.toMap() ?: emptyMap(),
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            completedAt = (map["completedAt"] as? Number)?.toLong()
        )
    }
}

@Serializable
enum class PaymentStatus {
    PENDING,        // 결제 대기
    PROCESSING,     // 처리 중
    COMPLETED,      // 완료됨
    FAILED,         // 실패
    CANCELLED,      // 취소됨
    REFUNDED,       // 환불됨
    PARTIALLY_REFUNDED;  // 부분 환불됨

    companion object {
        fun fromString(value: String): PaymentStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            FAILED
        }
    }
}