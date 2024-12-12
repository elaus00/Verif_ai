package mp.verif_ai.presentation.screens.question.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.question.QuestionUiState
import mp.verif_ai.presentation.screens.question.QuestionViewModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun QuestionContent(
    viewModel: QuestionViewModel = hiltViewModel(),
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState().value

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is QuestionUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is QuestionUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 트렌딩 질문 섹션
                    if (uiState.trendingQuestions.isNotEmpty()) {
                        item {
                            Text(
                                text = "트렌딩 질문",
                                style = MaterialTheme.typography.titleLarge,
                                color = VerifAiColor.TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(uiState.trendingQuestions) { question ->
                            QuestionCard(
                                title = question.title,
                                status = getQuestionStatusText(question.status),
                                date = DateUtils.formatShortDate(question.createdAt),
                                viewCount = question.viewCount,
                                onClick = { onQuestionClick(question.id) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }

                    // 내 질문 섹션
                    if (uiState.myQuestions.isNotEmpty()) {
                        item {
                            Text(
                                text = "내 질문",
                                style = MaterialTheme.typography.titleLarge,
                                color = VerifAiColor.TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(uiState.myQuestions) { question ->
                            QuestionCard(
                                title = question.title,
                                status = getQuestionStatusText(question.status),
                                date = DateUtils.formatShortDate(question.createdAt),
                                viewCount = question.viewCount,
                                onClick = { onQuestionClick(question.id) }
                            )
                        }
                    }

                    // 데이터가 없는 경우
                    if (uiState.trendingQuestions.isEmpty() && uiState.myQuestions.isEmpty()) {
                        item {
                            EmptyContent(
                                message = "아직 질문이 없습니다\n첫 질문을 작성해보세요!",
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }

            is QuestionUiState.Error -> {
                ErrorContent(
                    message = uiState.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            else -> {
                // Initial state
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun EmptyContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QuestionAnswer,
            contentDescription = null,
            tint = VerifAiColor.TextSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = VerifAiColor.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

private fun getQuestionStatusText(status: QuestionStatus): String {
    return when (status) {
        QuestionStatus.OPEN -> "답변 대기"
        QuestionStatus.CLOSED -> "답변 완료"
        QuestionStatus.IN_PROGRESS -> "답변 중"
        QuestionStatus.EXPIRED -> "기간 만료"
        QuestionStatus.DELETED -> "삭제됨"
    }
}