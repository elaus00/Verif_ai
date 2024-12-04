package mp.verif_ai.presentation.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.util.passkey.*
import mp.verif_ai.presentation.screens.auth.SignUpOptions
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events

    init {
        observeAuthState()
    }

    fun handleSignUpOption(option: SignUpOptions.Option, context: ComponentActivity) {
        when (option) {
            SignUpOptions.Option.GOOGLE -> {
                viewModelScope.launch {
                    _uiState.value = AuthUiState.Loading
                    // Google 로그인 처리
                }
            }
            SignUpOptions.Option.PHONE -> {
                // 전화번호 입력을 위한 이벤트 발생
                viewModelScope.launch {
                    _events.emit(AuthEvent.ShowPhoneInput)
                }
            }
            SignUpOptions.Option.PASSWORD -> {
                // 비밀번호 입력을 위한 이벤트 발생
                viewModelScope.launch {
                    _events.emit(AuthEvent.ShowPasswordInput)
                }
            }
        }
    }

    private fun signUpWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepository.signUpWithCredentialManager(
                    email = "",  // Google SignIn will provide this
                    password = "",  // Not needed for Google SignIn
                    nickname = "",  // Will be taken from Google account
                    context = activity
                ).onSuccess { user ->
                    _uiState.value = AuthUiState.Authenticated(user)
                    _events.emit(AuthEvent.NavigateToMain)
                }.onFailure { e ->
                    _uiState.value = AuthUiState.Error(e)
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign up with Google"))
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e)
                _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign up with Google"))
            }
        }
    }

    private fun showPhoneSignUpDialog() {
        viewModelScope.launch {
            _events.emit(AuthEvent.ShowPhoneInput)
        }
    }

    private fun showPasswordSignUpDialog() {
        viewModelScope.launch {
            _events.emit(AuthEvent.ShowPasswordInput)
        }
    }

    fun signUpWithPhone(phoneNumber: String, activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            // Phone number verification implementation
        }
    }

    fun signUpWithCredentialManager(email: String, password: String, nickname: String, context: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUpWithCredentialManager(email, password, nickname, context, enablePassKey = false)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Authenticated(user)
                    _events.emit(AuthEvent.NavigateToMain)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e)
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign up"))
                }
        }
    }

    fun signUpWithPassword(password: String, activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUpWithCredentialManager(
                email = "",  // PassKey/Credential Manager에서 처리
                password = password,
                nickname = "", // PassKey/Credential Manager에서 처리
                context = activity
            ).onSuccess { user ->
                _uiState.value = AuthUiState.Authenticated(user)
                _events.emit(AuthEvent.NavigateToMain)
            }.onFailure { e ->
                _uiState.value = AuthUiState.Error(e)
                _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign up with password"))
            }
        }
    }

    fun signIn(email: String? = null, password: String? = null, activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signIn(activity, email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Authenticated(user)
                    _events.emit(AuthEvent.NavigateToMain)
                }
                .onFailure { e ->
                    when (e) {
                        is PassKeyCancellationException -> {
                            _uiState.value = AuthUiState.Initial
                        }
                        is PassKeyNoCredentialException -> {
                            _uiState.value = AuthUiState.NoCredential
                        }
                        else -> {
                            _uiState.value = AuthUiState.Error(e)
                            _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign in"))
                        }
                    }
                }
        }
    }


    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess {
                    _uiState.value = AuthUiState.Initial
                    _events.emit(AuthEvent.NavigateToAuth)
                }
                .onFailure { e ->
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to sign out"))
                }
        }
    }

    fun withdraw() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.withdraw()
                .onSuccess {
                    _uiState.value = AuthUiState.Initial
                    _events.emit(AuthEvent.NavigateToAuth)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e)
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to withdraw"))
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email)
                .onSuccess {
                    _events.emit(AuthEvent.PasswordResetEmailSent)
                }
                .onFailure { e ->
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to send reset email"))
                }
        }
    }

    fun sendVerificationEmail(email: String) {
        viewModelScope.launch {
            authRepository.sendVerificationEmail(email)
                .onSuccess {
                    _events.emit(AuthEvent.VerificationEmailSent)
                }
                .onFailure { e ->
                    _events.emit(AuthEvent.ShowError(e.message ?: "Failed to send verification email"))
                }
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect { user ->
                _uiState.value = if (user != null) {
                    AuthUiState.Authenticated(user)
                } else {
                    AuthUiState.Initial
                }
            }
        }
    }
    fun showError(message: String) {
        viewModelScope.launch {
            _events.emit(AuthEvent.ShowError(message))
        }
    }


}

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data object NoCredential : AuthUiState()
    data class Authenticated(val user: User) : AuthUiState()
    data class ExistingAccount(val email: String) : AuthUiState()
    data class NewAccount(val email: String) : AuthUiState()
    data class Error(val exception: Throwable) : AuthUiState()
}

sealed class AuthEvent {
    data class ShowError(val message: String) : AuthEvent()
    data object NavigateToMain : AuthEvent()
    data object NavigateToAuth : AuthEvent()
    data object PasswordResetEmailSent : AuthEvent()
    data object VerificationEmailSent : AuthEvent()
    data object ShowPhoneInput : AuthEvent()
    data object ShowPasswordInput : AuthEvent()
}

