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
import mp.verif_ai.domain.model.User
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.UserRepository
import mp.verif_ai.domain.usecase.auth.SignInUseCase
import mp.verif_ai.domain.usecase.auth.SignUpUseCase
import mp.verif_ai.presentation.viewmodel.common.UiState
import javax.inject.Inject

/**
 * 사용자 인증 관련 기능을 처리하는 ViewModel입니다.
 * 회원가입, 로그인, 로그아웃 등의 인증 작업과 인증 상태를 관리합니다.
 *
 * @property signUpUseCase 회원가입 UseCase
 * @property signInUseCase 로그인 UseCase
 * @property authRepository 인증 관련 Repository
 * @property userRepository 사용자 정보 관련 Repository
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * 인증 작업의 현재 상태를 관리하는 StateFlow입니다.
     * 로딩, 성공, 에러 등의 상태를 포함합니다.
     */
    private val _authState = MutableStateFlow<UiState<User>>(UiState.Initial)
    val authState: StateFlow<UiState<User>> = _authState.asStateFlow()

    /**
     * 현재 로그인된 사용자 정보를 관리하는 StateFlow입니다.
     */
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * ViewModel 초기화 시 현재 로그인된 사용자 정보를 구독합니다.
     * 사용자 정보가 변경될 때마다 _currentUser가 업데이트됩니다.
     */
    init {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .collect { user ->
                    _currentUser.value = user
                }
        }
    }

    /**
     * 새로운 사용자 계정을 생성합니다.
     *
     * @param email 사용자 이메일
     * @param password 비밀번호
     * @param nickname 사용자 닉네임
     *
     * 처리 과정:
     * 1. Loading 상태로 변경
     * 2. SignUpUseCase 실행
     * 3. 결과에 따라 Success 또는 Error 상태로 변경
     */
    fun signUp(email: String, password: String, nickname: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            signUpUseCase(email, password, nickname)
                .onSuccess {
                    _authState.value = UiState.Success(it)
                }
                .onFailure {
                    _authState.value = UiState.Error(mapAuthError(it))
                }
        }
    }

    /**
     * 기존 사용자 계정으로 로그인합니다.
     *
     * @param email 사용자 이메일
     * @param password 비밀번호
     *
     * 처리 과정:
     * 1. Loading 상태로 변경
     * 2. SignInUseCase 실행
     * 3. 결과에 따라 Success 또는 Error 상태로 변경
     */
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

    /**
     * 현재 로그인된 사용자를 로그아웃합니다.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    /**
     * Firebase Authentication 에러를 사용자 친화적인 메시지로 변환합니다.
     *
     * @param error Firebase에서 발생한 예외
     * @return 사용자에게 표시할 에러 메시지
     *
     * 처리되는 에러 타입:
     * - FirebaseAuthInvalidCredentialsException: 잘못된 이메일/비밀번호
     * - FirebaseAuthInvalidUserException: 존재하지 않는 계정
     * - FirebaseAuthWeakPasswordException: 약한 비밀번호
     * - FirebaseAuthEmailException: 이미 사용 중인 이메일
     */
    private fun mapAuthError(error: Throwable): String = when (error) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
        is FirebaseAuthInvalidUserException -> "Account not found"
        is FirebaseAuthWeakPasswordException -> "Password is too weak"
        is FirebaseAuthEmailException -> "Email already in use"
        else -> error.message ?: "Unknown error occurred"
    }
}