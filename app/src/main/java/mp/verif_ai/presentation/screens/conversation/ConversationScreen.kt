package mp.verif_ai.presentation.screens.conversation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.conversation.components.ConversationDetailPage
import mp.verif_ai.presentation.screens.conversation.components.ConversationDetailTopBar
import mp.verif_ai.presentation.screens.conversation.components.ConversationHistoryPage
import mp.verif_ai.presentation.screens.conversation.components.ConversationHistoryTopBar
import mp.verif_ai.presentation.screens.conversation.components.ConversationPagerContainer
import mp.verif_ai.presentation.screens.conversation.components.ConversationSearchBar
import mp.verif_ai.presentation.screens.conversation.components.PagerContent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationEvent
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel(),
    onNavigateToExpertProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 2 }
    )

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
                else -> { /* 다른 이벤트 처리 */ }
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
            AnimatedContent(
                targetState = pagerState.currentPage,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith
                            fadeOut() + slideOutVertically()
                }, label = ""
            ) { page ->
                when (page) {
                    0 -> Column {
                        ConversationHistoryTopBar()
                        ConversationSearchBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = {
                                searchQuery = it
                                viewModel.searchConversations(it)
                            }
                        )
                    }
                    1 -> ConversationDetailTopBar(
                        onBackClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ConversationPagerContainer(
                pagerState = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PagerContent(
                    pagerState = pagerState,
                    pageIndex = page
                ) {
                    when (page) {
                        0 -> ConversationHistoryPage(
                            uiState = uiState,
                            searchQuery = searchQuery,
                            onConversationClick = { conversationId ->
                                viewModel.loadConversation(conversationId)
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        )
                        1 -> ConversationDetailPage(
                            uiState = uiState,
                            onSendMessage = viewModel::sendMessage,
                            onRequestExpertReview = viewModel::requestExpertReview,
                            onModelSelect = viewModel::selectAiModel
                        )
                    }
                }
            }
        }
    }
}
