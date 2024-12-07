package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.presentation.screens.auth.CustomSnackbar
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageWithContextMenu(
    message: Message,
    canRequestExpertReview: Boolean = true,
    pointBalance: Int,
    onRequestExpertReview: () -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(DpOffset.Zero) }
    val interactionSource = remember { MutableInteractionSource()}
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .background(Color.White)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { },
                onLongClick = { showMenu = true }
        )
    ) {
        content()

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = menuOffset,
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
        ) {
            if (canRequestExpertReview) {
                DropdownMenuItem(
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "전문가 검증 요청",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "필요 포인트: ${Adoption.EXPERT_REVIEW_POINTS}P",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (pointBalance >= Adoption.EXPERT_REVIEW_POINTS) {
                                    VerifAiColor.TextSecondary
                                } else {
                                    VerifAiColor.Status.DeletedText
                                }
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (pointBalance >= Adoption.EXPERT_REVIEW_POINTS) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                VerifAiColor.Status.DeletedText
                            }
                        )
                    },
                    onClick = {
//                        if (pointBalance >= Adoption.EXPERT_REVIEW_POINTS) {
                        if (pointBalance <= Adoption.EXPERT_REVIEW_POINTS) { // 임시로 설정 ToDo
                                onRequestExpertReview()
                        } else {
                            showMenu = true // 디버그용 원래는 false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "포인트가 부족합니다",
                                    actionLabel = "Dismiss",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                        showMenu = false
                    },
//                    enabled = pointBalance >= Adoption.EXPERT_REVIEW_POINTS // TODO
                )

            DropdownMenuItem(
                text = { Text("복사하기") },
                onClick = {
                    onCopy(message.content)
                    showMenu = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = { Text("공유하기") },
                onClick = {
                    onShare(message.content)
                    showMenu = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData = snackbarData,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
    }

suspend fun onInsufficientPoints(snackbarHostState: SnackbarHostState) {
    snackbarHostState.showSnackbar(
        message = "포인트가 부족합니다",
        actionLabel = "Dismiss",
        duration = SnackbarDuration.Short
    )
}