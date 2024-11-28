package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.passkey.*
import mp.verif_ai.domain.repository.PassKeyRepository
import javax.inject.Inject

@HiltViewModel
class PassKeyViewModel @Inject constructor(
    private val passKeyRepository: PassKeyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PassKeyUiState>(PassKeyUiState.Initial)
    val uiState: StateFlow<PassKeyUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PassKeyEvent>()
    val events: SharedFlow<PassKeyEvent> = _events

    private val _passKeys = MutableStateFlow<List<PassKeyInfo>>(emptyList())
    val passKeys: StateFlow<List<PassKeyInfo>> = _passKeys.asStateFlow()

    init {
        val currentUserId = getCurrentUserId() // AuthRepository에서 가져오거나 주입받아야 함
        if (currentUserId != null) {
            observePassKeys(currentUserId)
        }
    }

    fun checkPassKeyStatus() {
        viewModelScope.launch {
            _uiState.value = PassKeyUiState.Loading
            try {
                val status = passKeyRepository.checkPassKeyStatus()
                _uiState.value = PassKeyUiState.StatusChecked(status)
            } catch (e: Exception) {
                _uiState.value = PassKeyUiState.Error(e)
            }
        }
    }

    fun registerPassKey(userId: String, displayName: String?) {
        viewModelScope.launch {
            _uiState.value = PassKeyUiState.Loading
            when (val result = passKeyRepository.registerPassKey(userId, displayName)) {
                is PassKeyRegistrationResult.Success -> {
                    _uiState.value = PassKeyUiState.Registered
                    _events.emit(PassKeyEvent.RegistrationSuccess)
                }
                is PassKeyRegistrationResult.Error -> {
                    _uiState.value = PassKeyUiState.Error(result.exception)
                    _events.emit(PassKeyEvent.ShowError(result.exception.message ?: "Failed to register passkey"))
                }
            }
        }
    }

//    fun signInWithPassKey() {
//        viewModelScope.launch {
//            _uiState.value = PassKeyUiState.Loading
//            passKeyRepository.signInWithPassKey()
//                .onSuccess { result ->
//                    when (result) {
//                        is PassKeySignInResult.Success -> {
//                            _uiState.value = PassKeyUiState.SignedIn(result.userId)
//                            _events.emit(PassKeyEvent.SignInSuccess)
//                        }
//                        is PassKeySignInResult.Error -> {
//                            _uiState.value = PassKeyUiState.Error(result.exception)
//                            _events.emit(PassKeyEvent.ShowError(result.exception.message ?: "Sign in failed"))
//                        }
//                    }
//                }
//                .onFailure { e ->
//                    _uiState.value = PassKeyUiState.Error(e)
//                    _events.emit(PassKeyEvent.ShowError(e.message ?: "Sign in failed"))
//                }
//        }
//    }

    fun removePassKey(credentialId: String) {
        viewModelScope.launch {
            _uiState.value = PassKeyUiState.Loading
            passKeyRepository.removePassKey(credentialId)
                .onSuccess {
                    _events.emit(PassKeyEvent.PassKeyRemoved)
                }
                .onFailure { e ->
                    _uiState.value = PassKeyUiState.Error(e)
                    _events.emit(PassKeyEvent.ShowError(e.message ?: "Failed to remove passkey"))
                }
        }
    }

    private fun observePassKeys(userId: String) {
        viewModelScope.launch {
            passKeyRepository.observePassKeys(userId)
                .catch { e ->
                    _events.emit(PassKeyEvent.ShowError(e.message ?: "Failed to observe passkeys"))
                }
                .collect { passKeys ->
                    _passKeys.value = passKeys
                }
        }
    }

    fun resetError() {
        _uiState.value = PassKeyUiState.Initial
    }

    private fun getCurrentUserId(): String? {
        // AuthRepository에서 현재 사용자 ID를 가져오는 로직 필요
        return null
    }
}

sealed class PassKeyUiState {
    data object Initial : PassKeyUiState()
    data object Loading : PassKeyUiState()
    data class StatusChecked(val status: PassKeyStatus) : PassKeyUiState()
    data object Registered : PassKeyUiState()
    data class SignedIn(val userId: String) : PassKeyUiState()
    data class Error(val error: Throwable) : PassKeyUiState()
}

sealed class PassKeyEvent {
    data object RegistrationSuccess : PassKeyEvent()
    data object SignInSuccess : PassKeyEvent()
    data object PassKeyRemoved : PassKeyEvent()
    data class ShowError(val message: String) : PassKeyEvent()
}