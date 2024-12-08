package mp.verif_ai.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import mp.verif_ai.presentation.screens.theme.VerifAiColor

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