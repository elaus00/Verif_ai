package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationViewModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun ConversationContent(
    messages: List<Message>,
    expertReviews: List<ExpertReview>,
    canRequestExpertReview: Boolean,
    pointBalance: Int,
    onRequestExpertReview: () -> Unit,
    onExpertProfileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 8.dp),
        reverseLayout = true,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages.reversed()) { message ->
            val isCurrentUser = message.messageSource?.type == SourceType.USER

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isCurrentUser) {
                    Alignment.CenterEnd
                } else {
                    Alignment.CenterStart
                }
            ) {
                MessageWithContextMenu(
                    message = message,
                    canRequestExpertReview = canRequestExpertReview,
                    pointBalance = pointBalance,
                    onRequestExpertReview = onRequestExpertReview,
                    onCopy = {},
                    onShare = {},
                ) {
                    Surface(
                        color = when (message.messageSource?.type) {
                            SourceType.USER -> VerifAiColor.Primary.copy(alpha = 0.1f)
                            SourceType.AI -> MaterialTheme.colorScheme.surface
                            SourceType.EXPERT -> VerifAiColor.Secondary.copy(alpha = 0.1f)
                            null -> MaterialTheme.colorScheme.surface
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            when (message.messageSource?.type) {
                                SourceType.USER -> VerifAiColor.Primary.copy(alpha = 0.2f)
                                SourceType.AI -> VerifAiColor.DividerColor
                                SourceType.EXPERT -> VerifAiColor.Secondary.copy(alpha = 0.2f)
                                null -> VerifAiColor.DividerColor
                            }
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Message source indicator (AI model or Expert)
                            if (message.messageSource?.type != SourceType.USER) {
                                Text(
                                    text = when (message.messageSource?.type) {
                                        SourceType.AI -> message.messageSource!!.model?.apiName ?: "AI"
                                        SourceType.EXPERT -> "전문가"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerifAiColor.TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            // Message content
                            Text(
                                text = message.content,
                                color = VerifAiColor.TextPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            // Verification status if applicable
                            if (message is Message.Text && message.isVerified) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Verified,
                                        contentDescription = "검증됨",
                                        tint = VerifAiColor.Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "검증됨",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VerifAiColor.Primary
                                    )
                                }
                            }

                            // Expert reviews
                            if (message is Message.Text && message.expertReviews.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                message.expertReviews.forEach { review ->
                                    ExpertReviewItem(
                                        review = review,
                                        onExpertClick = onExpertProfileClick
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpertReviewItem(
    review: ExpertReview,
    onExpertClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = VerifAiColor.SurfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { onExpertClick(review.expertId) }
                .padding(8.dp)
        ) {
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = VerifAiColor.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "전문가 평가",
                    style = MaterialTheme.typography.labelSmall,
                    color = VerifAiColor.TextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "평점",
                        tint = VerifAiColor.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = review.rating.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = VerifAiColor.Primary
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = VerifAiColor.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = VerifAiColor.Primary
            )
        ) {
            Text("다시 시도")
        }
    }
}