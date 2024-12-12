package mp.verif_ai.presentation.screens.question.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.question.QuestionUiState
import mp.verif_ai.presentation.screens.question.QuestionViewModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun ExploreQuestionList(
    viewModel: QuestionViewModel = hiltViewModel(),
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState().value

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is QuestionUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VerifAiColor.Navy.Deep
                )
            }

            is QuestionUiState.Success -> {
                Column {
                    // 질문 섹션
                    if (uiState.trendingQuestions.isNotEmpty()) {
                        TrendingQuestionSection(
                            title = "Trending Questions",
                            trendingQuestions = uiState.trendingQuestions,
                            onQuestionClick = onQuestionClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 데이터가 없는 경우
                    if (uiState.trendingQuestions.isEmpty() && uiState.myQuestions.isEmpty()) {
                        EmptyContent(
                            message = "No questions yet.\nBe the first to ask!",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
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

            else -> Unit
        }
    }
}

@Composable
fun TrendingQuestionSection(
    title: String,
    trendingQuestions: List<TrendingQuestion>,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
//        Text(
//            text = title,
//            style = MaterialTheme.typography.titleLarge,
//            color = VerifAiColor.TextPrimary,
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//        )

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = trendingQuestions,
                key = { it.id }
            ) { question ->
                QuestionCard(
                    title = question.title,
                    status = question.status,
                    category = question.category,
                    tags = question.tags,
                    authorName = question.authorName,
                    points = question.points,
                    answerCount = question.commentCount,
                    viewCount = question.viewCount,
                    date = DateUtils.formatShortDate(question.createdAt),
                    onClick = { onQuestionClick(question.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
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
