package mp.verif_ai.presentation.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.presentation.screens.home.EmptyState
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import kotlin.collections.forEach

@Composable
fun TrendingQuestionsCard(
    questions: List<TrendingQuestion>,
    onSeeMoreClick: () -> Unit,
    onQuestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            CardHeader(
                title = "Trending questions",
                onSeeMoreClick = onSeeMoreClick
            )

            AnimatedVisibility(
                visible = questions.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    questions.forEach { question ->
                        TrendingQuestionItem(
                            question = question,
                            onClick = { onQuestionClick(question.id) }
                        )
                        if (question != questions.last()) {
                            Divider(color = VerifAiColor.DividerColor)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = questions.isEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                EmptyState(
                    message = "트렌딩 질문이 없습니다"
                )
            }
        }
    }
}

@Composable
fun TrendingQuestionItem(
    question: TrendingQuestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question.title,
            style = MaterialTheme.typography.bodyMedium,
            color = VerifAiColor.TextPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // View Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Views",
                    modifier = Modifier.size(16.dp),
                    tint = VerifAiColor.TextTertiary
                )
                Text(
                    text = "${question.viewCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }

            // Comment Count
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Comments",
                    modifier = Modifier.size(16.dp),
                    tint = VerifAiColor.TextTertiary
                )
                Text(
                    text = "${question.commentCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = VerifAiColor.TextTertiary
                )
            }
        }
    }
}


// Common Components
@Composable
fun CardHeader(
    title: String,
    onSeeMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.5).sp,
                lineHeight = 24.sp
            ),
            color = VerifAiColor.TextPrimary
        )

        TextButton(
            onClick = onSeeMoreClick,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = VerifAiColor.Primary.copy(alpha = 0.8f)
            )
        ) {
            Text(
                text = "더보기",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 0.sp
                )
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "See more",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}