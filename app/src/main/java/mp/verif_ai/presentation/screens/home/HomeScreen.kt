package mp.verif_ai.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
            FloatingActionButton(
                onClick = { navController.navigate(Screen.MainNav.Home.ConversationScreen.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Start Conversation",
                    tint = Color.White
                )
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
                    navController.navigate(Screen.MainNav.Explore.Detail.createRoute(questionId))
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