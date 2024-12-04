package mp.verif_ai.domain.model.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

data class User(
    val id: String = "",
    val email: String = "",
    val phoneNumber: String? = null,
    val nickname: String = "",
    val type: UserType = UserType.NORMAL,
    val status: UserStatus = UserStatus.ACTIVE,
    val points: Int = 0,
    val emailVerified: Boolean = false,
    val expertVerificationStatus: String? = null,  // PENDING, APPROVED, REJECTED 추가
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastSignInAt: Long? = null,  // 마지막 로그인 시간 추가
    val signInMethod: String? = null,  // EMAIL, GOOGLE 추가
    val displayName: String? = null,
) {
    /**
     * Firestore에서 사용할 Map으로 변환합니다.
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "email" to email,
        "phoneNumber" to phoneNumber,
        "nickname" to nickname,
        "type" to type.name,
        "status" to status.name,
        "points" to points,
        "emailVerified" to emailVerified,
        "expertVerificationStatus" to expertVerificationStatus,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "lastSignInAt" to lastSignInAt,
        "signInMethod" to signInMethod
    )

    companion object {
        /**
         * Firestore 문서를 User 객체로 변환합니다.
         */
        fun fromFirebaseUser(firebaseUser: FirebaseUser?): User? {
            return firebaseUser?.let { fbUser ->
                User(
                    id = fbUser.uid,
                    email = fbUser.email ?: "",
                    phoneNumber = fbUser.phoneNumber,
                    nickname = fbUser.displayName ?: "",
                    emailVerified = fbUser.isEmailVerified,
                    signInMethod = when {
                        fbUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } ->
                            SignInMethod.GOOGLE.name
                        fbUser.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID } ->
                            SignInMethod.EMAIL.name
                        fbUser.providerData.any { it.providerId == "apple.com" } ->
                            SignInMethod.APPLE.name
                        else -> SignInMethod.EMAIL.name
                    },
                    lastSignInAt = fbUser.metadata?.lastSignInTimestamp
                )
            }
        }
    }
}

enum class UserType {
    NORMAL,
    EXPERT;

    companion object {
        fun fromString(value: String): UserType = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            NORMAL
        }
    }
}

// SignInMethod를 enum으로 추가
enum class SignInMethod {
    EMAIL,
    GOOGLE,
    APPLE;

    companion object {
        fun fromString(value: String): SignInMethod = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            EMAIL
        }
    }
}

// 전문가 인증 상태를 enum으로 추가
enum class ExpertVerificationStatus {
    PENDING,
    APPROVED,
    REJECTED;

    companion object {
        fun fromString(value: String): ExpertVerificationStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            REJECTED
        }
    }
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    BANNED;

    companion object {
        fun fromString(value: String): UserStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            ACTIVE
        }
    }
}