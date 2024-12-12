package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.presentation.screens.conversation.viewmodel.ConversationViewModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun ConversationContent(
    messages: List<Message>,
    canRequestExpertReview: Boolean,
    pointBalance: Int,
    onRequestExpertReview: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        reverseLayout = true,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(messages.reversed()) { message ->
            val isCurrentUser = message.messageSource?.type == SourceType.USER

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                    onCopy = { viewModel.copyMessageToClipboard(message) },
                    onShare = { viewModel.shareMessage(message) },
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Message source indicator (AI model or Expert)
                            if (message.messageSource?.type != SourceType.USER) {
                                Text(
                                    text = when (message.messageSource?.type) {
                                        SourceType.AI -> message.messageSource!!.model?.displayName ?: "AI"
                                        SourceType.EXPERT -> "전문가"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerifAiColor.TextSecondary,
                                )
                            }

                            // Message content
                            Text(
                                text = message.content,
                                color = VerifAiColor.TextPrimary,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
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


@Composable
fun PagerContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pageIndex: Int,
    content: @Composable () -> Unit
) {
    val scaleFactor = rememberPagerTransition(pagerState)
    val alpha = if (pagerState.currentPage == pageIndex) 1f else 0.6f

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scaleFactor.scaleX
                scaleY = scaleFactor.scaleY
                this.alpha = alpha
            }
            .fillMaxSize()
    ) {
        content()
    }
}