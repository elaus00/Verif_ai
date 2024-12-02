package mp.verif_ai.domain.repository

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.auth.User

interface AuthRepository {
    /**
     * CredentialManager를 사용하여 회원가입을 진행합니다.
     */
    suspend fun signUpWithCredentialManager(
        email: String,
        password: String,
        nickname: String,
        context: ComponentActivity,
        enablePassKey: Boolean = false
    ): Result<User>

    /**
     * PassKey를 사용하여 로그인을 진행합니다.
     */
    suspend fun signIn(activity: ComponentActivity, email: String? = null, password: String? = null): Result<User>

    /**
     * 로그아웃을 진행합니다.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * 회원 탈퇴를 진행합니다.
     */
    suspend fun withdraw(): Result<Unit>

    /**
     * 비밀번호 재설정 이메일을 전송합니다.
     */
    suspend fun resetPassword(email: String): Result<Unit>

    /**
     * 이메일 인증 메일을 전송합니다.
     */
    suspend fun sendVerificationEmail(email: String): Result<Unit>

    /**
     * 이메일 인증 상태를 확인합니다.
     */
    suspend fun isEmailVerified(): Boolean

    /**
     * 인증 상태를 관찰합니다.
     */
    suspend fun observeAuthState(): Flow<User?>

    /**
     * 현재 로그인된 사용자 정보를 반환합니다.
     */
    fun getCurrentUser(): User?
}