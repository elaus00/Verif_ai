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
import mp.verif_ai.presentation.viewmodel.state.ChatUiState
import javax.inject.Inject

@HiltViewModel
class PromptDetailViewModel @Inject constructor(
    private val promptRepository: PromptRepository
) : ViewModel() {

    data class PromptDetailState(
        val id: String,
        val messages: List<ChatUiState>,
        val createdAt: Long
    )

    private val _promptDetail = MutableStateFlow<PromptDetailState?>(null)
    val promptDetail: StateFlow<PromptDetailState?> = _promptDetail.asStateFlow()

    fun loadPromptDetail(promptId: String) {
        viewModelScope.launch {
            try {
                // repository에서 데이터를 가져와서 PromptDetailState로 변환
                val detail = PromptDetailState(
                    id = promptId,
                    messages = emptyList(), // 실제로는 repository에서 가져온 메시지 리스트
                    createdAt = System.currentTimeMillis()
                )
                _promptDetail.value = detail
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}