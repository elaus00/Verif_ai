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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Firebase가 사용할 빈 생성자
    constructor() : this(
        id = "",
        email = "",
        phoneNumber = null,
        nickname = "",
        type = UserType.NORMAL,
        status = UserStatus.ACTIVE,
        points = 0,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    fun toMap(): Map<String, Any?> {  // Return 타입을 Map<String, Any?>로 명시적 선언
        return mapOf(
            "id" to id,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "nickname" to nickname,
            "type" to type.name,  // enum을 String으로 저장
            "status" to status.name,  // enum을 String으로 저장
            "points" to points,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }

    companion object {
        // Firestore 문서에서 User 객체로 변환하는 함수
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                id = map["id"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String,
                nickname = map["nickname"] as? String ?: "",
                type = UserType.valueOf(map["type"] as? String ?: UserType.NORMAL.name),
                status = UserStatus.valueOf(map["status"] as? String ?: UserStatus.ACTIVE.name),
                points = (map["points"] as? Number)?.toInt() ?: 0,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
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