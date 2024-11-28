package mp.verif_ai.domain.model.passkey

data class PassKeyInfo(
    val credentialId: String,
    val publicKey: String,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null,
    val name: String? = null,  // 사용자가 지정한 PassKey 이름
    val deviceName: String? = null  // PassKey가 생성된 기기 정보
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "credentialId" to credentialId,
        "publicKey" to publicKey,
        "userId" to userId,
        "createdAt" to createdAt,
        "lastUsedAt" to lastUsedAt,
        "name" to name,
        "deviceName" to deviceName
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): PassKeyInfo = PassKeyInfo(
            credentialId = map["credentialId"] as String,
            publicKey = map["publicKey"] as String,
            userId = map["userId"] as String,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            lastUsedAt = (map["lastUsedAt"] as? Number)?.toLong(),
            name = map["name"] as? String,
            deviceName = map["deviceName"] as? String
        )
    }
}