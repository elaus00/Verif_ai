package mp.verif_ai.domain.model.auth

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val exception: Exception) : AuthResult()
    data object Loading : AuthResult()
}