package mp.verif_ai.presentation.screens.question.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun QuestionCard(
    title: String,
    status: QuestionStatus, // String에서 QuestionStatus로 변경
    category: String,
    tags: List<String>,
    authorName: String,
    points: Int,
    answerCount: Int,
    viewCount: Int,
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            focusedElevation = 3.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuestionStatusChip(status = status)
//                    PointsBadge(points = points)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = VerifAiColor.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tags with limit
            if (tags.isNotEmpty()) {
                TagRow(
                    tags = tags,
                    maxVisibleTags = 3,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

//            Divider(
//                color = VerifAiColor.DividerColor.copy(alpha = 0.5f),
//                modifier = Modifier.padding(vertical = 2.dp)
//            )

            // Footer: Author info and Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = VerifAiColor.TextTertiary
                )

                // Right side: Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        icon = Icons.Default.QuestionAnswer,
                        count = answerCount,
                        contentDescription = "Answer count"
                    )
                    StatItem(
                        icon = Icons.Default.RemoveRedEye,
                        count = viewCount,
                        contentDescription = "View count"
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionStatusChip(
    status: QuestionStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        QuestionStatus.OPEN -> Pair(VerifAiColor.Status.PublishedBg, VerifAiColor.Status.PublishedText)
        QuestionStatus.CLOSED -> Pair(VerifAiColor.Status.ClosedBg, VerifAiColor.Status.ClosedText)
        QuestionStatus.IN_PROGRESS -> Pair(VerifAiColor.Status.DraftBg, VerifAiColor.Status.DraftText)
        QuestionStatus.EXPIRED -> Pair(VerifAiColor.Status.DeletedBg, VerifAiColor.Status.DeletedText)
        QuestionStatus.DELETED -> Pair(VerifAiColor.Status.DeletedBg, VerifAiColor.Status.DeletedText)
    }

    val statusText = when (status) {
        QuestionStatus.OPEN -> "Waiting for Answer"
        QuestionStatus.CLOSED -> "Answered"
        QuestionStatus.IN_PROGRESS -> "In Progress"
        QuestionStatus.EXPIRED -> "Expired"
        QuestionStatus.DELETED -> "Deleted"
    }

    AssistChip(
        modifier = Modifier
            .wrapContentSize(),
        onClick = { },
        label = {
            Text(text = statusText,
                style = TextStyle(fontSize = 12.sp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor,
            labelColor = textColor
        ),
    )
}

@Composable
fun PointsBadge(
    points: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = when {
            points >= 1000 -> VerifAiColor.Status.PublishedBg
            points >= 500 -> VerifAiColor.Navy.BaseAlpha
            else -> VerifAiColor.Navy.DarkAlpha
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Numbers,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = when {
                    points >= 1000 -> VerifAiColor.Status.PublishedText
                    points >= 500 -> VerifAiColor.Navy.Base
                    else -> VerifAiColor.Navy.Dark
                }
            )
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = when {
                    points >= 1000 -> VerifAiColor.Status.PublishedText
                    points >= 500 -> VerifAiColor.Navy.Base
                    else -> VerifAiColor.Navy.Dark
                }
            )
        }
    }
}


@Composable
private fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = VerifAiColor.Navy.BaseAlpha,
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = VerifAiColor.Navy.Deep,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TagRow(
    tags: List<String>,
    maxVisibleTags: Int = 3,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = tags,
            key = { tag -> tag }
        ) { tag ->
            AssistChip(
                onClick = { },
                label = { Text(tag) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = VerifAiColor.Navy.DarkAlpha,
                    labelColor = VerifAiColor.Navy.Dark
                )
            )
        }
//
//        if (tags.size > maxVisibleTags) {
//            item {
//                AssistChip(
//                    onClick = { },
//                    label = {
//                        Text(
//                            text = "+${tags.size - maxVisibleTags}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    },
//                    colors = AssistChipDefaults.assistChipColors(
//                        containerColor = VerifAiColor.Navy.DarkAlpha,
//                        labelColor = VerifAiColor.Navy.Dark
//                    )
//                )
//            }
//        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    count: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = VerifAiColor.TextSecondary
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = VerifAiColor.TextSecondary
        )
    }
}