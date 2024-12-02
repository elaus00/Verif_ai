package mp.verif_ai.domain.util.passkey

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import java.security.SecureRandom

data object PassKeyConfig {
    private const val RP_ID = "verifai-71428.firebaseapp.com"
    private const val RP_NAME = "Android client for mp.verif_ai"

    @OptIn(ExperimentalEncodingApi::class)
    private fun generateChallenge(): String {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }.let { Base64.encode(it) }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getCreateOptions(
        userId: String,
        displayName: String,
    ) = """
        {
            "rp": {
                "id": "$RP_ID",
                "name": "$RP_NAME"
            },
            "user": {
                "id": "${Base64.encode(userId.toByteArray())}",
                "name": "$userId",
                "displayName": "$displayName"
            },
            "challenge": "${generateChallenge()}",
            "pubKeyCredParams": [
                {
                    "type": "public-key",
                    "alg": -7
                }
            ],
            "timeout": 3000,
            "authenticatorSelection": {
                "authenticatorAttachment": "platform",
                "requireResidentKey": true,
                "userVerification": "preferred"
            }
        }
    """.trimIndent()

    fun getRequestOptions() = """
        {
            "rpId": "$RP_ID",
            "userVerification": "preferred",
            "challenge": "${generateChallenge()}",
            "timeout": 30000,
            "allowCredentials": []
        }
    """.trimIndent()
}