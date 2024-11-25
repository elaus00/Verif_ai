package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.auth.AuthCredential
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkExistingAccount(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.checkExistingAccount(email)
                .onSuccess { exists ->
                    _uiState.value = if (exists) {
                        AuthUiState.ExistingAccount(email)
                    } else {
                        AuthUiState.NewAccount(email)
                    }
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception)
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception)
                }
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signInWithCredential(credential)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception)
                }
        }
    }

    fun signUpWithEmail(email: String, password: String, nickname: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signUpWithEmail(email, password, nickname)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.SignedIn(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception)
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.signOut()
                .onSuccess {
                    _uiState.value = AuthUiState.SignedOut
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception)
                }
        }
    }

    fun resetErrorState() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Initial
        }
    }

    fun handleError(exception: Throwable) {
        _uiState.value = when (exception) {
            is IllegalArgumentException -> AuthUiState.Error(exception) // 유효성 검사 실패
            is ApiException -> when (exception.statusCode) {
                SIGN_IN_CANCELLED -> AuthUiState.Initial // 사용자가 취소
                NETWORK_ERROR -> AuthUiState.Error(Exception("네트워크 연결을 확인해주세요"))
                else -> AuthUiState.Error(Exception("로그인에 실패했습니다"))
            }
            is FirebaseAuthInvalidCredentialsException -> AuthUiState.Error(Exception("이메일 또는 비밀번호가 올바르지 않습니다"))
            is FirebaseAuthInvalidUserException -> AuthUiState.Error(Exception("존재하지 않는 계정입니다"))
            is FirebaseAuthWeakPasswordException -> AuthUiState.Error(Exception("비밀번호가 너무 약합니다"))
            is FirebaseAuthEmailException -> AuthUiState.Error(Exception("이미 사용중인 이메일입니다"))
            else -> AuthUiState.Error(Exception("알 수 없는 오류가 발생했습니다"))
        }
    }
}

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class ExistingAccount(val email: String) : AuthUiState()
    data class NewAccount(val email: String) : AuthUiState()
    data class SignedIn(val user: User) : AuthUiState()
    data object SignedOut : AuthUiState()
    data class Error(val exception: Throwable) : AuthUiState()
}