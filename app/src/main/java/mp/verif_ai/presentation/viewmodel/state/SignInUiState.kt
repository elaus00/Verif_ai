package mp.verif_ai.presentation.viewmodel.state

import mp.verif_ai.domain.model.auth.User

sealed class SignInUiState {
    object Initial : SignInUiState()
    object Loading : SignInUiState()
    data class Success(val user: User) : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}

sealed class SignInEvent {
    data class ShowError(val message: String) : SignInEvent()
    object NavigateToHome : SignInEvent()
}