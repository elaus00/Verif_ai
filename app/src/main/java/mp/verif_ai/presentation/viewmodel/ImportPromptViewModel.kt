package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.usecase.prompt.CreateQuestionFromPromptUseCase
import mp.verif_ai.domain.usecase.prompt.ImportPromptUseCase
import mp.verif_ai.domain.util.Resource
import mp.verif_ai.presentation.screens.prompt.ImportPromptUiState

@HiltViewModel
class ImportPromptViewModel @Inject constructor(
    private val importPromptUseCase: ImportPromptUseCase,
    private val createQuestionUseCase: CreateQuestionFromPromptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportPromptUiState>(ImportPromptUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun importPrompt(promptText: String) {
        viewModelScope.launch {
            importPromptUseCase(promptText)
                .collect { resource ->
                    _uiState.value = when (resource) {
                        is Resource.Loading -> ImportPromptUiState.Loading
                        is Resource.Success -> ImportPromptUiState.Success(resource.data!!)
                        is Resource.Error -> ImportPromptUiState.Error(resource.message ?: "Unknown error")
                    }
                }
        }
    }

//    fun createQuestion(conversationId: String) {
//        viewModelScope.launch {
//            createQuestionUseCase(conversationId)
//                .collect { resource ->
//                    _uiState.value = when (resource) {
//                        is Resource.Loading -> ImportPromptUiState.Loading
//                        is Resource.Success -> ImportPromptUiState.QuestionCreated(resource.data)
//                        is Resource.Error -> ImportPromptUiState.Error(resource.message ?: "Unknown error")
//                    }
//                }
//        }
//    }
}
