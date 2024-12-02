package mp.verif_ai.data.auth

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
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

    override suspend fun signUpWithCredentialManager(
        email: String,
        password: String,
        nickname: String,
        context: ComponentActivity,
        enablePassKey: Boolean
    ): Result<User> = runCatching {
        Log.d("SignUpProcess", "Starting sign up process")

        val credentialManager = CredentialManager.create(context)
        Log.d("SignUpProcess", "CredentialManager instance created")

        try {
            Log.d("SignUpProcess", "Creating password request with email: $email")
            val request = CreatePasswordRequest(
                id = email,
                password = password,
                preferImmediatelyAvailableCredentials = false,
                isAutoSelectAllowed = true
            )
            Log.d("SignUpProcess", "Password request created successfully")

            Log.d("SignUpProcess", "Attempting to save credentials to Credential Manager")
            val result = credentialManager.createCredential(
                context,
                request
            )
            Log.d("SignUpProcess", "Successfully saved credentials. Result type: ${result.type}")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw IllegalStateException("User not created")
            Log.d("SignUpProcess", "Successfully created Firebase Auth account with uid: ${user.uid}")

            Log.d("SignUpProcess", "Attempting to save user info to Firestore")
            usersCollection.document(user.uid)
                .set(
                    hashMapOf(
                        "email" to email,
                        "nickname" to nickname,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Log.d("SignUpProcess", "Successfully saved user info to Firestore")

            // 패스키 등록은 선택적으로 수행
            if (enablePassKey) {
                Log.d("SignUpProcess", "PassKey registration enabled, attempting to register")
                try {
                    passKeyRepository.registerPassKey(user.uid, nickname, context).let { result ->
                        when (result) {
                            is PassKeyRegistrationResult.Success -> {
                                Log.d("SignUpProcess", "Successfully registered PassKey")
                            }
                            is PassKeyRegistrationResult.Error -> {
                                Log.w("SignUpProcess", "Failed to register PassKey", result.exception)
                                // 패스키 등록 실패해도 회원가입은 완료로 처리
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("SignUpProcess", "PassKey registration failed", e)
                    // 패스키 등록 실패해도 계속 진행
                }
            }

            user.toUser()  // 패스키 등록 성공/실패와 관계없이 User 객체 반환

        } catch (e: CreateCredentialException) {
            Log.e("SignUpProcess", "CreateCredentialException details:", e)
            if (e is CreateCredentialNoCreateOptionException) {
                Log.e("SignUpProcess", "No create options available. Check if:")
                Log.e("SignUpProcess", "1. Google Play Services is installed and up to date")
                Log.e("SignUpProcess", "2. Password autofill service is enabled in system settings")
                Log.e("SignUpProcess", "3. Device Android version: ${android.os.Build.VERSION.SDK_INT}")
            }
            throw e
        } catch (e: Exception) {
            Log.e("SignUpProcess", "Unexpected error during sign up process", e)
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun signIn(
        activity: ComponentActivity,
        email: String?,
        password: String?
    ): Result<User> = runCatching {
        // 이메일/패스워드가 제공된 경우, 직접 Firebase 로그인 시도
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            return try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user?.toUser() ?: throw IllegalStateException("User not found")
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        // 기존 Credential Manager 로그인 로직
        val requestOptionsJson = getRequestOptions()
        val credentialManager = CredentialManager.create(activity)

        val passwordOption = GetPasswordOption(
            allowedUserIds = emptySet(),
            allowedProviders = emptySet(),
            isAutoSelectAllowed = true,
        )

        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestOptionsJson,
            clientDataHash = null,
            allowedProviders = emptySet()
        )

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId("488280392024-v9gm0ef7eq47j762rveopjpdd2879s80.apps.googleusercontent.com")
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(false)
            .setNonce(null)
            .build()

        val credentialRequest = GetCredentialRequest(
            listOf(
                passwordOption,
                googleIdOption,
                getPublicKeyCredentialOption
            )
        )

        val result = withContext(Dispatchers.Main) {
            try {
                credentialManager.prepareGetCredential(credentialRequest)
                credentialManager.getCredential(
                    context = activity,
                    request = credentialRequest
                )
            } catch (e: GetCredentialException) {
                when (e) {
                    is GetCredentialCancellationException -> throw PassKeyCancellationException()
                    is GetCredentialInterruptedException -> {
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