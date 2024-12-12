package mp.verif_ai.presentation.screens.answer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.theme.VerifAiColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnswerScreen(
    questionId: String,
    viewModel: AnswerViewModel = hiltViewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is AnswerEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss"
                    )
                }
                is AnswerEvent.AnswerCreated -> {
                    navController.navigate(
                        Screen.MainNav.Explore.Question.Detail.createRoute(questionId)
                    ) {
                        popUpTo(Screen.MainNav.Explore.Question.CreateAnswer.route) { inclusive = true }
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
                        text = "Write Reply",
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
                            // TODO: 전문가 검증 로직 추가
                            viewModel.createAnswer(
                                Answer(
                                    questionId = questionId,
                                    content = content,
                                    isExpertAnswer = true  // 임시로 true 설정
                                )
                            )
                        },
                        enabled = content.isNotBlank()
                    ) {
                        Text(
                            text = "등록",
                            color = if (content.isNotBlank())
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
            Spacer(modifier = Modifier.height(16.dp))

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
                        text = "답변 작성 가이드",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerifAiColor.TextSecondary
                    )
                    Text(
                        text = "• 객관적이고 전문적인 답변을 작성해주세요\n" +
                                "• 근거나 참고 자료를 함께 제시해주세요\n" +
                                "• 답변이 채택되면 포인트가 지급됩니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VerifAiColor.TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        text = "답변 내용",
                        color = VerifAiColor.TextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerifAiColor.Navy.Deep,
                    unfocusedBorderColor = VerifAiColor.BorderColor
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}