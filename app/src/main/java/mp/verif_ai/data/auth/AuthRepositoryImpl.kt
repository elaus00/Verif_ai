package mp.verif_ai.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.auth.SignInMethod
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun checkExistingAccount(email: String): Result<Boolean> = runCatching {
        try {
            // 이메일/패스워드로 임시 로그인 시도
            // 존재하지 않는 이메일이면 FirebaseAuthInvalidUserException 발생
            auth.signInWithEmailAndPassword(email, "temp_password_123").await()
            true
        } catch (e: Exception) {
            when {
                // 이메일이 존재하지만 비밀번호가 틀린 경우 (= 계정 존재)
                e.message?.contains("INVALID_PASSWORD") == true -> true
                // 계정이 존재하지 않는 경우
                e.message?.contains("USER_NOT_FOUND") == true -> false
                // 이메일 형식이 잘못된 경우
                e.message?.contains("INVALID_EMAIL") == true ->
                    throw IllegalArgumentException("Invalid email format")
                // 기타 예상치 못한 에러
                else -> throw e
            }
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Result<User> = runCatching {
        val firebaseCredential = when (credential) {
            is AuthCredential.Email -> {
                auth.signInWithEmailAndPassword(credential.email, credential.password).await()
                    .user?.toUser() ?: throw IllegalStateException("User not found")
            }
            is AuthCredential.Google -> {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                auth.signInWithCredential(firebaseCredential).await()
                    .user?.toUser() ?: throw IllegalStateException("User not found")
            }
        }
        firebaseCredential
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return signInWithCredential(AuthCredential.Email(email, password))
    }

    override suspend fun signInWithGoogle(): Result<User> {
        // Google Sign In은 Activity에서 처리해야 하므로, 여기서는 구현하지 않습니다.
        throw UnsupportedOperationException("Google Sign In must be handled in Activity")
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        nickname: String
    ): Result<User> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw IllegalStateException("User not found")

        // Create user document in Firestore
        val userModel = User(
            id = user.uid,
            email = email,
            nickname = nickname,
            emailVerified = user.isEmailVerified,
            signInMethod = SignInMethod.EMAIL.name
        )

        firestore.collection("users").document(user.uid)
            .set(userModel.toMap())
            .await()

        userModel
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun withdraw(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No user signed in")

        // Delete Firestore user document
        firestore.collection("users").document(user.uid)
            .delete()
            .await()

        // Delete Firebase Auth user
        user.delete().await()
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun sendVerificationEmail(email: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No user signed in")
        user.sendEmailVerification().await()
    }

    override suspend fun verifyEmailCode(code: String): Result<Unit> = runCatching {
        // 이메일 verification은 Firebase에서 자동으로 처리되므로,
        // 여기서는 현재 유저의 이메일 인증 상태만 확인합니다.
        val user = auth.currentUser ?: throw IllegalStateException("No user signed in")
        user.reload().await()
        if (!user.isEmailVerified) {
            throw IllegalStateException("Email not verified")
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }

    private fun com.google.firebase.auth.FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            phoneNumber = phoneNumber,
            nickname = displayName ?: "",
            emailVerified = isEmailVerified,
            signInMethod = when (providerData.firstOrNull()?.providerId) {
                "password" -> SignInMethod.EMAIL.name
                "google.com" -> SignInMethod.GOOGLE.name
                else -> SignInMethod.EMAIL.name
            }
        )
    }
}