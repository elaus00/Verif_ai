package mp.verif_ai.domain.repository

import mp.verif_ai.domain.model.auth.ExpertProfile
import mp.verif_ai.domain.model.auth.User
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.auth.ExpertStatus

interface UserRepository {
    /**
     * 현재 로그인된 사용자의 정보를 Flow로 제공합니다.
     * 로그아웃 시 null을 emit하며, 사용자 정보가 업데이트될 때마다 새로운 값을 emit합니다.
     *
     * @return 현재 로그인된 사용자 정보를 담은 Flow
     */
    suspend fun observeCurrentUser(): Flow<User?>

    /**
     * 현재 로그인된 사용자 정보를 일회성으로 조회합니다.
     * 로그인된 사용자가 없는 경우 null을 반환합니다.
     *
     * @return 현재 로그인된 사용자 정보
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * 특정 사용자의 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 요청된 사용자의 정보
     * @throws NoSuchElementException 사용자를 찾을 수 없는 경우
     */
    suspend fun getUser(userId: String): Result<User>

    /**
     * 사용자 프로필을 업데이트합니다.
     * 업데이트된 시간은 자동으로 현재 시간으로 설정됩니다.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param updates 업데이트할 필드와 값들
     * @return 업데이트된 사용자 정보
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<User>

    /**
     * 사용자의 포인트를 실시간으로 모니터링합니다.
     * 포인트가 변경될 때마다 새로운 값을 emit합니다.
     *
     * @return 사용자의 현재 포인트를 담은 Flow
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun observeUserPoints(): Flow<Int>

    /**
     * 사용자의 현재 포인트를 일회성으로 조회합니다.
     *
     * @return 현재 포인트
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun getUserPoints(): Result<Int>

    /**
     * 사용자의 포인트를 업데이트합니다.
     *
     * @param amount 변경할 포인트 양 (양수: 증가, 음수: 감소)
     * @return 업데이트된 후의 총 포인트
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     * @throws IllegalArgumentException 포인트가 0 미만이 되는 경우
     */
    suspend fun updatePoints(amount: Int): Result<Int>

    /**
     * 전문가 프로필을 실시간으로 모니터링합니다.
     * 프로필이 업데이트될 때마다 새로운 값을 emit합니다.
     *
     * @param userId 모니터링할 전문가의 ID
     * @return 전문가 프로필 정보를 담은 Flow
     */
    suspend fun observeExpertProfile(userId: String): Flow<ExpertProfile?>

    /**
     * 전문가 프로필을 일회성으로 조회합니다.
     *
     * @param userId 조회할 전문가의 ID
     * @return 전문가 프로필 정보
     * @throws NoSuchElementException 전문가 프로필을 찾을 수 없는 경우
     */
    suspend fun getExpertProfile(userId: String): Result<ExpertProfile>

    /**
     * 새로운 전문가 프로필을 생성합니다.
     *
     * @param profile 생성할 전문가 프로필 정보
     * @return 생성된 전문가 프로필
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     * @throws IllegalArgumentException 이미 전문가 프로필이 존재하는 경우
     */
    suspend fun createExpertProfile(profile: ExpertProfile): Result<ExpertProfile>

    /**
     * 전문가 프로필을 업데이트합니다.
     *
     * @param updates 업데이트할 필드와 값들
     * @return 업데이트된 전문가 프로필
     * @throws IllegalStateException 로그인된 사용자가 없거나 전문가가 아닌 경우
     */
    suspend fun updateExpertProfile(updates: Map<String, Any>): Result<ExpertProfile>

    /**
     * 전문가 상태를 실시간으로 모니터링합니다.
     * 상태가 변경될 때마다 새로운 값을 emit합니다.
     *
     * @return 전문가 상태를 담은 Flow
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun observeExpertStatus(): Flow<ExpertStatus>

    /**
     * 현재 전문가 상태를 일회성으로 조회합니다.
     *
     * @return 현재 전문가 상태
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    suspend fun getExpertStatus(): Result<ExpertStatus>
}