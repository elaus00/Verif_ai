package mp.verif_ai.presentation.screens.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mp.verif_ai.domain.model.expert.ExpertFields
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.question.components.QuestionContent
import mp.verif_ai.presentation.screens.question.components.TagSelection
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getTrendingQuestions()
        // 이벤트 수집
        viewModel.events.collect { event ->
            when (event) {
                is QuestionEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
                is QuestionEvent.QuestionCreated -> {
                    navController.navigate("question/${event.questionId}")
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
                title = { Text("질문하기") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("question/create") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Question")
            }
        }
    ) { padding ->
        QuestionContent(
            onQuestionClick = { questionId ->
                navController.navigate("question/$questionId")
            },
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionScreen(
    viewModel: QuestionViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(emptyList<String>()) }
    var showGuide by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is QuestionEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss"
                    )
                }
                is QuestionEvent.QuestionCreated -> {
                    navController.navigate(Screen.MainNav.Explore.Question.Detail.createRoute(event.questionId))
                    {
                        popUpTo("question/create") { inclusive = true }
                    }
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
                title = {
                    Text(
                        text = "새 질문 작성",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = VerifAiColor.TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = VerifAiColor.TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.createQuestion(
                                Question(
                                    title = title,
                                    content = content,
                                    tags = selectedTags
                                )
                            )
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text(
                            text = "등록",
                            color = if (title.isNotBlank() && content.isNotBlank())
                                VerifAiColor.Navy.Deep
                            else
                                VerifAiColor.TextTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 작성 가이드
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
                                "• 질문의 배경과 목적을 설명해주세요\n" +
                                "• 관련 태그를 선택하여 전문가의 답변을 받아보세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VerifAiColor.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 제목 입력
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        text = "제목",
                        color = VerifAiColor.TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerifAiColor.Navy.Deep,
                    unfocusedBorderColor = VerifAiColor.BorderColor
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 내용 입력
            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    if (showGuide && content.isNotEmpty()) {
                        showGuide = false
                    }
                },
                label = {
                    Text(
                        text = "내용",
                        color = VerifAiColor.TextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerifAiColor.Navy.Deep,
                    unfocusedBorderColor = VerifAiColor.BorderColor
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TagSelection(
                selectedTags = selectedTags,
                onTagSelected = { tag ->
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSelection(
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExpertFields.getAllFields().forEach { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTagSelected(tag) },
                label = { Text(tag) },
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