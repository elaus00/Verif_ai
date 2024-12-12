
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mp.verif_ai.domain.model.question.CommentParentType
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.question.QuestionEvent
import mp.verif_ai.presentation.screens.question.QuestionUiState
import mp.verif_ai.presentation.screens.question.QuestionViewModel
import mp.verif_ai.presentation.screens.question.components.QuestionDetailContent
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    questionId: String,
    viewModel: QuestionViewModel = hiltViewModel(),
    navController: NavController,
    onAnswerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var commentInputText by remember { mutableStateOf("") }


    LaunchedEffect(questionId) {
        viewModel.getQuestionById(questionId)

        // 댓글 목록 가져오기
        viewModel.observeComments(questionId, CommentParentType.QUESTION)

        // 이벤트 구독
        viewModel.events.collect { event ->
            when (event) {
                is QuestionEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "확인"
                    )
                }
                is QuestionEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "확인"
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(snackbarData = data)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("질문 상세") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { padding ->
        when (uiState) {
            is QuestionUiState.Success -> {
                val question = (uiState as QuestionUiState.Success).question
                if (question != null) {
                    QuestionDetailContent(
                        question = question,
                        onAnswerClick = onAnswerClick,
                        onCommentSubmit = { content ->
                            viewModel.createComment(
                                parentId = questionId,
                                parentType = CommentParentType.QUESTION,
                                content = content
                            )
                            commentInputText = ""  // 입력 후 초기화
                        },
                        onCommentDelete = { commentId ->
                            viewModel.deleteComment(
                                commentId = commentId,
                                parentId = questionId,
                                parentType = CommentParentType.QUESTION
                            )
                        },
                        onCommentReport = { commentId ->
                            viewModel.reportComment(
                                commentId = commentId,
                                parentId = questionId,
                                parentType = CommentParentType.QUESTION
                            )
                        },
                        onLikeClick = { viewModel.likeQuestion(questionId) },
                        onReportClick = {
                            viewModel.reportQuestion(questionId, it.first, it.second)
                        },
                        commentContent = commentInputText,
                        onCommentContentChange = { commentInputText = it },
                        modifier = Modifier.padding(padding)
                    )
                }
            }

            is QuestionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = (uiState as QuestionUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.getQuestionById(questionId) }
                        ) {
                            Text("다시 시도")
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(
                        message = (uiState as QuestionUiState.Error).message,
                        actionLabel = "확인"
                    )
                }
            }
            QuestionUiState.Initial -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    // 초기 상태에서는 로딩을 시작하도록
                    LaunchedEffect(Unit) {
                        viewModel.getQuestionById(questionId)
                    }
                }
            }
            QuestionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "질문을 불러오는 중입니다...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = VerifAiColor.TextSecondary
                        )
                    }
                }
            }
        }
    }
}