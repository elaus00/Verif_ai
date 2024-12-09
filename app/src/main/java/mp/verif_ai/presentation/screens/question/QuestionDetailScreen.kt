package mp.verif_ai.presentation.screens.question

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.question.components.QuestionDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    questionId: String,
    viewModel: QuestionViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(questionId) {
        viewModel.getQuestionById(questionId)
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
        }
    ) { padding ->
        when (uiState) {
            is QuestionUiState.Success -> {
                val question = (uiState as QuestionUiState.Success).question
                if (question != null) {
                    QuestionDetailContent(
                        question = question,
                        onAnswerClick = { answerId ->
                            navController.navigate("answer/$answerId")
                        },
                        onCommentClick = { /* TODO: 댓글 기능 구현 */ },
                        onLikeClick = { /* TODO: 좋아요 기능 구현 */ },
                        onReportClick = { /* TODO: 신고 기능 구현 */ },
                        modifier = Modifier.padding(padding)
                    )
                }
            }
            is QuestionUiState.Error -> {
                LaunchedEffect(Unit) {
                    snackbarHostState.showSnackbar(
                        message = (uiState as QuestionUiState.Error).message,
                        actionLabel = "Dismiss"
                    )
                }
            }
            is QuestionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {}
        }
    }
}