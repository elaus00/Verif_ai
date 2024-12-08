package mp.verif_ai.presentation.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.presentation.screens.home.EmptyState
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach


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
