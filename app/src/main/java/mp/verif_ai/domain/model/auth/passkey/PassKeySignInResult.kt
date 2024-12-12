package mp.verif_ai.domain.model.auth.passkey

sealed class PassKeySignInResult {
    data class Success(
        val credentialId: String,
        val userId: String
    ) : PassKeySignInResult()
    data class Error(val exception: Exception) : PassKeySignInResult()
}
