package mp.verif_ai.domain.model.auth

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data class AuthenticationFailed(val error: Exception) : AuthState()
}

sealed class AuthException : Exception() {
    data class CredentialError(val exception: Exception) : AuthException()
    data class NoCredentialAvailable(val exception: Exception) : AuthException()
    data object UserNotFound : AuthException()
}