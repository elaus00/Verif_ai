package mp.verif_ai.presentation.viewmodel.prompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.prompt.PromptTemplate
import mp.verif_ai.domain.repository.PromptRepository
import javax.inject.Inject

@HiltViewModel
class PromptTemplatesViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {
    private val _templates = MutableStateFlow<List<PromptTemplate>>(emptyList())
    val templates: StateFlow<List<PromptTemplate>> = _templates.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            promptRepository.getPromptTemplates()
                .catch { e ->
                    // Handle error
                }
                .collect { templateList ->
                    _templates.value = templateList
                }
        }
    }
}