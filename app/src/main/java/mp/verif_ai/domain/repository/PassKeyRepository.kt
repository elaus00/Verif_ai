package mp.verif_ai.domain.repository

import androidx.activity.ComponentActivity
import androidx.credentials.GetCredentialResponse
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.passkey.PassKeyInfo
import mp.verif_ai.domain.model.passkey.PassKeyRegistrationResult
import mp.verif_ai.domain.model.passkey.PassKeySignInResult
import mp.verif_ai.domain.model.passkey.PassKeyStatus

/**
 * PassKey 인증 및 관리를 위한 Repository 인터페이스
 * PassKey 등록, 인증, 관리 및 Firebase와의 연동을 담당합니다.
 */
interface PassKeyRepository {
    /**
     * PassKey 상태 및 등록 관련 작업
     */

    /**
     * 사용자를 위한 새로운 PassKey를 등록합니다.
     * @param userId 등록할 사용자의 고유 ID
     * @param name PassKey의 식별 이름 (선택사항)
     * @return 등록 결과를 포함한 Result
     */
    suspend fun registerPassKey(userId: String, name: String? = null, context: ComponentActivity): PassKeyRegistrationResult

    /**
     * PassKey를 사용하여 로그인을 시도합니다.
     * @return 로그인 결과를 포함한 Result
     */
    suspend fun signInWithPassKey(result: GetCredentialResponse): Result<PassKeySignInResult>

    /**
     * PassKey 관리 관련 작업
     */

    /**
     * 사용자의 등록된 모든 PassKey 정보를 조회합니다.
     * @param userId 조회할 사용자의 고유 ID
     * @return PassKey 정보 목록을 포함한 Result
     */
    suspend fun getRegisteredPassKeys(userId: String): Result<List<PassKeyInfo>>

    /**
     * 특정 PassKey를 삭제합니다.
     * @param credentialId 삭제할 PassKey의 고유 ID
     * @return 삭제 작업 결과를 포함한 Result
     */
    suspend fun removePassKey(credentialId: String): Result<Unit>

    /**
     * 사용자의 모든 PassKey를 실시간으로 관찰합니다.
     * @param userId 관찰할 사용자의 고유 ID
     * @return PassKey 목록의 변경사항을 emit하는 Flow
     */
    fun observePassKeys(userId: String): Flow<List<PassKeyInfo>>

    /**
     * Firebase 연동 관련 작업
     */

    /**
     * PassKey 정보를 Firebase에 저장합니다.
     * @param passKeyInfo 저장할 PassKey 정보
     * @return 저장 작업 결과를 포함한 Result
     */
    suspend fun savePassKeyToFirebase(passKeyInfo: PassKeyInfo): Result<Unit>

    /**
     * PassKey의 마지막 사용 시간을 업데이트합니다.
     * @param credentialId 업데이트할 PassKey의 고유 ID
     * @return 업데이트 작업 결과를 포함한 Result
     */
    suspend fun updatePassKeyLastUsed(credentialId: String): Result<Unit>

    /**
     * PassKey의 유효성을 검증합니다.
     * @param credentialId 검증할 PassKey의 고유 ID
     * @return 유효성 검증 결과를 포함한 Result
     */
    suspend fun verifyPassKey(credentialId: String): Result<Boolean>
}