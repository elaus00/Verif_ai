package mp.verif_ai.domain.model.auth

data class ExpertProfile(
    val userId: String = "",  // expertId를 userId로 변경
    val nickname: String = "", // 전문가 활동명 추가
    val specialties: List<String> = emptyList(),
    val introduction: String = "",  // 전문가 소개 추가
    val certification: String? = null,
    val experience: Int = 0,  // 경력 연차 추가
    val status: ExpertStatus = ExpertStatus.PENDING,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,  // 리뷰 수 추가
    val answersCount: Int = 0,
    val adoptionRate: Double = 0.0,  // Int에서 Double로 변경
    val totalEarnings: Int = 0,  // 총 수익 추가
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long? = null  // 마지막 활동 시간 추가
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "nickname" to nickname,
        "specialties" to specialties,
        "introduction" to introduction,
        "certification" to certification,
        "experience" to experience,
        "status" to status.name,
        "rating" to rating,
        "reviewCount" to reviewCount,
        "answersCount" to answersCount,
        "adoptionRate" to adoptionRate,
        "totalEarnings" to totalEarnings,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt,
        "lastActiveAt" to lastActiveAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): ExpertProfile = ExpertProfile(
            userId = map["userId"] as? String ?: "",
            nickname = map["nickname"] as? String ?: "",
            specialties = (map["specialties"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            introduction = map["introduction"] as? String ?: "",
            certification = map["certification"] as? String,
            experience = (map["experience"] as? Number)?.toInt() ?: 0,
            status = ExpertStatus.fromString(map["status"] as? String ?: ""),
            rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
            reviewCount = (map["reviewCount"] as? Number)?.toInt() ?: 0,
            answersCount = (map["answersCount"] as? Number)?.toInt() ?: 0,
            adoptionRate = (map["adoptionRate"] as? Number)?.toDouble() ?: 0.0,
            totalEarnings = (map["totalEarnings"] as? Number)?.toInt() ?: 0,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            lastActiveAt = (map["lastActiveAt"] as? Number)?.toLong()
        )
    }
}

enum class ExpertStatus {
    PENDING,    // 검증 대기중
    VERIFIED,   // 검증 완료
    REJECTED,   // 거절됨
    SUSPENDED;  // 활동 정지

    companion object {
        fun fromString(value: String): ExpertStatus = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            PENDING
        }
    }
}

// 전문가 분야를 enum으로 관리
enum class ExpertSpecialty {
    ACCOUNTING,         // 회계
    TAX,               // 세무
    CORPORATE_LAW,     // 기업법
    LABOR_LAW,         // 노동법
    REAL_ESTATE,       // 부동산
    INTELLECTUAL_PROPERTY,  // 지적재산권
    INTERNATIONAL_TRADE,    // 국제무역
    INVESTMENT,        // 투자
    BUSINESS_PLANNING, // 사업계획
    OTHER;            // 기타

    companion object {
        fun fromString(value: String): ExpertSpecialty = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            OTHER
        }
    }
}