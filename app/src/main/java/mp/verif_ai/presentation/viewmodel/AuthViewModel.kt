package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.UserRepository
import mp.verif_ai.domain.usecase.auth.SignInUseCase
import mp.verif_ai.domain.usecase.auth.SignUpUseCase
import mp.verif_ai.domain.usecase.auth.VerifyEmailUseCase
import javax.inject.Inject

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object UserNotFound : AuthError()
    object WeakPassword : AuthError()
    object EmailInUse : AuthError()
    object UserNotLoggedIn : AuthError()
    data class Unknown(val message: String) : AuthError()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Initial)
    val authState: StateFlow<UiState<User>> = _authState.asStateFlow()

    private val _verificationState = MutableStateFlow<UiState<Unit>>(UiState.Initial)
    val verificationState: StateFlow<UiState<Unit>> = _verificationState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _isExpertMode = MutableStateFlow(false)
    val isExpertMode: StateFlow<Boolean> = _isExpertMode.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .collect { user ->
                    _currentUser.value = user
                }
        }
    }

    fun setEmail(email: String): Boolean {
        return if (isValidEmail(email)) {
            _email.value = email
            true
        } else {
            false
        }
    }

    fun signUp(email: String, password: String, nickname: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            signUpUseCase(email, password, nickname)
                .onSuccess {
                    _authState.value = UiState.Success(it)
                    sendVerificationEmail()
                }
                .onFailure {
                    _authState.value = UiState.Error(mapAuthError(it))
                }
        }
    }

    private fun sendVerificationEmail() {
        viewModelScope.launch {
            _verificationState.value = UiState.Loading
            verifyEmailUseCase(email.value)
                .onSuccess {
                    _verificationState.value = UiState.Success(Unit)
                }
                .onFailure {
                    _verificationState.value = UiState.Error(mapAuthError(it))
                }
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            _verificationState.value = UiState.Loading
            authRepository.verifyEmailCode(code)
                .onSuccess {
                    _verificationState.value = UiState.Success(Unit)
                }
                .onFailure {
                    _verificationState.value = UiState.Error(mapAuthError(it))
                }
        }
    }

    fun setExpertMode(isExpert: Boolean) {
        _isExpertMode.value = isExpert
    }

    fun submitExpertInfo(expertInfo: Map<String, Any>) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                _authState.value = UiState.Loading
                userRepository.updateExpertInfo(user.id, expertInfo)
                    .onSuccess {
                        _authState.value = UiState.Success(it)
                    }
                    .onFailure {
                        _authState.value = UiState.Error(mapAuthError(it))
                    }
            } ?: run {
                _authState.value = UiState.Error(AuthError.UserNotLoggedIn)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            signInUseCase(email, password)
                .onSuccess {
                    _authState.value = UiState.Success(it)
                }
                .onFailure {
                    _authState.value = UiState.Error(mapAuthError(it))
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun mapAuthError(error: Throwable): AuthError = when (error) {
        is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
        is FirebaseAuthInvalidUserException -> AuthError.UserNotFound
        is FirebaseAuthWeakPasswordException -> AuthError.WeakPassword
        is FirebaseAuthEmailException -> AuthError.EmailInUse
        else -> AuthError.Unknown(error.message ?: "Unknown error occurred")
    }
}