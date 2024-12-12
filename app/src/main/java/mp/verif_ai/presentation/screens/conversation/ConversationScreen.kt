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
import androidx.navigation.NavHostController
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
    navController : NavHostController,
    viewModel: ConversationViewModel = hiltViewModel(),
    onNavigateToExpertProfile: (String) -> Unit,
    onNavigateToPointCharge: () -> Unit = {}
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
                        actionLabel = "Close",
                        duration = SnackbarDuration.Long
                    )
                }
                is ConversationEvent.InsufficientPoints -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Lack of Point",
                        actionLabel = "Charge",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigateToPointCharge()
                    }
                }
                is ConversationEvent.NavigateToExpertProfile -> {
                    onNavigateToExpertProfile(event.expertId)
                }
                is ConversationEvent.RequestExpertReviewSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = "Expert review completed",
                        actionLabel = "Close",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> Unit
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
                },
                label = "TopBar Animation"
            ) { page ->
                when (page) {
                    0 -> Column {
                        ConversationHistoryTopBar(
                            onBackClick = { navController.navigateUp() },
                            onRefresh = { viewModel.loadConversationHistory() }
                        )
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
                        },
                        currentConversation = viewModel.getCurrentConversation(),
                        onNewConversationClick = {
                            viewModel.startNewConversation()
                            scope.launch {
                                pagerState.animateScrollToPage(1)
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
                                scope.launch {
                                    viewModel.loadConversation()
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        )
                        1 -> ConversationDetailPage(
                            uiState = uiState,
                            onSendMessage = viewModel::sendMessage,
                            onRequestExpertReview = viewModel::requestExpertReview,
                            onModelSelect = viewModel::selectAiModel,
                            onRetry = viewModel::retry
                        )
                    }
                }
            }
        }
    }
}