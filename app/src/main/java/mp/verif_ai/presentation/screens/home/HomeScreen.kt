package mp.verif_ai.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.verif_ai.domain.model.Question
import mp.verif_ai.presentation.viewmodel.HomeUiState
import mp.verif_ai.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onQuestionClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VerifAI") },
                actions = {
                    IconButton(onClick = { viewModel.refreshQuestions() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "새로고침"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingContent()
            is HomeUiState.Success -> QuestionList(
                questions = state.questions,
                onQuestionClick = onQuestionClick,
                modifier = Modifier.padding(paddingValues)
            )
            is HomeUiState.Error -> ErrorContent(
                message = state.message,
                onRetry = { viewModel.refreshQuestions() }
            )

            is HomeUiState.Error -> TODO()
            HomeUiState.Loading -> TODO()
            is HomeUiState.Success -> TODO()
        }
    }
}

@Composable
private fun QuestionList(
    questions: List<Question>,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(questions) { question ->
            QuestionCard(
                question = question,
                onClick = { onQuestionClick(question.id) }
            )
        }
    }
}

@Composable
private fun QuestionCard(
    question: Question,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question.author,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = question.formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}