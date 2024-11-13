package mp.verif_ai.domain.repository

import mp.verif_ai.domain.model.ExpertProfile
import mp.verif_ai.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 정보 관리를 담당하는 Repository 인터페이스입니다.
 * 사용자 프로필 관리, 포인트 관리, 전문가 프로필 관리 등을 처리합니다.
 */
interface UserRepository {
    /**
     * 현재 로그인된 사용자의 정보를 Flow로 제공합니다.
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * 특정 사용자의 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     */
    suspend fun getUser(userId: String): Result<User>

    /**
     * 사용자 프로필을 업데이트합니다.
     *
     * @param user 업데이트할 사용자 정보
     */
    suspend fun updateUser(user: User): Result<User>

    /**
     * 사용자의 포인트를 실시간으로 모니터링합니다.
     *
     * @param userId 포인트를 조회할 사용자의 ID
     */
    fun getUserPoints(userId: String): Flow<Int>

    /**
     * 전문가 프로필을 생성합니다.
     *
     * @param userId 전문가 프로필을 생성할 사용자의 ID
     * @param profile 생성할 전문가 프로필 정보
     */
    suspend fun createExpertProfile(userId: String, profile: ExpertProfile): Result<ExpertProfile>
}