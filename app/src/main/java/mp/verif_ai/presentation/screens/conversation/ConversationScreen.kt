package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.ConversationContent
import mp.verif_ai.presentation.screens.conversation.components.ErrorContent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationEvent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationUiState
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationViewModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    onNavigateToExpertProfile: (String) -> Unit,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var userInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ConversationEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Long
                    )
                }
                is ConversationEvent.InsufficientPoints -> {
                    snackbarHostState.showSnackbar(
                        message = "포인트가 부족합니다",
                        actionLabel = "충전하기",
                        duration = SnackbarDuration.Long
                    )
                }
                is ConversationEvent.NavigateToExpertProfile -> {
                    onNavigateToExpertProfile(event.expertId)
                }
                is ConversationEvent.RequestExpertReviewSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = "전문가 검증 요청이 완료되었습니다",
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
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
                    Text("Conversation")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                when (val state = uiState) {
                    is ConversationUiState.Success -> {
                        // AI 모델 선택기 (필요한 경우)
                        if (state.selectedModel != null) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = VerifAiColor.SurfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "AI 모델: ${state.selectedModel.apiName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = VerifAiColor.TextSecondary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    TextButton(onClick = { /* 모델 변경 */ }) {
                                        Text("변경")
                                    }
                                }
                            }
                        }

                        // 입력바
                        ChatBottomBar(
                            userInput = userInput,
                            onUserInputChange = { userInput = it },
                            onSendMessage = {
                                if (userInput.isNotBlank()) {
                                    viewModel.sendMessage(userInput)
                                    userInput = ""
                                }
                            },
                            onVoiceRecognition = { /* 음성 인식 */ },
                            onAddClick = { /* 첨부 */ }
                        )

                        // 전문가 검증 요청 버튼
                        if (state.canRequestExpertReview) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                color = VerifAiColor.Primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "전문가 검증 요청",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "필요 포인트: ${Adoption.EXPERT_REVIEW_POINTS}P",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = VerifAiColor.TextSecondary
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.requestExpertReview() },
                                        enabled = state.pointBalance >= Adoption.EXPERT_REVIEW_POINTS
                                    ) {
                                        Text("요청하기")
                                    }
                                }
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is ConversationUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ConversationUiState.Success -> {
                    ConversationContent(
                        messages = state.messages,
                        expertReviews = state.expertReviews,
                        onExpertProfileClick = onNavigateToExpertProfile
                    )
                }
                is ConversationUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = viewModel::retry
                    )
                }
            }
        }
    }
}
