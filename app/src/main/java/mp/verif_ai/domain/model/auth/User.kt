package mp.verif_ai.domain.model.auth

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
    val signInMethod: String? = null  // EMAIL, GOOGLE, APPLE 추가
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
        fun fromMap(map: Map<String, Any?>): User = User(
            id = map["id"] as? String ?: "",
            email = map["email"] as? String ?: "",
            phoneNumber = map["phoneNumber"] as? String,
            nickname = map["nickname"] as? String ?: "",
            type = UserType.fromString(map["type"] as? String ?: ""),
            status = UserStatus.fromString(map["status"] as? String ?: ""),
            points = (map["points"] as? Number)?.toInt() ?: 0,
            emailVerified = map["emailVerified"] as? Boolean ?: false,
            expertVerificationStatus = map["expertVerificationStatus"] as? String,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            lastSignInAt = (map["lastSignInAt"] as? Number)?.toLong(),
            signInMethod = map["signInMethod"] as? String
        )
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