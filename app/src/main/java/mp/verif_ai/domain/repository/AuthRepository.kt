package mp.verif_ai.domain.repository

import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.auth.User

interface AuthRepository {
    /**
     * 이메일 주소로 기존 계정 존재 여부를 확인합니다.
     *
     * @param email 확인할 이메일 주소
     * @return Result<Boolean> - true: 기존 계정 존재, false: 신규 사용자
     */
    suspend fun checkExistingAccount(email: String): Result<Boolean>

    /**
     * Firebase Auth Credential을 사용하여 인증을 수행합니다.
     *
     * @param credential Firebase Auth Credential
     * @return 인증된 사용자 정보
     */
    suspend fun signInWithCredential(credential: AuthCredential): Result<User>

    /**
     * 이메일/비밀번호로 로그인합니다.
     * 내부적으로 signInWithCredential을 호출합니다.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Google 계정으로 로그인합니다.
     * 내부적으로 signInWithCredential을 호출합니다.
     */
    suspend fun signInWithGoogle(): Result<User>

    /**
     * 이메일/비밀번호로 새 계정을 등록합니다.
     *
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return 생성된 사용자 정보
     */
    suspend fun signUpWithEmail(email: String, password: String, nickname: String): Result<User>

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

    /**
     * 이메일 인증 코드를 발송합니다.
     */
    suspend fun sendVerificationEmail(email: String): Result<Unit>

    /**
     * 이메일 인증 코드를 확인합니다.
     */
    suspend fun verifyEmailCode(code: String): Result<Unit>

    /**
     * 이메일 인증 상태를 확인합니다.
     */
    suspend fun isEmailVerified(): Boolean
}