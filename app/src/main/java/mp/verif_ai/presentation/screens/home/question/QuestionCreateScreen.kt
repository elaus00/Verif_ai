package mp.verif_ai.presentation.screens.question

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.verif_ai.presentation.viewmodel.QuestionCreateUiState
import mp.verif_ai.presentation.viewmodel.QuestionCreateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionCreateScreen(
    onQuestionCreated: (String) -> Unit,
    viewModel: QuestionCreateViewModel = viewModel()
) {
    var showDiscardDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState() // StateFlow를 State로 변환

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("질문하기") },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveAsDraft() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "임시저장"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        QuestionCreateContent(
            uiState = uiState, // 변환된 State 값을 전달
            onTitleChange = viewModel::updateTitle,
            onContentChange = viewModel::updateContent,
            onSubmit = {
                viewModel.submitQuestion { questionId ->
                    onQuestionCreated(questionId)
                }
            },
            modifier = Modifier.padding(paddingValues)
        )

        if (showDiscardDialog) {
            DiscardChangesDialog(
                onDismiss = { showDiscardDialog = false },
                onConfirm = { /* TODO: Navigate back */ }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestionCreateContent(
    uiState: QuestionCreateUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text("제목") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.titleError != null,
            supportingText = uiState.titleError?.let { { Text(it) } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.content,
            onValueChange = onContentChange,
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            isError = uiState.contentError != null,
            supportingText = uiState.contentError?.let { { Text(it) } }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // AI 추천 태그 섹션
        if (uiState.suggestedTags.isNotEmpty()) {
            Text(
                text = "추천 태그",
                style = MaterialTheme.typography.titleSmall
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.suggestedTags.forEach { tag ->
                    SuggestionChip(
                        onClick = { /* TODO: Add tag */ },
                        label = { Text(tag) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isValid
        ) {
            Text("질문하기")
        }
    }

    if (uiState.isLoading) {
        LoadingOverlay()
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DiscardChangesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("작성을 취소하시겠습니까?") },
        text = { Text("작성 중인 내용은 저장되지 않습니다.") },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}