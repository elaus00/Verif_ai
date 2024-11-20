package mp.verif_ai.presentation.screens.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import mp.verif_ai.domain.model.Notification
import mp.verif_ai.domain.model.NotificationType
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.viewmodel.InboxUiState
import mp.verif_ai.presentation.viewmodel.InboxViewModel
import mp.verif_ai.util.getTimeAgo

@Composable
fun InboxScreen(
    viewModel: InboxViewModel = hiltViewModel(),
    onNotificationClick: (String) -> Unit,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)  // Scaffold의 padding 적용
                .padding(horizontal = 25.dp)  // 좌우 padding 추가
        ) {
            InboxHeader(
                onClearAll = { /* TODO: Implement clear all */ }
            )

            SearchBar(
                onSearch = { /* TODO: Implement search */ }
            )

            NotificationList(
                uiState = uiState,
                onNotificationClick = onNotificationClick
            )
        }
    }
}
@Composable
private fun InboxHeader(
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onClearAll) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear all"
            )
        }
    }
}

@Composable
private fun SearchBar(
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onSearch(it)
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF171A1F)
                ),
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF171A1F).copy(alpha = 0.64f)
                        )
                    }
                    innerTextField()
                }
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF7F7F7F)
            )
        }
    }
}

@Composable
private fun NotificationList(
    uiState: InboxUiState,
    onNotificationClick: (String) -> Unit
) {
    when (uiState) {
        is InboxUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is InboxUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = uiState.message)
            }
        }
        is InboxUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Notification Icon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = Color(0xFF2A5AB3),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        NotificationType.REPLY -> Icons.AutoMirrored.Filled.Reply
                        NotificationType.COMMENT -> Icons.AutoMirrored.Filled.Comment
                        NotificationType.UPVOTE -> Icons.Default.ThumbUp
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = notification.questionTitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = getNotificationMessage(notification),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = getTimeAgo(notification.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9095A0)
            )
        }
    }
}

private fun getNotificationMessage(notification: Notification): String {
    return when (notification.type) {
        NotificationType.REPLY -> "${notification.actorName} replied to your question."
        NotificationType.COMMENT -> "${notification.actorName} commented on your question."
        NotificationType.UPVOTE -> "${notification.actorName} upvoted your answer."
    }
}