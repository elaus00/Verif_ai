package mp.verif_ai.presentation.screens.question

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.expert.ExpertFields
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.ErrorContent
import mp.verif_ai.presentation.screens.question.components.EmptyContent
import mp.verif_ai.presentation.screens.question.components.QuestionCard
import mp.verif_ai.presentation.screens.question.components.QuestionUtils.getQuestionStatusText
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun QuestionsContent(
    viewModel: QuestionViewModel = hiltViewModel(),
    onQuestionClick: (String) -> Unit,
    onCreateQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QuestionEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Long
                    )
                }
                is QuestionEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> { /* 다른 이벤트 처리 */ }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateQuestion,
                containerColor = VerifAiColor.Navy.Deep,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "새 질문 작성",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is QuestionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = VerifAiColor.Navy.Deep
                    )
                }
            }

            is QuestionUiState.Success -> {
                val successState = uiState as QuestionUiState.Success
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 작성 가이드
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = VerifAiColor.Navy.SearchBarBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "질문 작성 가이드",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = VerifAiColor.TextSecondary
                                )
                                Text(
                                    text = "• 구체적이고 명확한 질문을 작성해주세요\n" +
                                            "• 관련된 카테고리를 선택해주세요\n" +
                                            "• 필요한 경우 이미지나 파일을 첨부해주세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = VerifAiColor.TextTertiary
                                )
                            }
                        }
                    }

                    // Categories section
                    item {
                        CategorySection(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { category ->
                                viewModel.onCategorySelected(category)
                            }
                        )
                    }

                    // Trending questions section
                    item {
                        Text(
                            text = "인기 질문",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = VerifAiColor.TextPrimary
                        )
                    }

                    items(
                        items = successState.trendingQuestions,
                        key = { it.id }
                    ) { question ->
                        QuestionCard(
                            title = question.title,
                            status = getQuestionStatusText(question.status),
                            date = DateUtils.formatShortDate(question.createdAt),
                            viewCount = question.viewCount,
                            onClick = { onQuestionClick(question.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    // Recent questions section
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "최근 질문",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = VerifAiColor.TextPrimary
                        )
                    }

                    items(
                        items = successState.myQuestions,
                        key = { it.id }
                    ) { question ->
                        QuestionCard(
                            title = question.title,
                            status = getQuestionStatusText(question.status),
                            date = DateUtils.formatShortDate(question.createdAt),
                            viewCount = question.viewCount,
                            onClick = { onQuestionClick(question.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    // Empty state handling
                    if (successState.trendingQuestions.isEmpty() && successState.myQuestions.isEmpty()) {
                        item {
                            EmptyContent(
                                message = "아직 등록된 질문이 없습니다.\n첫 질문을 작성해보세요!",
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
                    message = (uiState as QuestionUiState.Error).message,
                    onRetry = { viewModel.refreshQuestions() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun CategorySection(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = ExpertFields.getAllFields()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = VerifAiColor.Navy.Light,
                    selectedLabelColor = Color.White,
                    containerColor = VerifAiColor.Navy.SearchBarBg,
                    labelColor = VerifAiColor.TextSecondary
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}