package mp.verif_ai.presentation.screens.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.question.Comment
import mp.verif_ai.domain.model.question.CommentParentType
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.CommentRepository
import mp.verif_ai.domain.usecase.question.CreateQuestionUseCase
import mp.verif_ai.domain.usecase.question.GetMyQuestionsUseCase
import mp.verif_ai.domain.usecase.question.GetQuestionUseCase
import mp.verif_ai.domain.usecase.question.GetTrendingQuestionsUseCase
import mp.verif_ai.domain.usecase.question.UpdateQuestionUseCase
import mp.verif_ai.domain.util.NotificationManager
import mp.verif_ai.presentation.screens.question.components.ReportReason
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val createQuestionUseCase: CreateQuestionUseCase,
    private val getQuestionUseCase: GetQuestionUseCase,
    private val getTrendingQuestionsUseCase: GetTrendingQuestionsUseCase,
    private val getMyQuestionsUseCase: GetMyQuestionsUseCase,
    private val updateQuestionUseCase: UpdateQuestionUseCase,
    private val commentRepository: CommentRepository,
    private val authRepository: AuthRepository,
    private val notificationManager: NotificationManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionUiState>(QuestionUiState.Initial)
    val uiState: StateFlow<QuestionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<QuestionEvent>()
    val events = _events.asSharedFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _likes = MutableStateFlow<Set<String>>(emptySet())
    val likes: StateFlow<Set<String>> = _likes.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _commentContent = MutableStateFlow("")
    val commentContent: StateFlow<String> = _commentContent.asStateFlow()

    val currentUserId = authRepository.getCurrentUser()?.id


    fun createQuestion(question: Question) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = QuestionUiState.Loading
            try {
                createQuestionUseCase(question)
                    .onSuccess { questionId ->
                        _events.emit(QuestionEvent.QuestionCreated(questionId))
                        notificationManager.showNotification(
                            title = "질문이 등록되었습니다",
                            content = question.title,
                            questionId = questionId
                        )
                        _uiState.value = QuestionUiState.Success(
                            question = question,
                        )
                    }
                    .onFailure { e ->
                        _events.emit(QuestionEvent.ShowError(e.message ?: "질문 등록에 실패했습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("질문 등록 중 오류가 발생했습니다"))
            } finally {
                _uiState.value = QuestionUiState.Initial
            }
        }
    }

    fun getQuestionById(questionId: String) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = QuestionUiState.Loading
            try {
                getQuestionUseCase(questionId)
                    .onSuccess { question ->
                        _uiState.value = QuestionUiState.Success(
                            question = question,
                            trendingQuestions = emptyList(),
                            myQuestions = emptyList()
                        )
                    }
                    .onFailure { e ->
                        _events.emit(QuestionEvent.ShowError(e.message ?: "질문을 불러올 수 없습니다"))
                        _uiState.value = QuestionUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("질문을 불러오는 중 오류가 발생했습니다"))
                _uiState.value = QuestionUiState.Error("알 수 없는 오류가 발생했습니다")
            }
        }
    }

    fun likeQuestion(questionId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentLikes = _likes.value
                if (questionId in currentLikes) {
                    _likes.value = currentLikes - questionId
                } else {
                    _likes.value = currentLikes + questionId
                    notificationManager.showNotification(
                        title = "새로운 좋아요",
                        content = "회원님의 질문에 좋아요가 추가되었습니다",
                        questionId = questionId
                    )
                }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("좋아요 처리 중 오류가 발생했습니다"))
            }
        }
    }

    fun addComment(questionId: String, comment: String) {
        viewModelScope.launch(dispatcher) {
            try {
                // TODO: 실제 댓글 추가 로직 구현
                val newComment = Comment(
                    content = comment,
                    parentId = questionId,
                    parentType = CommentParentType.QUESTION
                )
                _comments.value = _comments.value + newComment
                notificationManager.showNotification(
                    title = "새로운 댓글",
                    content = "회원님의 질문에 새로운 댓글이 달렸습니다",
                    questionId = questionId
                )
                _events.emit(QuestionEvent.ShowSnackbar("댓글이 추가되었습니다"))
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("댓글 추가 중 오류가 발생했습니다"))
            }
        }
    }

    fun reportQuestion(questionId: String, reason: ReportReason, additionalComment: String) {
        viewModelScope.launch(dispatcher) {
            try {
                // TODO: 실제 신고 처리 로직 구현
                _events.emit(QuestionEvent.ShowSnackbar("신고가 접수되었습니다"))
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("신고 처리 중 오류가 발생했습니다"))
            }
        }
    }

    fun getTrendingQuestions(limit: Int = 5) {
        viewModelScope.launch(dispatcher) {
            try {
                getTrendingQuestionsUseCase(limit).collect { questions ->
                    _uiState.update { currentState ->
                        when (currentState) {
                            is QuestionUiState.Success -> currentState.copy(
                                trendingQuestions = questions
                            )
                            else -> QuestionUiState.Success(
                                question = null,
                                trendingQuestions = questions,
                                myQuestions = emptyList()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("트렌딩 질문을 불러오는 중 오류가 발생했습니다"))
            }
        }
    }

    fun getMyQuestions(userId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                getMyQuestionsUseCase(userId).collect { questions ->
                    _uiState.update { currentState ->
                        when (currentState) {
                            is QuestionUiState.Success -> currentState.copy(
                                myQuestions = questions
                            )
                            else -> QuestionUiState.Success(
                                question = null,
                                trendingQuestions = emptyList(),
                                myQuestions = questions
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("내 질문을 불러오는 중 오류가 발생했습니다"))
            }
        }
    }

    fun updateQuestion(question: Question) {
        viewModelScope.launch(dispatcher) {
            try {
                updateQuestionUseCase(question)
                    .onSuccess {
                        _events.emit(QuestionEvent.QuestionUpdated)
                        _events.emit(QuestionEvent.ShowSnackbar("질문이 수정되었습니다"))
                    }
                    .onFailure { e ->
                        _events.emit(QuestionEvent.ShowError(e.message ?: "질문 수정에 실패했습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("질문 수정 중 오류가 발생했습니다"))
            }
        }
    }

    fun onCategorySelected(category: String) {
        viewModelScope.launch {
            _selectedCategory.value = if (_selectedCategory.value == category) null else category
            refreshQuestions() // 카테고리가 변경될 때 질문 목록을 새로고침
        }
    }

    fun refreshQuestions() {
        viewModelScope.launch {
            _uiState.value = QuestionUiState.Loading
            try {
                // 현재 선택된 카테고리에 따라 필터링된 질문을 가져옴
                val currentCategory = _selectedCategory.value
                getTrendingQuestionsUseCase(5).collect { questions ->
                    val filteredQuestions = if (currentCategory != null) {
                        questions.filter { it.category == currentCategory }
                    } else {
                        questions
                    }
                    _uiState.value = QuestionUiState.Success(
                        question = null,
                        trendingQuestions = filteredQuestions,
                        myQuestions = (_uiState.value as? QuestionUiState.Success)?.myQuestions ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("질문을 불러오는 중 오류가 발생했습니다"))
                _uiState.value = QuestionUiState.Error("알 수 없는 오류가 발생했습니다")
            }
        }
    }

    fun adoptAnswer(questionId: String, answerId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentQuestion = (uiState.value as? QuestionUiState.Success)?.question
                if (currentQuestion != null) {
                    val updatedQuestion = currentQuestion.copy(
                        selectedAnswerId = answerId,
                        status = QuestionStatus.CLOSED
                    )
                    updateQuestionUseCase(updatedQuestion)
                        .onSuccess {
                            _events.emit(QuestionEvent.ShowSnackbar("답변이 채택되었습니다"))
                            notificationManager.showNotification(
                                title = "답변 채택",
                                content = "회원님의 답변이 채택되었습니다",
                                questionId = questionId
                            )
                        }
                        .onFailure { e ->
                            _events.emit(QuestionEvent.ShowError(e.message ?: "답변 채택에 실패했습니다"))
                        }
                }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("답변 채택 중 오류가 발생했습니다"))
            }
        }
    }

    fun createComment(parentId: String, parentType: CommentParentType, content: String) {
        if (content.isBlank()) {
            viewModelScope.launch {
                _events.emit(QuestionEvent.ShowError("댓글 내용을 입력해주세요"))
            }
            return
        }

        viewModelScope.launch(dispatcher) {
            try {
                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(QuestionEvent.ShowError("로그인이 필요합니다"))

                val comment = Comment(
                    content = content,
                    authorId = currentUser.id,
                    authorName = currentUser.nickname,
                    parentId = parentId,
                    parentType = parentType
                )

                commentRepository.createComment(comment)
                    .onSuccess {
                        _events.emit(QuestionEvent.ShowSnackbar("댓글이 등록되었습니다"))
                        _commentContent.value = "" // 입력 필드 초기화
                        commentRepository.updateComment(comment)
                        getQuestionById(parentId) // 질문 데이터 새로고침
                    }
                    .onFailure { e ->
                        _events.emit(QuestionEvent.ShowError(e.message ?: "댓글 등록에 실패했습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("댓글 등록 중 오류가 발생했습니다"))
            }
        }
    }

    fun deleteComment(commentId: String, parentId: String, parentType: CommentParentType) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(QuestionEvent.ShowError("로그인이 필요합니다"))

                commentRepository.getComment(commentId)
                    .onSuccess { comment ->
                        if (comment.authorId != currentUser.id) {
                            _events.emit(QuestionEvent.ShowError("본인의 댓글만 삭제할 수 있습니다"))
                            return@launch
                        }

                        commentRepository.deleteComment(commentId)
                            .onSuccess {
                                _events.emit(QuestionEvent.ShowSnackbar("댓글이 삭제되었습니다"))
                                getQuestionById(parentId) // 질문 데이터 새로고침
                            }
                            .onFailure { e ->
                                _events.emit(QuestionEvent.ShowError(e.message ?: "댓글 삭제에 실패했습니다"))
                            }
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("댓글 삭제 중 오류가 발생했습니다"))
            }
        }
    }

    fun reportComment(commentId: String, parentId: String, parentType: CommentParentType) {
        viewModelScope.launch(dispatcher) {
            try {
                val currentUser = authRepository.getCurrentUser()
                    ?: return@launch _events.emit(QuestionEvent.ShowError("로그인이 필요합니다"))

                commentRepository.reportComment(commentId, currentUser.id)
                    .onSuccess {
                        _events.emit(QuestionEvent.ShowSnackbar("신고가 접수되었습니다"))
                        getQuestionById(parentId) // 질문 데이터 새로고침
                    }
                    .onFailure { e ->
                        _events.emit(QuestionEvent.ShowError(e.message ?: "신고 접수에 실패했습니다"))
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("신고 처리 중 오류가 발생했습니다"))
            }
        }
    }

    fun observeComments(parentId: String, parentType: CommentParentType) {
        viewModelScope.launch(dispatcher) {
            try {
                commentRepository.observeComments(parentId, parentType)
                    .collect { commentsList ->
                        _uiState.update { currentState ->
                            when (currentState) {
                                is QuestionUiState.Success -> {
                                    currentState.copy(
                                        question = currentState.question?.copy(
                                            comments = commentsList
                                        )
                                    )
                                }
                                else -> currentState
                            }
                        }
                    }
            } catch (e: Exception) {
                _events.emit(QuestionEvent.ShowError("댓글을 불러오는 중 오류가 발생했어용"))
            }
        }
    }



    fun updateCommentContent(content: String) {
        viewModelScope.launch {
            _commentContent.value = content
        }
    }
}