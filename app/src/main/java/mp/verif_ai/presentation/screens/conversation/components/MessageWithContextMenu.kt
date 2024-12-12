package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.presentation.screens.components.CustomContextMenu
import mp.verif_ai.presentation.screens.components.CustomSnackbar
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
                text = "Request validation for Expert",
                icon = { color ->
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = if (pointBalance >= Adoption.EXPERT_REVIEW_POINTS) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            VerifAiColor.Status.DeletedText
                        }
                    )
                },
                subText = "Point needed: ${Adoption.EXPERT_REVIEW_POINTS}",
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