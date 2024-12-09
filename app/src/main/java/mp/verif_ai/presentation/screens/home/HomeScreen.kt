package mp.verif_ai.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.home.components.RecentConversationsCard
import mp.verif_ai.presentation.screens.home.components.TrendingQuestionsCard
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val recentConversations by viewModel.recentConversations.collectAsState()
    val trendingQuestions by viewModel.trendingQuestions.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                modifier = Modifier.padding(16.dp),
                onNotificationClick = {
                    navController.navigate(Screen.MainNav.Inbox.InboxScreen.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.MainNav.Settings.Profile.View.route)
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController as NavHostController)
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI Chat FAB
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.MainNav.Home.ConversationScreen.route)
                            isExpanded = false
                        },
                        containerColor = VerifAiColor.Navy.Light,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "AI와 대화하기"
                        )
                    }
                }

                // Question FAB
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.MainNav.Explore.Question.Create.route)
                            isExpanded = false
                        },
                        containerColor = VerifAiColor.Navy.Medium,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionAnswer,
                            contentDescription = "질문하기"
                        )
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { isExpanded = !isExpanded },
                    containerColor = VerifAiColor.Navy.Deep,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (isExpanded) "메뉴 닫기" else "메뉴 열기",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Recent Conversations with AI
            RecentConversationsCard(
                conversations = recentConversations,
                onSubClick = {
                    navController.navigate(Screen.MainNav.Home.route)
                },
                onConversationClick = { conversationId ->
                    navController.navigate(Screen.MainNav.Home.ConversationDetail.createRoute(conversationId))
                }
            )

            // Trending Questions
            TrendingQuestionsCard(
                questions = trendingQuestions,
                onSeeMoreClick = {
                    navController.navigate(Screen.MainNav.Explore.ExploreScreen.route)
                },
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Explore.Question.Detail.createRoute(questionId))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "Verif AI",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = VerifAiColor.TextPrimary
            )
        },
        actions = {
            // Notification Icon
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = VerifAiColor.TextPrimary
                )
            }

            // Profile Icon
            IconButton(onClick = onProfileClick) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = VerifAiColor.TextPrimary,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.White,
        ),
        modifier = modifier
    )
}

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}