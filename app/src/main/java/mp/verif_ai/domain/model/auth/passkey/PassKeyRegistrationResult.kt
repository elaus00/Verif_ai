package mp.verif_ai.domain.model.auth.passkey

sealed class PassKeyRegistrationResult {
    data class Success(val credentialId: String) : PassKeyRegistrationResult()
    data class Error(val exception: Exception) : PassKeyRegistrationResult()
}