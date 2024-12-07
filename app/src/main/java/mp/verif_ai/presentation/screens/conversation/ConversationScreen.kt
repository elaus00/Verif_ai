package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.AIModelSelector
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
                modifier = Modifier
                    .padding(top = 20.dp),
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
                        expertReviews = state.expertReviews,
                        canRequestExpertReview = state.canRequestExpertReview,
                        pointBalance = state.pointBalance,
                        onRequestExpertReview = viewModel::requestExpertReview,
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