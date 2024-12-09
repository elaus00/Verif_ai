package mp.verif_ai.presentation.screens.answer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.util.date.DateUtils
import mp.verif_ai.presentation.screens.question.components.LikeButton
import mp.verif_ai.presentation.screens.theme.VerifAiColor


@Composable
fun AnswerItem(
    answer: Answer,
    isAdopted: Boolean,
    onAnswerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onAnswerClick(answer.id) },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAdopted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (answer.isExpertAnswer) {
                        AssistChip(
                            onClick = { },
                            label = { Text("전문가") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = VerifAiColor.Status.PublishedBg,
                                labelColor = VerifAiColor.Status.PublishedText
                            )
                        )
                    }

                    Text(
                        text = answer.authorName,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                if (isAdopted) {
                    AssistChip(
                        onClick = { },
                        label = { Text("채택됨") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = VerifAiColor.Status.PublishedText
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = VerifAiColor.Status.PublishedBg,
                            labelColor = VerifAiColor.Status.PublishedText
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = answer.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateUtils.getTimeAgo(answer.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = VerifAiColor.TextSecondary
                )

                LikeButton(
                    isLiked = false,  // TODO: 실제 좋아요 상태 연동
                    likeCount = answer.helpfulCount,
                    onLikeClick = { /* TODO: 좋아요 기능 구현 */ }
                )
            }
        }
    }
}