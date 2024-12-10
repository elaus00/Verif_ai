package mp.verif_ai.presentation.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.question.Comment
import mp.verif_ai.domain.model.question.CommentParentType
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.CommentRepository
import mp.verif_ai.domain.util.NotificationManager
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommentUiState>(CommentUiState.Initial)
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CommentEvent>()
    val events: SharedFlow<CommentEvent> = _events.asSharedFlow()

    private var currentParentId: String? = null
    private var currentParentType: CommentParentType? = null

    fun createComment(parentId: String, parentType: CommentParentType, content: String) {
        if (content.isBlank()) {
            viewModelScope.launch {
                _events.emit(CommentEvent.ShowError("댓글 내용을 입력해주세요"))
            }
            return
        }

        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = CommentUiState.Loading

                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(CommentEvent.ShowError("로그인이 필요합니다"))

                val comment = Comment(
                    content = content,
                    authorId = currentUser.id,
                    authorName = currentUser.nickname,
                    parentId = parentId,
                    parentType = parentType
                )

                commentRepository.createComment(comment)
                    .onSuccess { commentId ->
                        _events.emit(CommentEvent.CommentCreated)
                        _events.emit(CommentEvent.ShowSnackbar("댓글이 등록되었습니다"))
                        notificationManager.showNotification(
                            title = "새로운 댓글",
                            content = content,
                            questionId = if (parentType == CommentParentType.QUESTION) parentId else null
                        )
                        refreshComments(parentId, parentType)
                    }
                    .onFailure { e ->
                        _events.emit(CommentEvent.ShowError(e.message ?: "댓글 등록에 실패했습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(CommentEvent.ShowError("댓글 등록 중 오류가 발생했습니다"))
            } finally {
                _uiState.value = CommentUiState.Initial
            }
        }
    }

    fun observeComments(parentId: String, parentType: CommentParentType) {
        currentParentId = parentId
        currentParentType = parentType

        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = CommentUiState.Loading
                commentRepository.observeComments(parentId, parentType)
                    .collect { comments ->
                        _uiState.value = CommentUiState.Success(comments)
                    }
            } catch (e: Exception) {
                _events.emit(CommentEvent.ShowError("댓글을 불러오는 중 오류가 발생했습니다"))
                _uiState.value = CommentUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
            }
        }
    }

    fun reportComment(commentId: String, parentId: String, parentType: CommentParentType) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(CommentEvent.ShowError("로그인이 필요합니다"))

                commentRepository.getComment(commentId)
                    .onSuccess { comment ->
                        if (comment.authorId == currentUser.id) {
                            _events.emit(CommentEvent.ShowError("자신의 댓글은 신고할 수 없습니다"))
                            return@launch
                        }

                        commentRepository.reportComment(commentId, currentUser.id)
                            .onSuccess {
                                _events.emit(CommentEvent.CommentReported)
                                _events.emit(CommentEvent.ShowSnackbar("신고가 접수되었습니다"))
                                refreshComments(parentId, parentType)
                            }
                            .onFailure { e ->
                                _events.emit(CommentEvent.ShowError(e.message ?: "신고 접수에 실패했습니다"))
                            }
                    }
                    .onFailure { e ->
                        _events.emit(CommentEvent.ShowError(e.message ?: "댓글을 찾을 수 없습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(CommentEvent.ShowError("신고 처리 중 오류가 발생했습니다"))
            }
        }
    }

    fun deleteComment(commentId: String, parentId: String, parentType: CommentParentType) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(CommentEvent.ShowError("로그인이 필요합니다"))

                commentRepository.getComment(commentId)
                    .onSuccess { comment ->
                        if (comment.authorId != currentUser.id) {
                            _events.emit(CommentEvent.ShowError("본인의 댓글만 삭제할 수 있습니다"))
                            return@launch
                        }

                        commentRepository.deleteComment(commentId)
                            .onSuccess {
                                _events.emit(CommentEvent.CommentDeleted)
                                _events.emit(CommentEvent.ShowSnackbar("댓글이 삭제되었습니다"))
                                refreshComments(parentId, parentType)
                            }
                            .onFailure { e ->
                                _events.emit(CommentEvent.ShowError(e.message ?: "댓글 삭제에 실패했습니다"))
                            }
                    }
                    .onFailure { e ->
                        _events.emit(CommentEvent.ShowError(e.message ?: "댓글을 찾을 수 없습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(CommentEvent.ShowError("댓글 삭제 중 오류가 발생했습니다"))
            }
        }
    }

    private fun refreshComments(parentId: String, parentType: CommentParentType) {
        observeComments(parentId, parentType)
    }

    // 작성중인 댓글 내용을 저장하는 상태
    private val _commentContent = MutableStateFlow("")
    val commentContent: StateFlow<String> = _commentContent.asStateFlow()

    fun updateCommentContent(content: String) {
        viewModelScope.launch {
            _commentContent.value = content
        }
    }

    fun clearCommentContent() {
        viewModelScope.launch {
            _commentContent.value = ""
        }
    }
}

sealed class CommentUiState {
    data object Initial : CommentUiState()
    data object Loading : CommentUiState()
    data class Success(val comments: List<Comment>) : CommentUiState()
    data class Error(val message: String) : CommentUiState()
}

sealed class CommentEvent {
    data class ShowError(val message: String) : CommentEvent()
    data class ShowSnackbar(val message: String) : CommentEvent()
    data object CommentCreated : CommentEvent()
    data object CommentDeleted : CommentEvent()
    data object CommentReported : CommentEvent()
}