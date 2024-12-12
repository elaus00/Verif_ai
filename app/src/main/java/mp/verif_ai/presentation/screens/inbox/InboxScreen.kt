
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.SwipeAction
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.viewmodel.InboxUiState
import mp.verif_ai.presentation.viewmodel.InboxViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: InboxViewModel = hiltViewModel(),
    onNotificationClick: (Notification) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var refreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(refreshing)

    LaunchedEffect(refreshing) {
        if (refreshing) {
            viewModel.refresh()
            refreshing = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("알림") },
                actions = {
                    IconButton(onClick = { /* Show filter options */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "필터")
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController)
        },
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { refreshing = true },
            modifier = Modifier.padding(paddingValues)
        ) {
            when (uiState) {
                is InboxUiState.Loading -> LoadingState()
                is InboxUiState.Empty -> EmptyState()
                is InboxUiState.Success -> {
                    val notifications = (uiState as InboxUiState.Success).notifications
                    NotificationList(
                        notifications = notifications,
                        onNotificationClick = onNotificationClick,
                        onSwipe = viewModel::onNotificationSwiped,
                        onLoadMore = viewModel::loadMore
                    )
                }
                is InboxUiState.Error -> ErrorState(
                    message = (uiState as InboxUiState.Error).message,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationList(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit,
    onSwipe: (Notification, SwipeAction) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            SwipeableNotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onSwipe = { action -> onSwipe(notification, action) }
            )
        }

        if (notifications.isNotEmpty()) {
            item {
                LoadMoreIndicator(onLoadMore = onLoadMore)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableNotificationCard(
    notification: Notification,
    onClick: () -> Unit,
    onSwipe: (SwipeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(false) }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
        val transition = updateTransition(transitionState, "cardTransition")
        val offsetTransition by transition.animateFloat(
            label = "offsetTransition",
            targetValueByState = { if (isRevealed) -200f else 0f } // ToDo 오류 수정 필요
        )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset { IntOffset(offsetTransition.roundToInt(), 0) },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NotificationContent(
                notification = notification,
                modifier = Modifier.weight(1f)
            )
            if (!notification.isRead) {
                Badge(
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationContent(
    notification: Notification,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = notification.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = notification.content,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = notification.timestamp.formatToDateTime(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadMoreIndicator(
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }

    LaunchedEffect(Unit) {
        onLoadMore()
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No notifications",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "If there's a notification, you'll see it here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text("다시 시도")
        }
    }
}

private fun Long.formatToDateTime(): String {
    return SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(Date(this))
}