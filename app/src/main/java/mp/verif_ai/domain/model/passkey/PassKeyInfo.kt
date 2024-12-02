package mp.verif_ai.domain.model.passkey

data class PassKeyInfo(
    val credentialId: String,          // Credential Manager에서 생성한 고유 ID
    val publicKeyData: String,         // 응답으로 받은 public key 데이터
    val userId: String,                // user.name에서 얻은 값
    val displayName: String? = null,   // user.displayName에서 얻은 값
    val clientDataHash: String? = null, // 인증 해시값
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null,
    // deviceInfo는 옵션이지만 유용할 수 있는 정보들
    val deviceInfo: DeviceInfo? = null
) {
    data class DeviceInfo(
        val model: String = android.os.Build.MODEL,
        val manufacturer: String = android.os.Build.MANUFACTURER,
        val sdkVersion: Int = android.os.Build.VERSION.SDK_INT
    )

    fun toMap(): Map<String, Any?> = buildMap {
        put("credentialId", credentialId)
        put("publicKeyData", publicKeyData)
        put("userId", userId)
        put("displayName", displayName)
        put("clientDataHash", clientDataHash)
        put("createdAt", createdAt)
        put("lastUsedAt", lastUsedAt)
        deviceInfo?.let { device ->
            put("deviceModel", device.model)
            put("deviceManufacturer", device.manufacturer)
            put("deviceSdkVersion", device.sdkVersion)
        }
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): PassKeyInfo = PassKeyInfo(
            credentialId = map["credentialId"] as String,
            publicKeyData = map["publicKeyData"] as String,
            userId = map["userId"] as String,
            displayName = map["displayName"] as? String,
            clientDataHash = map["clientDataHash"] as? String,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            lastUsedAt = (map["lastUsedAt"] as? Number)?.toLong(),
            deviceInfo = if (map.containsKey("deviceModel")) {
                DeviceInfo(
                    model = map["deviceModel"] as String,
                    manufacturer = map["deviceManufacturer"] as String,
                    sdkVersion = (map["deviceSdkVersion"] as Number).toInt()
                )
            } else null
        )
    }
}