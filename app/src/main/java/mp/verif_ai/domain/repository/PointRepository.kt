package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.payment.PointTransaction
import mp.verif_ai.domain.model.payment.TransactionType

interface PointRepository {
    /**
     * 포인트 거래 내역을 기록합니다.
     *
     * @param userId 사용자 ID
     * @param amount 거래 포인트 (양수: 적립, 음수: 차감)
     * @param type 거래 유형
     * @param relatedId 관련 리소스 ID (optional)
     * @return 기록 성공 여부
     */
    suspend fun recordTransaction(
        userId: String,
        amount: Int,
        type: TransactionType,
        relatedId: String? = null
    ): Result<Unit>

    /**
     * 사용자의 포인트 거래 이력을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 거래 이력 리스트를 포함하는 Flow
     */
    suspend fun getTransactionHistory(userId: String): Flow<List<PointTransaction>>

    /**
     * 사용자의 현재 포인트를 실시간으로 관찰합니다.
     *
     * @param userId 관찰할 사용자의 ID
     * @return 포인트 잔액을 포함하는 Flow
     */
    suspend fun observeUserPoints(userId: String): Flow<Int>

    /**
     * 사용자의 현재 포인트를 일회성으로 조회합니다.
     *
     * @return 현재 포인트
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun getUserPoints(): Result<Int>
}