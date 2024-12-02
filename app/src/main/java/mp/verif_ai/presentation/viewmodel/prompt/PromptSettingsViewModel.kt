package mp.verif_ai.presentation.viewmodel.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.repository.PromptSettingsRepository
import javax.inject.Inject

@HiltViewModel
class PromptSettingsViewModel @Inject constructor(
    private val promptSettingsRepository: PromptSettingsRepository
) : ViewModel() {

    data class PromptSettings(
        val model: String = "GPT-4",
        val temperature: Float = 0.7f,
        val maxTokens: Int = 2000,
        val apiKey: String = ""
    )

    private val _settings = MutableStateFlow(PromptSettings())
    val settings: StateFlow<PromptSettings> = _settings.asStateFlow()

    private val _events = MutableSharedFlow<PromptSettingsEvent>()
    val events = _events.asSharedFlow()

    fun loadSettings() {
        viewModelScope.launch {
            try {
                // TODO: Implement loading settings from repository
                val loadedSettings = promptSettingsRepository.getPromptSettings()
                _settings.value = loadedSettings
            } catch (e: Exception) {
                emitEvent(PromptSettingsEvent.Error("Failed to load settings"))
            }
        }
    }

    fun updateModel(model: String) {
        viewModelScope.launch {
            try {
                _settings.value = _settings.value.copy(model = model)
                saveSettings()
            } catch (e: Exception) {
                emitEvent(PromptSettingsEvent.Error("Failed to update model"))
            }
        }
    }

    fun updateTemperature(temperature: Float) {
        viewModelScope.launch {
            try {
                _settings.value = _settings.value.copy(temperature = temperature)
                saveSettings()
            } catch (e: Exception) {
                emitEvent(PromptSettingsEvent.Error("Failed to update temperature"))
            }
        }
    }

    fun updateMaxTokens(maxTokens: Int) {
        viewModelScope.launch {
            try {
                _settings.value = _settings.value.copy(maxTokens = maxTokens)
                saveSettings()
            } catch (e: Exception) {
                emitEvent(PromptSettingsEvent.Error("Failed to update max tokens"))
            }
        }
    }

    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            try {
                // TODO: Implement secure storage of API key
                _settings.value = _settings.value.copy(apiKey = apiKey)
                saveSettings()
                emitEvent(PromptSettingsEvent.Success("Settings saved successfully"))
            } catch (e: Exception) {
                emitEvent(PromptSettingsEvent.Error("Failed to save API key"))
            }
        }
    }

    private suspend fun saveSettings() {
        try {
            promptSettingsRepository.savePromptSettings(_settings.value)
        } catch (e: Exception) {
            emitEvent(PromptSettingsEvent.Error("Failed to save settings"))
        }
    }

    private suspend fun emitEvent(event: PromptSettingsEvent) {
        _events.emit(event)
    }
}

sealed class PromptSettingsEvent {
    data class Success(val message: String) : PromptSettingsEvent()
    data class Error(val message: String) : PromptSettingsEvent()
}