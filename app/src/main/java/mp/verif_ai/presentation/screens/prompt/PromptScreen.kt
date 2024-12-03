package mp.verif_ai.presentation.screens.prompt

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.presentation.viewmodel.prompt.PromptViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.prompt.components.PromptBottomBar
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import mp.verif_ai.presentation.viewmodel.prompt.VoiceRecognitionEvent
import mp.verif_ai.presentation.viewmodel.prompt.VoiceRecognitionViewModel
import mp.verif_ai.presentation.viewmodel.state.ChatUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptScreen(
    viewModel: PromptViewModel = hiltViewModel(),
    voiceViewModel: VoiceRecognitionViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val voiceState by voiceViewModel.state.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // 음성 인식 결과 처리
    val voiceRecognizerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        voiceViewModel.handleVoiceRecognitionResult(result)
    }

    // 권한 처리
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceRecognizerLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요...")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            })
        }
    }

    // 이벤트 처리
    LaunchedEffect(Unit) {
        voiceViewModel.events.collect { event ->
            when (event) {
                is VoiceRecognitionEvent.RecognitionSuccess -> {
                    userInput = event.text
                }
                is VoiceRecognitionEvent.RecognitionError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
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
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = VerifAiColor.TextPrimary
                        )
                    }
                },
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Verif AI",
                            color = VerifAiColor.TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = VerifAiColor.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = VerifAiColor.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            PromptBottomBar(
                userInput = userInput,
                onUserInputChange = { userInput = it },
                onSendMessage = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendPrompt(userInput)
                        userInput = ""
                    }
                },
                onVoiceRecognition = {
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                },
                onAddClick = { /* TODO */ }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = 8.dp),
            reverseLayout = true,
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages.reversed()) { message ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = when (message) {
                        is ChatUiState.UserMessage -> Alignment.CenterEnd
                        else -> Alignment.CenterStart
                    }
                ) {
                    Surface(
                        color = when (message) {
                            is ChatUiState.UserMessage -> VerifAiColor.Primary.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            when (message) {
                                is ChatUiState.UserMessage -> VerifAiColor.Primary.copy(alpha = 0.2f)
                                else -> VerifAiColor.DividerColor
                            }
                        )
                    ) {
                        Text(
                            when (message) {
                                is ChatUiState.UserMessage -> message.content
                                is ChatUiState.AssistantMessage -> message.content
                                is ChatUiState.ErrorMessage -> message.message
                            },
                            modifier = Modifier.padding(12.dp),
                            color = VerifAiColor.TextPrimary
                        )
                    }
//                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
private fun WelcomeMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Welcome to Verif AI Chat",
            style = MaterialTheme.typography.titleLarge,
            color = VerifAiColor.TextPrimary
        )
        Text(
            text = "Ask me anything about your AI-related questions",
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}