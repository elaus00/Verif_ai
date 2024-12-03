package mp.verif_ai.presentation.viewmodel.prompt

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VoiceRecognitionViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<VoiceRecognitionState>(VoiceRecognitionState.Idle)
    val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<VoiceRecognitionEvent>()
    val events = _events.asSharedFlow()

    fun handleVoiceRecognitionResult(result: ActivityResult) {
        viewModelScope.launch {
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                results?.get(0)?.let { recognizedText ->
                    _state.value = VoiceRecognitionState.Success(recognizedText)
                    _events.emit(VoiceRecognitionEvent.RecognitionSuccess(recognizedText))
                }
            } else {
                _state.value = VoiceRecognitionState.Error("음성 인식에 실패했습니다")
                _events.emit(VoiceRecognitionEvent.RecognitionError("음성 인식에 실패했습니다"))
            }
        }
    }

    fun resetState() {
        _state.value = VoiceRecognitionState.Idle
    }
}

sealed class VoiceRecognitionState {
    data object Idle : VoiceRecognitionState()
    data object Recording : VoiceRecognitionState()
    data class Success(val text: String) : VoiceRecognitionState()
    data class Error(val message: String) : VoiceRecognitionState()
}

sealed class VoiceRecognitionEvent {
    data class RecognitionSuccess(val text: String) : VoiceRecognitionEvent()
    data class RecognitionError(val message: String) : VoiceRecognitionEvent()
}