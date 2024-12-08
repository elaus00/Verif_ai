package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.ConversationContent
import mp.verif_ai.presentation.screens.conversation.components.ErrorContent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationEvent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationUiState
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var userInput by remember { mutableStateOf("") }

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
                    TODO()
                }
                is ConversationEvent.RequestExpertReviewSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = "전문가 검증 요청이 완료되었습니다",
                        actionLabel = "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }


                is ConversationEvent.MessageSent -> {
                    // 메시지 전송 성공 시 처리
                    // 예: 스크롤 위치 조정, 사운드 재생 등
//                    snackbarHostState.showSnackbar(
//                        message = "메시지가 전송되었습니다",
//                        duration = SnackbarDuration.Short
//                    )
                }
                is ConversationEvent.AiResponseReceived -> {
                    // AI 응답 수신 완료 시 처리
                    // 예: 로딩 인디케이터 숨기기, 스크롤 위치 조정 등
//                    snackbarHostState.showSnackbar(
//                        message = "AI 응답이 완료되었습니다",
//                        duration = SnackbarDuration.Short
//                    )
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
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp),
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
                            onAddClick = { /* 첨부 */ },
                            models = state.aiModels,
                            selectedModel = state.selectedModel,
                            onModelSelect = viewModel::selectAiModel
                        )
                    }
                    else -> {TODO()}
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
                        canRequestExpertReview = state.canRequestExpertReview,
                        pointBalance = state.pointBalance,
                        onRequestExpertReview = viewModel::requestExpertReview,
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