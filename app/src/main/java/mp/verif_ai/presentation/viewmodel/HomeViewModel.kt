package mp.verif_ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.Question
import mp.verif_ai.presentation.screens.Screen

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val questions: List<Question>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshQuestions()
    }

    fun refreshQuestions() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // TODO: API 호출
                _uiState.value = HomeUiState.Success(emptyList())
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("질문을 불러오는데 실패했습니다.")
            }
        }
    }
}