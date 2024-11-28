package mp.verif_ai.domain.model.passkey

sealed class PassKeyRegistrationResult {
    data class Success(val credentialId: String) : PassKeyRegistrationResult()
    data class Error(val exception: Exception) : PassKeyRegistrationResult()
}