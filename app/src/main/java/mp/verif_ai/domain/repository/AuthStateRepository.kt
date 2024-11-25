package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.auth.AuthState

interface AuthStateRepository {
    /**
     * 현재 인증 상태를 관찰합니다.
     * 로그인/로그아웃 등 인증 상태 변경시 새로운 값을 emit합니다.
     */
    suspend fun observeAuthState(): Flow<AuthState>

    /**
     * 현재 인증 세션의 유효성을 확인합니다.
     */
    suspend fun isSessionValid(): Result<Boolean>

    /**
     * 자동 로그인 상태를 설정/해제합니다.
     */
    suspend fun setAutoSignIn(enabled: Boolean): Result<Unit>

    /**
     * 현재 자동 로그인 상태를 확인합니다.
     */
    suspend fun isAutoSignInEnabled(): Result<Boolean>
}