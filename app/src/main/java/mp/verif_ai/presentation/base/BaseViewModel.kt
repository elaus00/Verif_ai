package mp.verif_ai.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()
    
    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                block()
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error")
            } finally {
                _loading.value = false
            }
        }
    }
}
