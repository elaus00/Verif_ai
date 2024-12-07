package mp.verif_ai.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun CustomDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    iconContent: (@Composable (Color) -> Unit)? = null,
    itemContent: (@Composable (String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Dropdown rotation"
    )

    Column(modifier = modifier
        .background(Color.Transparent)) {
        // Optional Label
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) MaterialTheme.colorScheme.error
                else VerifAiColor.TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Dropdown Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (enabled) MaterialTheme.colorScheme.surface
                    else VerifAiColor.Component.Button.Disabled.copy(alpha = 0.1f)
                )
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> MaterialTheme.colorScheme.error
                        expanded -> VerifAiColor.Navy.Base
                        else -> VerifAiColor.BorderColor
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                }
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (iconContent != null) {
                        iconContent(
                            if (enabled) VerifAiColor.Component.Icon.Primary
                            else VerifAiColor.Component.Icon.Disabled
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = selectedItem,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) VerifAiColor.TextPrimary
                        else VerifAiColor.TextTertiary
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.rotate(rotationState),
                    tint = if (enabled) VerifAiColor.Component.Icon.Primary
                    else VerifAiColor.Component.Icon.Disabled
                )
            }
        }

        // Error Message
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .width(IntrinsicSize.Max)
                .border(
                    width = 1.dp,
                    color = VerifAiColor.BorderColor,

                ),
            properties = PopupProperties(focusable = true),
            shape = RoundedCornerShape(16.dp)
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
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (item == selectedItem)
                                VerifAiColor.Interaction.Hover
                            else MaterialTheme.colorScheme.surface
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomDropdownMenuPreview() {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")
    var selectedItem by remember { mutableStateOf(items[0]) }

    CustomDropdownMenu(
        items = items,
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it },
        label = "Select an item",
        modifier = Modifier.padding(16.dp)
    )
}