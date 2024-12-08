package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
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
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    data class MenuItem(
        val text: String,
        val icon: @Composable (Color) -> Unit,
        val onClick: () -> Unit,
        val enabled: Boolean = true,
        val subText: String? = null
    )

    val menuItems = mutableListOf<MenuItem>()

    if (canRequestExpertReview) {
        menuItems.add(
            MenuItem(
                text = "전문가 검증 요청",
                icon = { color ->
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
                subText = "필요 포인트: ${Adoption.EXPERT_REVIEW_POINTS}P",
                onClick = {
                    if (pointBalance <= Adoption.EXPERT_REVIEW_POINTS) {
                        onRequestExpertReview()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "포인트가 부족합니다",
                                actionLabel = "Dismiss",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    showMenu = false
                }
            )
        )
    }

    menuItems.addAll(
        listOf(
            MenuItem(
                text = "복사하기",
                icon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                onClick = {
                    onCopy(message.content)
                    showMenu = false
                }
            ),
            MenuItem(
                text = "공유하기",
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                onClick = {
                    onShare(message.content)
                    showMenu = false
                }
            )
        )
    )

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { },
                onLongClick = { showMenu = true }
            )
    ) {
        content()

        CustomContextMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            items = menuItems.map { it.text },
            modifier = Modifier.width(200.dp),
            itemContent = { itemText ->
                val menuItem = menuItems.find { it.text == itemText }
                if (menuItem != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        menuItem.icon(VerifAiColor.Component.Icon.Primary)
                        Column {
                            Text(
                                text = menuItem.text,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            menuItem.subText?.let { subText ->
                                Text(
                                    text = subText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (pointBalance >= Adoption.EXPERT_REVIEW_POINTS) {
                                        VerifAiColor.TextSecondary
                                    } else {
                                        VerifAiColor.Status.DeletedText
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            CustomSnackbar(
                snackbarData = snackbarData,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CustomContextMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    modifier: Modifier = Modifier,
    itemContent: (@Composable (String) -> Unit)? = null
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .width(IntrinsicSize.Max)
            .border(
                width = 1.dp,
                color = VerifAiColor.BorderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        properties = PopupProperties(focusable = true)
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = {
                    if (itemContent != null) {
                        itemContent(item)
                    } else {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = VerifAiColor.TextPrimary
                        )
                    }
                },
                onClick = {
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}