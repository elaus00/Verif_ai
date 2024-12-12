package mp.verif_ai.domain.model.auth

sealed class AuthCredential {
    data class Email(
        val email: String,
        val password: String
    ) : AuthCredential()

    data class Google(
        val idToken: String
    ) : AuthCredential()

    data class PassKey(
        val authenticationResponseJson: String
    ) : AuthCredential()
}