package mp.verif_ai.presentation.screens.question.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.model.question.Comment
import mp.verif_ai.domain.model.question.CommentStatus
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: String,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        color = VerifAiColor.TextPrimary
                    )
                    if (comment.isReported) {
                        Text(
                            text = "신고됨",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = DateUtils.formatRelativeTime(comment.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = VerifAiColor.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (comment.status == CommentStatus.HIDDEN)
                    VerifAiColor.TextSecondary
                else
                    VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onReplyClick) {
                    Text("답글")
                }

                if (comment.authorId == currentUserId) {
                    TextButton(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("삭제")
                    }
                } else {
                    TextButton(onClick = onReportClick) {
                        Text("신고")
                    }
                }
            }

            if (comment.status == CommentStatus.HIDDEN) {
                Text(
                    text = "신고로 인해 숨겨진 댓글입니다",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}