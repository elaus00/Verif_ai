package mp.verif_ai.presentation.screens.question.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import mp.verif_ai.presentation.screens.question.comment.CommentInput
import mp.verif_ai.presentation.screens.question.comment.CommentItem
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionDetailContent(
    question: Question,
    onAnswerClick: (String) -> Unit,
    onCommentSubmit: (String) -> Unit,
    onCommentDelete: (String) -> Unit,
    onCommentReport: (String) -> Unit,
    onLikeClick: () -> Unit,
    onReportClick: (Pair<ReportReason, String>) -> Unit,
    commentContent: String,
    onCommentContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var showCommentInput by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Question Header
            item {
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
            }

            item {
                Divider(color = VerifAiColor.DividerColor)
            }

            // Actions
            item {
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
                            isLiked = false,
                            likeCount = 0,
                            onLikeClick = onLikeClick
                        )

                        TextButton(
                            onClick = { showCommentInput = true }
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
            }

            item {
                Divider(color = VerifAiColor.DividerColor)
            }

            // Comments Section Header
            item {
                Text(
                    text = "댓글 ${question.comments.size}개",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = VerifAiColor.TextPrimary
                )
            }

            // Comments
            items(
                items = question.comments,
                key = { it.id }
            ) { comment ->
                CommentItem(
                    comment = comment,
                    currentUserId = "", // TODO: AuthRepository에서 가져오기
                    onDeleteClick = { onCommentDelete(comment.id) },
                    onReportClick = { onCommentReport(comment.id) },
                    onReplyClick = { showCommentInput = true }
                )
            }

            // Answers Section Header with Write Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "답변 ${question.answers.size}개",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerifAiColor.TextPrimary
                    )
                    Button(
                        onClick = { onAnswerClick("create") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerifAiColor.Navy.Deep
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("답변하기")
                    }
                }
            }

            // Answers
            items(
                items = question.answers,
                key = { it.id }
            ) { answer ->
                AnswerItem(
                    answer = answer,
                    isAdopted = answer.id == question.selectedAnswerId,
                    onAnswerClick = onAnswerClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Comment Input (Overlaid at the bottom when visible)
        AnimatedVisibility(
            visible = showCommentInput,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            CommentInput(
                value = commentContent,
                onValueChange = onCommentContentChange,
                onSubmit = {
                    onCommentSubmit(commentContent)
                    showCommentInput = false
                },
                onDismiss = {
                    showCommentInput = false
                    onCommentContentChange("")
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
                onReportClick(Pair(reason, comment))
                showReportDialog = false
            }
        )
    }
}