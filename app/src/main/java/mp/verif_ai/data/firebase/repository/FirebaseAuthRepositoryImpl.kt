package mp.verif_ai.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.model.auth.UserStatus
import mp.verif_ai.domain.model.auth.UserType
import mp.verif_ai.domain.model.extension.VerificationConstants.MAX_VERIFICATION_ATTEMPTS
import mp.verif_ai.domain.model.extension.VerificationConstants.VERIFICATION_CODE_EXPIRY
import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Firebase Authentication을 사용하여 사용자 인증을 처리하는 Repository 구현체입니다.
 * 이 클래스는 회원가입, 로그인, 로그아웃, 회원탈퇴, 비밀번호 재설정 등
 * 사용자 인증과 관련된 모든 작업을 처리합니다.
 *
 * @property auth Firebase Authentication 인스턴스로, 사용자 인증 작업을 수행합니다.
 * @property firestore Firestore 데이터베이스 인스턴스로, 사용자 기본 정보를 저장합니다.
 */
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    /**
     * User 컬렉션에 대한 참조입니다.
     * 사용자의 기본 정보를 저장하고 관리하는데 사용됩니다.
     */
    private val usersCollection = firestore.collection("User")
    private val verificationCollection = firestore.collection("EmailVerification")

    init {
        // reCAPTCHA 설정 - 테스트 환경에서만 사용
        // 프로덕션 환경에서는 제거하거나 조건부로 처리해야 합니다.
        auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    }

    /**
     * 새로운 사용자 계정을 생성하고 기본 프로필 정보를 저장합니다.
     *
     * 처리 과정:
     * 1. Firebase Authentication을 통해 이메일/비밀번호로 계정을 생성합니다.
     * 2. 생성된 계정의 UID를 사용하여 Firestore에 사용자 기본 정보를 저장합니다.
     * 3. 저장된 사용자 정보를 반환합니다.
     *
     * @param email 사용자 이메일 주소
     * @param password 사용자 비밀번호
     * @param nickname 사용자 닉네임
     * @return 생성된 사용자 정보를 담은 Result
     * @throws IllegalStateException 사용자 생성에 실패한 경우
     */
    override suspend fun signUp(
        email: String,
        password: String,
        nickname: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // 1. Firebase Auth로 계정 생성
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("User creation failed")

            // 2. 사용자 기본 정보 객체 생성
            val user = User(
                id = firebaseUser.uid,
                email = email,
                phoneNumber = null,
                nickname = nickname,
                type = UserType.NORMAL,
                status = UserStatus.ACTIVE,
                points = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // 3. Firestore에 사용자 정보 저장
            usersCollection.document(user.id)
                .set(user.toMap())
                .await()

            Result.success(user)
        } catch (e: Exception) {
            // 이미 사용 중인 이메일인 경우에 대한 처리
            if (e.message?.contains("email address is already in use") == true) {
                Result.failure(IllegalStateException("이미 사용 중인 이메일입니다."))
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 기존 사용자 계정으로 로그인을 수행합니다.
     *
     * 처리 과정:
     * 1. Firebase Authentication을 통해 이메일/비밀번호로 로그인을 시도합니다.
     * 2. 로그인 성공 시, Firestore에서 해당 사용자의 정보를 조회합니다.
     * 3. 조회된 사용자 정보를 반환합니다.
     *
     * @param email 사용자 이메일 주소
     * @param password 사용자 비밀번호
     * @return 로그인된 사용자 정보를 담은 Result
     * @throws IllegalStateException 로그인 실패 또는 사용자 정보 조회 실패 시
     */
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // 1. Firebase Auth로 로그인
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw IllegalStateException("Login failed")

            // 2. Firestore에서 사용자 정보 조회
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: throw IllegalStateException("User data not found")

            Result.success(user)
        } catch (e: Exception) {
            // reCAPTCHA 및 인증 관련 에러 처리
            when {
                e.message?.contains("RECAPTCHA") == true -> {
                    Result.failure(IllegalStateException("보안 인증에 실패했습니다. 잠시 후 다시 시도해주세요."))
                }
                e.message?.contains("password is invalid") == true -> {
                    Result.failure(IllegalStateException("이메일 또는 비밀번호가 올바르지 않습니다."))
                }
                else -> Result.failure(e)
            }
        }
    }

    /**
     * 현재 로그인된 사용자를 로그아웃 처리합니다.
     * Firebase Authentication의 signOut() 메소드를 호출하여 수행됩니다.
     *
     * @return 로그아웃 성공 여부를 담은 Result
     */
    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 현재 로그인된 사용자의 계정을 삭제합니다.
     *
     * 처리 과정:
     * 1. 현재 로그인된 사용자 확인
     * 2. Firestore에서 사용자 관련 데이터 삭제
     * 3. Firebase Authentication에서 사용자 계정 삭제
     *
     * @return 회원탈퇴 성공 여부를 담은 Result
     * @throws IllegalStateException 로그인된 사용자가 없는 경우
     */
    override suspend fun withdraw(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: throw IllegalStateException("로그인된 사용자가 없습니다.")

            // 1. Firestore에서 사용자 데이터 삭제
            usersCollection.document(user.uid).delete().await()

            // 2. Firebase Auth에서 사용자 삭제
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 비밀번호 재설정 이메일을 전송합니다.
     * Firebase Authentication의 비밀번호 재설정 기능을 사용합니다.
     *
     * @param email 비밀번호를 재설정할 이메일 주소
     * @return 이메일 전송 성공 여부를 담은 Result
     */
    override suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 이메일 인증 코드를 생성하고 전송합니다.
     *
     * 처리 과정:
     * 1. 6자리 랜덤 코드 생성
     * 2. Firestore에 인증 코드와 만료 시간 저장
     * 3. 이메일 발송 (Firebase Cloud Functions 사용)
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @return 인증 코드 전송 성공 여부를 담은 Result
     */
    override suspend fun sendVerificationEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val code = generateVerificationCode()
            val expirationTime = System.currentTimeMillis() + VERIFICATION_CODE_EXPIRY

            // Firestore에 인증 정보 저장
            verificationCollection.document(email).set(
                hashMapOf(
                    "code" to code,
                    "email" to email,
                    "expirationTime" to expirationTime,
                    "verified" to false,
                    "attempts" to 0
                )
            ).await()

            // Cloud Functions를 통한 이메일 발송 (실제 구현 필요)
            // cloudFunctions.getHttpsCallable("sendVerificationEmail")
            //     .call(hashMapOf("email" to email, "code" to code))
            //     .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 이메일 인증 코드를 확인합니다.
     *
     * 처리 과정:
     * 1. Firestore에서 해당 이메일의 인증 정보 조회
     * 2. 코드 일치 여부, 만료 시간, 시도 횟수 확인
     * 3. 인증 성공 시 사용자 상태 업데이트
     *
     * @param code 사용자가 입력한 인증 코드
     * @return 인증 성공 여부를 담은 Result
     */
    override suspend fun verifyEmailCode(code: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
                ?: throw IllegalStateException("No user logged in")

            val verificationDoc = verificationCollection.document(currentUser.email!!)
                .get()
                .await()

            if (!verificationDoc.exists()) {
                return@withContext Result.failure(IllegalStateException("인증 코드를 찾을 수 없습니다."))
            }

            val storedCode = verificationDoc.getString("code")!!
            val expirationTime = verificationDoc.getLong("expirationTime")!!
            val attempts = verificationDoc.getLong("attempts")!! + 1

            when {
                attempts > MAX_VERIFICATION_ATTEMPTS -> {
                    Result.failure(IllegalStateException("인증 시도 횟수를 초과했습니다. 새로운 코드를 요청해주세요."))
                }
                System.currentTimeMillis() > expirationTime -> {
                    Result.failure(IllegalStateException("인증 코드가 만료되었습니다. 새로운 코드를 요청해주세요."))
                }
                code != storedCode -> {
                    // 시도 횟수 증가
                    verificationDoc.reference.update("attempts", attempts)
                    Result.failure(IllegalStateException("잘못된 인증 코드입니다."))
                }
                else -> {
                    // 인증 성공 처리
                    verificationDoc.reference.update(
                        mapOf(
                            "verified" to true,
                            "verifiedAt" to System.currentTimeMillis()
                        )
                    ).await()

                    // 사용자 상태 업데이트
                    usersCollection.document(currentUser.uid)
                        .update("emailVerified", true)
                        .await()

                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 현재 사용자의 이메일 인증 상태를 확인합니다.
     *
     * @return 이메일 인증 완료 여부
     */
    override suspend fun isEmailVerified(): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser ?: return@withContext false
            val userDoc = usersCollection.document(currentUser.uid).get().await()
            return@withContext userDoc.getBoolean("emailVerified") ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }

}