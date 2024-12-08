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
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                onClick = { navController.navigate(Screen.MainNav.Home.ConversationDetail.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar for Questions
            SearchBar(
                onCreateQuestion = {
                    navController.navigate(Screen.MainNav.Explore.Create.route)
                }
            )

            // Recent Conversations with AI
            RecentConversationsCard(
                conversations = recentConversations,
                onSeeMoreClick = {
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
            QuestionStatusChip(status = question.status)
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
            // Date
            Text(
                text = formatTimestamp(question.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = VerifAiColor.TextTertiary
            )

            // Views and Points
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

                // Points
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Points",
                        modifier = Modifier.size(16.dp),
                        tint = VerifAiColor.TextTertiary
                    )
                    Text(
                        text = "${question.points}P",
                        style = MaterialTheme.typography.labelMedium,
                        color = VerifAiColor.TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionStatusChip(
    status: QuestionStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        QuestionStatus.OPEN -> VerifAiColor.Status.PublishedBg to VerifAiColor.Status.PublishedText
        QuestionStatus.CLOSED -> VerifAiColor.Status.ClosedBg to VerifAiColor.Status.ClosedText
        QuestionStatus.EXPIRED -> VerifAiColor.Status.DeletedBg to VerifAiColor.Status.DeletedText
        QuestionStatus.DELETED -> VerifAiColor.Status.DeletedBg to VerifAiColor.Status.DeletedText
        QuestionStatus.IN_PROGRESS -> VerifAiColor.Status.DraftBg to VerifAiColor.Status.DraftText
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Text(
            text = when(status) {
                QuestionStatus.OPEN -> "답변 대기"
                QuestionStatus.CLOSED -> "답변 완료"
                QuestionStatus.EXPIRED -> "만료됨"
                QuestionStatus.DELETED -> "삭제됨"
                QuestionStatus.IN_PROGRESS -> "답변 중"
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
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
        // Title and Type
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.title,
                style = MaterialTheme.typography.titleMedium,
                color = VerifAiColor.TextPrimary,
                maxLines = 1
            )
            QuestionStatusChip(status = conversation.status)
        }

        // Last message preview if exists
        conversation.messages.lastOrNull()?.let { lastMessage ->
            Text(
                text = lastMessage.content,
                style = MaterialTheme.typography.bodyMedium,
                color = VerifAiColor.TextSecondary,
                maxLines = 2
            )
        }

        // Bottom Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimestamp(conversation.updatedAt),
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
                    text = "${conversation.participantIds.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun QuestionStatusChip(
    status: ConversationStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        ConversationStatus.ACTIVE -> VerifAiColor.Status.PublishedBg to VerifAiColor.Status.PublishedText
        ConversationStatus.COMPLETED -> VerifAiColor.Status.ClosedBg to VerifAiColor.Status.ClosedText
        ConversationStatus.EXPIRED -> VerifAiColor.Status.DeletedBg to VerifAiColor.Status.DeletedText
        ConversationStatus.DELETED -> VerifAiColor.Status.DeletedBg to VerifAiColor.Status.DeletedText
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Text(
            text = when(status) {
                ConversationStatus.ACTIVE -> "진행중"
                ConversationStatus.COMPLETED -> "완료"
                ConversationStatus.EXPIRED -> "만료됨"
                ConversationStatus.DELETED -> "삭제됨"
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
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