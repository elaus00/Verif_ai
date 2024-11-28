package mp.verif_ai.domain.model.auth

sealed class SignUpState {
    data object Initial : SignUpState()
    data object Loading : SignUpState()
    data class ExistingAccount(val email: String) : SignUpState()
    data class NewAccount(val email: String) : SignUpState()
    data class Error(val exception: Exception) : SignUpState()
}