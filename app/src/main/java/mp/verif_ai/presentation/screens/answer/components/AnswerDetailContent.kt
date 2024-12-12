package mp.verif_ai.presentation.screens.answer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.question.components.CommentInput
import mp.verif_ai.presentation.screens.question.components.CommentItem
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun AnswerDetailContent(
    question: Question,
    answer: Answer,
    currentUserId: String, // 추가
    onAdoptAnswer: () -> Unit,
    onCommentSubmit: (String) -> Unit,
    onCommentDelete: (String) -> Unit,
    onCommentReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommentInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 질문 요약 정보
        Text(
            text = question.title,
            style = MaterialTheme.typography.titleLarge,
            color = VerifAiColor.TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 답변 내용
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (answer.id == question.selectedAnswerId)
                    VerifAiColor.Navy.Light
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = answer.authorName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (answer.id == question.selectedAnswerId) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "채택됨",
                                tint = VerifAiColor.Navy.Deep
                            )
                        }
                    }

                    Text(
                        text = DateUtils.formatRelativeTime(answer.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = VerifAiColor.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = answer.content,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (question.authorId == "현재_사용자_ID" && // 실제 구현시 현재 사용자 ID 사용
                    question.selectedAnswerId == null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onAdoptAnswer,
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerifAiColor.Navy.Deep
                        )
                    ) {
                        Text("답변 채택하기")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 댓글 섹션
        Text(
            text = "댓글 ${answer.comments.size}개",
            style = MaterialTheme.typography.titleMedium
        )

        answer.comments.forEach { comment ->
            CommentItem(
                comment = comment,
                currentUserId = currentUserId,
                onDeleteClick = { onCommentDelete(comment.id) },
                onReportClick = { onCommentReport(comment.id) },
                onReplyClick = { showCommentInput = true }
            )
            Divider(color = VerifAiColor.DividerColor)
        }
    }

    // 댓글 입력
    if (showCommentInput) {
        CommentInput(
            value = commentText,
            onValueChange = { commentText = it },
            onSubmit = {
                if (commentText.isNotBlank()) {
                    onCommentSubmit(commentText)
                    showCommentInput = false
                    commentText = ""
                }
            },
            onDismiss = {
                showCommentInput = false
                commentText = ""
            }
        )
    }
}