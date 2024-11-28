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

/**
 * 사용자 인증 및 계정 관리를 위한 Repository 인터페이스
 * 이메일/비밀번호 인증, 소셜 로그인, 계정 관리 기능을 담당합니다.
 */
interface AuthRepository {
    /**
     * CredentialManager를 통해 통합된 PassKey 인증을 수행합니다.
     * 사용자는 시스템 제공 UI를 통해 등록된 PassKey로 인증할 수 있습니다.
     *
     * @param context 인증에 필요한 Context
     * @return 인증된 사용자 정보를 포함한 Result
     * @throws PassKeyNoCredentialException PassKey가 등록되지 않은 경우
     * @throws PassKeyCancellationException 사용자가 인증을 취소한 경우
     * @throws PassKeyNotSupportedException 기기가 PassKey를 지원하지 않는 경우
     */
    suspend fun signIn(activity: ComponentActivity): Result<User>

    /**
     * 신규 이메일/비밀번호 계정을 생성하고 사용자 정보를 등록합니다.
     *
     * @param email 사용할 이메일 주소
     * @param password 설정할 비밀번호
     * @param nickname 사용자 닉네임
     * @return 생성된 사용자 정보를 포함한 Result
     */
    suspend fun signUpWithEmail(email: String, password: String, nickname: String): Result<User>

    /**
     * 이메일 주소로 기존 계정 존재 여부를 확인합니다.
     *
     * @param email 확인할 이메일 주소
     * @return 계정 존재 여부를 포함한 Result
     */
    suspend fun checkExistingAccount(email: String): Result<Boolean>

    /**
     * 현재 로그인된 사용자의 인증 정보를 삭제하고 로그아웃합니다.
     * PassKey 관련 데이터도 함께 정리됩니다.
     *
     * @return 로그아웃 작업 결과를 포함한 Result
     */
    suspend fun signOut(): Result<Unit>

    /**
     * 현재 사용자의 계정을 완전히 삭제합니다.
     * 계정 정보, PassKey 데이터, 사용자 관련 모든 데이터가 삭제됩니다.
     *
     * @return 계정 삭제 작업 결과를 포함한 Result
     */
    suspend fun withdraw(): Result<Unit>

    /**
     * 비밀번호 재설정 이메일을 발송합니다.
     *
     * @param email 비밀번호를 재설정할 이메일 주소
     * @return 이메일 전송 결과를 포함한 Result
     */
    suspend fun resetPassword(email: String): Result<Unit>

    /**
     * 이메일 인증 메일을 발송합니다.
     *
     * @param email 인증할 이메일 주소
     * @return 인증 메일 전송 결과를 포함한 Result
     */
    suspend fun sendVerificationEmail(email: String): Result<Unit>

    /**
     * 현재 사용자의 이메일 인증 상태를 확인합니다.
     *
     * @return 이메일 인증 완료 여부
     */
    suspend fun isEmailVerified(): Boolean

    /**
     * 사용자의 인증 상태 변경을 실시간으로 관찰합니다.
     * 로그인, 로그아웃, 인증 정보 변경 등의 이벤트를 감지할 수 있습니다.
     *
     * @return 사용자 정보의 변경사항을 emit하는 Flow
     */
    suspend fun observeAuthState(): Flow<User?>

    /**
     * 현재 로그인된 사용자 정보를 반환합니다.
     * 로그인되지 않은 경우 null을 반환합니다.
     *
     * @return 현재 사용자 정보 또는 null
     */
    fun getCurrentUser(): User?
}