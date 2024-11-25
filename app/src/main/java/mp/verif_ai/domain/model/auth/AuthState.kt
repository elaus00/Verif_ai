package mp.verif_ai.domain.model.auth

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data class AuthenticationFailed(val error: Exception) : AuthState()
}