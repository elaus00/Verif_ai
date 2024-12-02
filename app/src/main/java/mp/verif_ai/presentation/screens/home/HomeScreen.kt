package mp.verif_ai.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import mp.verif_ai.domain.model.prompt.Conversation
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onQuestionClick: (String) -> Unit,
    onCreateQuestion: () -> Unit,
    onSeeMoreQuestions: () -> Unit,
    onSeeMoreConversations: () -> Unit,
    onSeeMoreTrending: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar()
        },
        bottomBar = {
            AppBottomNavigation(navController = navController as NavHostController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.MainNav.Prompt.Main.route) },
                containerColor = VerifAiColor.Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Open Prompt",
                    tint = Color.White
                )
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 25.dp)
                .padding(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Search Bar (Now clickable)
            SearchBar(
                onCreateQuestion = onCreateQuestion
            )

            // My Questions Card
            MyQuestionsCard(
                questions = listOf(
                    Question(title= "Is this answer accurate?", content = "Kotlin variables : var or val?"),
                    Question(title = "This answer from AI does not work.", content = "How to implement OCR on application")
                ),
                onSeeMoreClick = onSeeMoreQuestions,
                onQuestionClick = onQuestionClick
            )

            // Recent Conversations Card
            RecentConversationsCard(
                conversations = listOf(/* ... */),
                onSeeMoreClick = onSeeMoreConversations,
                onConversationClick = { /* Handle conversation click */ }
            )

            // Trending Questions Card
            TrendingQuestionsCard(
                questions = listOf(/* ... */),
                onSeeMoreClick = onSeeMoreTrending,
                onQuestionClick = onQuestionClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
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
            IconButton(
                onClick = onNotificationClick
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = VerifAiColor.TextPrimary
                )
            }

            // Profile Icon
            IconButton(
                onClick = onProfileClick
            ) {
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
private fun SearchBar(
    onCreateQuestion: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onCreateQuestion),
        shape = RoundedCornerShape(20.dp),
        color = VerifAiColor.Background
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Validate my questions",
                style = MaterialTheme.typography.bodyMedium,
                color = VerifAiColor.TextPrimary.copy(alpha = 0.64f)
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Question",
                tint = VerifAiColor.TextPrimary
            )
        }
    }
}

@Composable
fun SeeMoreButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "see more",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Normal
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "See more",
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun MyQuestionsCard(
    questions: List<Question>,
    onSeeMoreClick: () -> Unit,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            // Header
            CardHeader(
                title = "My questions",
                onSeeMoreClick = onSeeMoreClick
            )

            // Questions list with animation
            AnimatedVisibility(
                visible = questions.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    questions.forEach { question ->
                        QuestionItem(
                            question = question,
                            onClick = { onQuestionClick(question.id) }
                        )
                        if (question != questions.last()) {
                            Divider(color = VerifAiColor.DividerColor)
                        }
                    }
                }
            }

            // Empty state
            AnimatedVisibility(
                visible = questions.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                EmptyState(
                    message = "아직 작성한 질문이 없습니다"
                )
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: Question,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Title and Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                color = VerifAiColor.TextPrimary,
                maxLines = 1
            )
            StatusChip(status = question.status)
        }

        // Content Preview
        Text(
            text = question.content,
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextSecondary,
            maxLines = 2
        )

        // Bottom Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Author and Date
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question.author,
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
                Text(
                    text = "•",
                    color = VerifAiColor.TextTertiary
                )
                Text(
                    text = question.formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }
        }
    }
}
@Composable
private fun StatusChip(
    status: QuestionStatus,
    modifier: Modifier = Modifier
) {
//    val (backgroundColor, textColor) = when (status) {
//        QuestionStatus.DRAFT -> VerifAiColor.StatusDraftBg to VerifAiColor.StatusDraftText
//        QuestionStatus.PUBLISHED -> VerifAiColor.StatusPublishedBg to VerifAiColor.StatusPublishedText
//        QuestionStatus.CLOSED -> VerifAiColor.StatusClosedBg to VerifAiColor.StatusClosedText
//        QuestionStatus.DELETED -> VerifAiColor.StatusDeletedBg to VerifAiColor.StatusDeletedText
//        QuestionStatus.CONTROVERSIAL -> VerifAiColor.StatusControversialBg to VerifAiColor.StatusControversialText
//    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun RecentConversationsCard(
    conversations: List<Conversation>,
    onSeeMoreClick: () -> Unit,
    onConversationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            CardHeader(
                title = "Recent Conversations with AI",
                onSeeMoreClick = onSeeMoreClick
            )

            AnimatedVisibility(
                visible = conversations.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    conversations.forEach { conversation ->
                        ConversationItem(
                            conversation = conversation,
                            onClick = { onConversationClick(conversation.id) }
                        )
//                        if (conversation != conversations.last()) {
//                            Divider(color = VerifAiColor.DividerColor)
//                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = conversations.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                EmptyState(
                    message = "아직 대화 기록이 없습니다"
                )
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Last Message
        Text(
            text = conversation.lastMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextPrimary,
            maxLines = 2
        )

        // Bottom Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = VerifAiColor.TextTertiary
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Participants",
                    modifier = Modifier.size(16.dp),
                    tint = VerifAiColor.TextTertiary
                )
                Text(
                    text = "${conversation.participantCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }
        }
    }
}

@Composable
fun TrendingQuestionsCard(
    questions: List<TrendingQuestion>,
    onSeeMoreClick: () -> Unit,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            CardHeader(
                title = "Trending questions",
                onSeeMoreClick = onSeeMoreClick
            )

            AnimatedVisibility(
                visible = questions.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    questions.forEach { question ->
                        TrendingQuestionItem(
                            question = question,
                            onClick = { onQuestionClick(question.id) }
                        )
                        if (question != questions.last()) {
                            Divider(color = VerifAiColor.DividerColor)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = questions.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                EmptyState(
                    message = "트렌딩 질문이 없습니다"
                )
            }
        }
    }
}

@Composable
fun TrendingQuestionItem(
    question: TrendingQuestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question.title,
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // View Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Views",
                    modifier = Modifier.size(16.dp),
                    tint = VerifAiColor.TextTertiary
                )
                Text(
                    text = "${question.viewCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }

            // Comment Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Comments",
                    modifier = Modifier.size(16.dp),
                    tint = VerifAiColor.TextTertiary
                )
                Text(
                    text = "${question.commentCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }
        }
    }
}

// Common Components
@Composable
fun CardHeader(
    title: String,
    onSeeMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.5).sp,
                lineHeight = 24.sp
            ),
            color = VerifAiColor.TextPrimary
        )

        TextButton(
            onClick = onSeeMoreClick,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = VerifAiColor.Primary.copy(alpha = 0.8f)
            )
        ) {
            Text(
                text = "더보기",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp
                )
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "See more",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
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