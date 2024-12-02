package mp.verif_ai.presentation.viewmodel

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.passkey.*
import mp.verif_ai.domain.repository.PassKeyRepository
import mp.verif_ai.domain.util.passkey.PassKeyConfig
import mp.verif_ai.domain.util.passkey.PassKeyConfig.getRequestOptions
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

    fun registerPassKey(userId: String, displayName: String?, context: ComponentActivity) {
        viewModelScope.launch {
            _uiState.value = PassKeyUiState.Loading
            when (val result = passKeyRepository.registerPassKey(userId, displayName, context)) {
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

    fun signInWithPassKey(context: ComponentActivity) {
        Log.d(TAG, "Starting PassKey sign-in process")
        viewModelScope.launch {
            _uiState.value = PassKeyUiState.Loading
            Log.d(TAG, "UI State changed to Loading")

            try {
                Log.d(TAG, "Creating CredentialManager instance")
                val credentialManager = CredentialManager.create(context)

                Log.d(TAG, "Creating credential request")
                val credentialRequest = createCredentialRequest()

                Log.d(TAG, "Attempting to get credential")
                val result = try {
                    credentialManager.getCredential(
                        context = context,
                        request = credentialRequest
                    ).also {
                        Log.d(TAG, "Successfully retrieved credential")
                    }
                } catch (e: GetCredentialException) {
                    when (e) {
                        is GetCredentialCancellationException -> {
                            Log.d(TAG, "Authentication cancelled by user")
                            _uiState.value = PassKeyUiState.Initial
                            _events.emit(PassKeyEvent.ShowError("Authentication cancelled"))
                            return@launch
                        }
                        is GetCredentialInterruptedException -> {
                            Log.d(TAG, "Authentication interrupted: ${e.message}")
                            _uiState.value = PassKeyUiState.Error(e)  // 상태 업데이트 추가
                            _events.emit(PassKeyEvent.ShowError("Authentication interrupted"))
                            return@launch
                        }
                        is NoCredentialException -> {
                            Log.d(TAG, "No credential available for authentication")
                            _uiState.value = PassKeyUiState.Initial
                            _events.emit(PassKeyEvent.NoCredentialAvailable)
                            return@launch
                        }
                        else -> {
                            _uiState.value = PassKeyUiState.Error(e)  // 상태 업데이트 추가
                            Log.e(TAG, "Unexpected credential exception", e)
                            throw e
                        }
                    }
                }

                Log.d(TAG, "Attempting to sign in with PassKey")
                passKeyRepository.signInWithPassKey(result)
                    .onSuccess { signInResult ->
                        when (signInResult) {
                            is PassKeySignInResult.Success -> {
                                Log.d(TAG, "PassKey sign-in successful for user: ${signInResult.userId}")
                                _uiState.value = PassKeyUiState.SignedIn(signInResult.userId)
                                _events.emit(PassKeyEvent.SignInSuccess)
                            }
                            is PassKeySignInResult.Error -> {
                                Log.e(TAG, "PassKey sign-in error", signInResult.exception)
                                _uiState.value = PassKeyUiState.Error(signInResult.exception)
                                _events.emit(PassKeyEvent.ShowError(
                                    signInResult.exception.message ?: "Sign in failed"
                                ))
                            }
                        }
                    }
                    .onFailure { e ->
                        Log.e(TAG, "PassKey sign-in failure", e)
                        _uiState.value = PassKeyUiState.Error(e)
                        _events.emit(PassKeyEvent.ShowError(e.message ?: "Sign in failed"))
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during PassKey sign-in", e)
                _uiState.value = PassKeyUiState.Error(e)
                _events.emit(PassKeyEvent.ShowError("An unexpected error occurred"))
            }
        }
    }

    companion object {
        private const val TAG = "SignInViewModel"
    }

    private fun createCredentialRequest(): GetCredentialRequest {
        val passwordOption = GetPasswordOption(
            allowedUserIds = emptySet(),
            allowedProviders = emptySet(),
            isAutoSelectAllowed = true
        )

        val publicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = getRequestOptions()
        )

        return GetCredentialRequest(
            listOf(passwordOption, publicKeyCredentialOption)
        )
    }


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
    data object NoCredentialAvailable : PassKeyEvent()
    data class ShowError(val message: String) : PassKeyEvent()
}