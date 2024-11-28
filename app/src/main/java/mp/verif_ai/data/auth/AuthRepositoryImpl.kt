package mp.verif_ai.data.auth

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.model.passkey.PassKeyRegistrationResult
import mp.verif_ai.domain.model.passkey.PassKeySignInResult
import mp.verif_ai.domain.model.passkey.PassKeyStatus
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.PassKeyRepository
import mp.verif_ai.domain.util.passkey.PassKeyCancellationException
import mp.verif_ai.domain.util.passkey.PassKeyConfig
import mp.verif_ai.domain.util.passkey.PassKeyConfig.getRequestOptions
import mp.verif_ai.domain.util.passkey.PassKeyNoCredentialException
import mp.verif_ai.domain.util.passkey.PassKeyNotSupportedException
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val passKeyRepository: PassKeyRepository,
) : AuthRepository {

    private val passKeysCollection = firestore.collection("passkeys")
    private val usersCollection = firestore.collection("users")

    fun generateChallenge(): ByteArray {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
    }

    override suspend fun signIn(activity: ComponentActivity): Result<User> = runCatching {
        val challenge = generateChallenge()
        val requestOptionsJson = getRequestOptions(challenge.toString())
        val credentialManager = CredentialManager.create(activity)

        // 패스 키 상태 확인 (존재 여부)
        val passKeyStatus = passKeyRepository.checkPassKeyStatus()

        // Password Sign-In 옵션 설정
        val passwordOption = GetPasswordOption(
            allowedUserIds = emptySet(), // 모든 사용자 자격 증명을 요청
            allowedProviders = emptySet(), // 기본 제공자 허용
            isAutoSelectAllowed = true,  // 수동 선택 UI 표시
        )

        // PassKey 옵션 설정
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestOptionsJson,
            clientDataHash = null,  // 기본값 사용
            allowedProviders = emptySet()  // 모든 제공자 허용
        )

        // Google Sign-In 옵션 설정
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("488280392024-v9gm0ef7eq47j762rveopjpdd2879s80.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setNonce(null)
            .build()

        val credentialRequest = when (passKeyStatus) {
            PassKeyStatus.NO_CREDENTIAL, PassKeyStatus.NOT_SUPPORTED -> {
                // PassKey가 없거나 지원되지 않는 경우 PassKey 옵션 제외
                GetCredentialRequest(
                    listOf(
                        passwordOption,
                        googleIdOption
                    )
                )
            }
            else -> {
                // PassKey 사용 가능한 경우 모든 옵션 포함
                GetCredentialRequest(
                    listOf(
                        googleIdOption,
                        passwordOption,
                        getPublicKeyCredentialOption
                    )
                )
            }
        }

        val result = withContext(Dispatchers.Main) {
            try {
                credentialManager.getCredential(
                    context = activity,
                    request = credentialRequest
                )
            } catch (e: GetCredentialException) {
                when (e) {
                    is GetCredentialCancellationException -> throw PassKeyCancellationException()
                    is GetCredentialInterruptedException -> {
                        // NO_CREDENTIAL 상태에서 다른 로그인 방식으로 전환
                        handleAlternativeSignIn(activity, googleIdOption, passwordOption)
                    }
                    else -> throw e
                }
            }
        }

        when (val signInResult = passKeyRepository.signInWithPassKey(result).getOrThrow()) {
            is PassKeySignInResult.Success -> auth.currentUser?.toUser()
                ?: throw IllegalStateException("User not found")
            is PassKeySignInResult.Error -> throw signInResult.exception
        }
    }

    private suspend fun handleAlternativeSignIn(
        activity: ComponentActivity,
        googleIdOption: GetGoogleIdOption,
        passwordOption: GetPasswordOption
    ): GetCredentialResponse {
        // 대체 인증 방식으로 재시도
        val credentialManager = CredentialManager.create(activity)
        val alternativeRequest = GetCredentialRequest(
            listOf(
                googleIdOption,
                passwordOption
            )
        )
        return credentialManager.getCredential(activity, alternativeRequest)
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        nickname: String
    ): Result<User> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw IllegalStateException("User not created")

        usersCollection.document(user.uid)
            .set(
                hashMapOf(
                    "email" to email,
                    "nickname" to nickname,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            .await()

        // PassKey 등록
        passKeyRepository.registerPassKey(user.uid, nickname).let { result ->
            when (result) {
                is PassKeyRegistrationResult.Success -> user.toUser()
                is PassKeyRegistrationResult.Error -> throw result.exception
            }
        }
    }

    override suspend fun checkExistingAccount(email: String): Result<Boolean> = runCatching {
        val result = auth.fetchSignInMethodsForEmail(email).await()
        result.signInMethods?.isNotEmpty() ?: false
    }


    override suspend fun signOut(): Result<Unit> = runCatching {
        // PassKey 관련 데이터 정리
        val currentUser = auth.currentUser
        if (currentUser != null) {
            passKeysCollection
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .await()
                .forEach { doc ->
                    doc.reference.delete().await()
                }
        }

        auth.signOut()
    }

    override suspend fun withdraw(): Result<Unit> = runCatching {
        val currentUser = auth.currentUser ?: throw IllegalStateException("No user signed in")

        // 1. PassKey 데이터 삭제
        passKeysCollection
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .await()
            .forEach { doc ->
                doc.reference.delete().await()
            }

        // 2. 사용자 데이터 삭제
        usersCollection.document(currentUser.uid).delete().await()

        // 3. Firebase Auth 계정 삭제
        currentUser.delete().await()
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun sendVerificationEmail(email: String): Result<Unit> = runCatching {
        val currentUser = auth.currentUser ?: throw IllegalStateException("No user signed in")
        currentUser.sendEmailVerification().await()
    }

    override suspend fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    override suspend fun observeAuthState(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUser())
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email.toString(),
            emailVerified = isEmailVerified,
            displayName = displayName
        )
    }


}