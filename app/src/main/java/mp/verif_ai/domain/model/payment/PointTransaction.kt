package mp.verif_ai.domain.model.payment

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PointTransaction(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val amount: Int,
    val type: TransactionType,
    val description: String,
    val relatedId: String? = null,     // 관련된 질문/답변/리뷰 ID
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val paymentInfo: PaymentInfo? = null  // 결제 관련 정보 추가
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "amount" to amount,
        "type" to type.name,
        "description" to description,
        "relatedId" to relatedId,
        "timestamp" to timestamp,
        "status" to status.name,
        "paymentInfo" to paymentInfo?.toMap()
    )

    companion object {
        const val MIN_PURCHASE_AMOUNT = 1000
        const val MAX_PURCHASE_AMOUNT = 1000000
    }
}

@Serializable
data class PaymentInfo(
    val paymentId: String,
    val method: PaymentMethod,
    val currency: String = "KRW",
    val originalAmount: Int,
    val paymentTimestamp: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "paymentId" to paymentId,
        "method" to method.name,
        "currency" to currency,
        "originalAmount" to originalAmount,
        "paymentTimestamp" to paymentTimestamp
    )
}

@Serializable
enum class TransactionType {
    EXPERT_REVIEW_REQUEST,  // 전문가 리뷰 요청 시 차감
    EXPERT_REVIEW_REWARD,   // 전문가가 답변 채택되어 보상받음
    POINT_PURCHASE,         // 포인트 구매
    POINT_REFUND,          // 포인트 환불
    QUESTION_CREATE,        // 질문 작성 시 차감
    ANSWER_ADOPTED,         // 답변 채택됨
    SYSTEM_REWARD,         // 시스템 보상 (이벤트 등)
    SYSTEM_DEDUCTION;      // 시스템 차감 (페널티 등)

    companion object {
        fun fromString(value: String): TransactionType = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            SYSTEM_DEDUCTION
        }
    }
}

@Serializable
enum class TransactionStatus {
    PENDING,    // 처리 대기 중
    COMPLETED,  // 완료됨
    FAILED,     // 실패
    REFUNDED,   // 환불됨
    CANCELLED;  // 취소됨

    companion object {
        fun fromString(value: String): TransactionStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            FAILED
        }
    }
}

@Serializable
enum class PaymentMethod {
    CREDIT_CARD,    // 신용카드
    BANK_TRANSFER,  // 계좌이체
    VIRTUAL_ACCOUNT,// 가상계좌
    MOBILE_PAYMENT, // 모바일 결제
    POINT_REWARD;   // 포인트 보상 (결제 아님)

    companion object {
        fun fromString(value: String): PaymentMethod = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            CREDIT_CARD
        }
    }
}