package mp.verif_ai.presentation.viewmodel.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.prompt.UserPrompt
import mp.verif_ai.domain.repository.PromptRepository
import javax.inject.Inject

@HiltViewModel
class PromptHistoryViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {
    private val _prompts = MutableStateFlow<List<UserPrompt>>(emptyList())
    val prompts: StateFlow<List<UserPrompt>> = _prompts.asStateFlow()

    init {
        loadPromptHistory()
    }

    private fun loadPromptHistory() {
        viewModelScope.launch {
            promptRepository.getPromptHistory()
                .catch { e ->
                    // Handle error
                }
                .collect { histories ->
                    _prompts.value = histories
                }
        }
    }
}