package mp.verif_ai.presentation.screens.question.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.verif_ai.presentation.screens.theme.VerifAiColor


@Composable
fun QuestionCard(
    title: String,
    status: String,
    date: String,
    viewCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        CardContent(
            title = title,
            status = status,
            date = date,
            viewCount = viewCount
        )
    }
}

@Composable
fun QuestionStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text(status) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when(status) {
                "OPEN" -> VerifAiColor.Status.PublishedBg
                "CLOSED" -> VerifAiColor.Status.ClosedBg
                "IN_PROGRESS" -> VerifAiColor.Status.DraftBg
                else -> VerifAiColor.Status.DeletedBg
            },
            labelColor = when(status) {
                "OPEN" -> VerifAiColor.Status.PublishedText
                "CLOSED" -> VerifAiColor.Status.ClosedText
                "IN_PROGRESS" -> VerifAiColor.Status.DraftText
                else -> VerifAiColor.Status.DeletedText
            }
        ),
        modifier = modifier
    )
}

@Composable
fun QuestionGuideText(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Text(
            text = """
                질문 작성 가이드:
                1. 구체적이고 명확한 질문을 작성해주세요
                2. 관련된 배경 정보를 포함해주세요
                3. 시도해본 방법이 있다면 함께 공유해주세요
                4. 적절한 태그를 선택해 주제를 분류해주세요
                5. 예의 바른 언어를 사용해주세요
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextSecondary,
            modifier = modifier
        )
    }
}

@Composable
internal fun CardContent(
    title: String,
    status: String,
    date: String,
    viewCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = VerifAiColor.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Chip
            AssistChip(
                onClick = { },
                label = { Text(status) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (status) {
                        "답변 대기" -> VerifAiColor.Status.PublishedBg
                        "답변 완료" -> VerifAiColor.Status.ClosedBg
                        "답변 중" -> VerifAiColor.Status.DraftBg
                        "기간 만료" -> VerifAiColor.Status.DeletedBg
                        else -> VerifAiColor.Status.DeletedBg
                    },
                    labelColor = when (status) {
                        "답변 대기" -> VerifAiColor.Status.PublishedText
                        "답변 완료" -> VerifAiColor.Status.ClosedText
                        "답변 중" -> VerifAiColor.Status.DraftText
                        "기간 만료" -> VerifAiColor.Status.DeletedText
                        else -> VerifAiColor.Status.DeletedText
                    }
                ),
                modifier = Modifier.height(24.dp)
            )

            // Info Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = VerifAiColor.TextSecondary
                )

                // Divider
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerifAiColor.TextSecondary
                )

                // View Count
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveRedEye,
                        contentDescription = null,
                        tint = VerifAiColor.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = viewCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = VerifAiColor.TextSecondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardContentPreview() {
    CardContent(
        title = "Android에서 Jetpack Compose로 UI를 만드는 방법에 대해 질문 있습니다.",
        status = "답변 대기",
        date = "2024.01.01",
        viewCount = 128
    )
}