package mp.verif_ai.domain.repository

import mp.verif_ai.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 인증 관련 작업을 처리하는 Repository 인터페이스입니다.
 * 회원가입, 로그인, 로그아웃, 회원탈퇴 등 인증 관련 기능을 정의합니다.
 */
interface AuthRepository {
    /**
     * 새로운 사용자 계정을 생성합니다.
     *
     * @param email 사용자 이메일
     * @param password 비밀번호
     * @param nickname 사용자 닉네임
     * @return 생성된 사용자 정보
     */
    suspend fun signUp(email: String, password: String, nickname: String): Result<User>

    /**
     * 기존 사용자 계정으로 로그인합니다.
     *
     * @param email 사용자 이메일
     * @param password 비밀번호
     * @return 로그인된 사용자 정보
     */
    suspend fun signIn(email: String, password: String): Result<User>

    /**
     * 현재 로그인된 사용자를 로그아웃합니다.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * 회원 탈퇴를 진행합니다.
     */
    suspend fun withdraw(): Result<Unit>

    /**
     * 비밀번호 재설정 이메일을 전송합니다.
     *
     * @param email 비밀번호를 재설정할 이메일 주소
     */
    suspend fun resetPassword(email: String): Result<Unit>
}