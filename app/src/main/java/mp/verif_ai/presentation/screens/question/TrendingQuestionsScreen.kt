package mp.verif_ai.presentation.screens.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.ErrorContent
import mp.verif_ai.presentation.screens.question.components.EmptyContent
import mp.verif_ai.presentation.screens.question.components.QuestionCard
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun TrendingQuestionsScreen(
    viewModel: QuestionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onQuestionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QuestionEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss"
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
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "트렌딩 질문",
                style = MaterialTheme.typography.headlineSmall,
                color = VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is QuestionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is QuestionUiState.Success -> {
                    if ((uiState as QuestionUiState.Success)
                            .trendingQuestions.isEmpty()) {
                        EmptyContent(
                            message = "트렌딩 질문이 없습니다",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items((uiState as QuestionUiState.Success).trendingQuestions) { question ->
                                QuestionCard(
                                    title = question.title,
                                    status = question.status.toString(),
                                    date = DateUtils.formatShortDate(question.createdAt),
                                    viewCount = question.viewCount,
                                    onClick = { onQuestionClick(question.id) }
                                )
                            }
                        }
                    }
                }

                is QuestionUiState.Error -> {
                    ErrorContent(
                        message = (uiState as QuestionUiState.Error).message,
                        modifier = Modifier.fillMaxWidth(),
                        onRetry = TODO()
                    )
                }

                else -> Unit
            }
        }
    }
}