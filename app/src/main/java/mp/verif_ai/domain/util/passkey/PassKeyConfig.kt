package mp.verif_ai.domain.util.passkey

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object PassKeyConfig {
//    private const val RP_ID = "android:apk-key-hash-[B@24bdbe8"
    private const val RP_ID = "verifai-71428.firebaseapp.com"
    private const val RP_NAME = "Android client for mp.verif_ai"

    @OptIn(ExperimentalEncodingApi::class)
    fun getCreateOptions(
        userId: String,
        displayName: String,
        challenge: String
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
            "challenge": "$challenge",
            "pubKeyCredParams": [
                {
                    "type": "public-key",
                    "alg": -7
                }
            ],
            "timeout": 30000,
            "authenticatorSelection": {
                "authenticatorAttachment": "platform",
                "requireResidentKey": true,
                "userVerification": "preferred"
            }
        }
    """.trimIndent()

    fun getRequestOptions(challenge: String) = """
        {
            "rpId": "$RP_ID",
            "rpName": "$RP_NAME",
            "userVerification": "preferred",
            "challenge": "$challenge",
            "timeout": 30000,
            "allowCredentials": []
        }
    """.trimIndent()
}