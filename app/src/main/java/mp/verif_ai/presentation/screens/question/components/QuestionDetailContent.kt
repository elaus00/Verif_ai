package mp.verif_ai.presentation.screens.question.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.answer.components.AnswerItem
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionDetailContent(
    question: Question,
    onAnswerClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var showCommentInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Question Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.headlineSmall,
                color = VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VerifAiColor.TextSecondary
                )
                Text(
                    text = "•",
                    color = VerifAiColor.TextSecondary
                )
                Text(
                    text = DateUtils.formatFullDate(question.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = VerifAiColor.TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = question.content,
                style = MaterialTheme.typography.bodyLarge,
                color = VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.tags.forEach { tag ->
                    AssistChip(
                        onClick = { },
                        label = { Text(tag) }
                    )
                }
            }
        }

        Divider(color = VerifAiColor.DividerColor)

        // Actions
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LikeButton(
                    isLiked = false,  // TODO: 실제 좋아요 상태 연동
                    likeCount = 0,    // TODO: 실제 좋아요 수 연동
                    onLikeClick = onLikeClick
                )

                TextButton(
                    onClick = { /* TODO: 댓글 입력 포커스 */ }
                ) {
                    Icon(Icons.Default.Comment, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("댓글")
                }
            }

            IconButton(onClick = { showReportDialog = true }) {
                Icon(Icons.Default.Flag, contentDescription = "신고")
            }
        }

        Divider(color = VerifAiColor.DividerColor)

        // Comments Section
        CommentSection(
            comments = question.comments,
            onCommentClick = onCommentClick,
            onReplyClick = { commentId ->
                showCommentInput = true
                // TODO: 대댓글 구현
            },
            onLikeClick = { commentId ->
                // TODO: 댓글 좋아요 구현
            }
        )

        // Answers Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "답변 ${question.answers.size}개",
                style = MaterialTheme.typography.titleMedium,
                color = VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            question.answers.forEach { answer ->
                AnswerItem(
                    answer = answer,
                    isAdopted = answer.id == question.selectedAnswerId,
                    onAnswerClick = onAnswerClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Comment Input
        AnimatedVisibility(
            visible = showCommentInput,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            CommentInput(
                value = commentText,
                onValueChange = { commentText = it },
                onSubmit = {
                    // TODO: 댓글 제출 구현
                    showCommentInput = false
                    commentText = ""
                },
                onDismiss = {
                    showCommentInput = false
                    commentText = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            )
        }
    }

    // Report Dialog
    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onConfirm = { reason, comment ->
                onReportClick()
                showReportDialog = false
                // TODO: 신고 처리 구현
            }
        )
    }
}