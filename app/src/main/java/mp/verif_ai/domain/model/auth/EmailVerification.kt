package mp.verif_ai.domain.model.auth

import java.sql.Timestamp

data class EmailVerification(
    val email: String,
    val code: String,
    val expirationTime: Long,
    val verified: Boolean = false,
    val attempts: Int = 0,
    val verifiedAt: Long? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "email" to email,
        "code" to code,
        "expirationTime" to expirationTime,
        "verified" to verified,
        "attempts" to attempts,
        "verifiedAt" to verifiedAt
    )
}