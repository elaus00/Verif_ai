package mp.verif_ai.presentation.screens.conversation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@Composable
fun ChatBottomBar(
    userInput: String,
    onUserInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceRecognition: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, VerifAiColor.DividerColor)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AddButton(onClick = onAddClick)
                ChatTextField(
                    value = userInput,
                    onValueChange = onUserInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                ActionButton(
                    isInputEmpty = userInput.isBlank(),
                    onVoiceClick = onVoiceRecognition,
                    onSendClick = onSendMessage
                )
            }
        }
    }
}

@Composable
private fun AddButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = VerifAiColor.TextPrimary
        )
    }
}

@Composable
private fun ChatTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text("Message", color = VerifAiColor.TextSecondary) },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        maxLines = 1,
    )
}

@Composable
private fun ActionButton(
    isInputEmpty: Boolean,
    onVoiceClick: () -> Unit,
    onSendClick: () -> Unit,
) {
    if (isInputEmpty) {
        IconButton(
            onClick = onVoiceClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardVoice,
                contentDescription = "Voice",
                tint = VerifAiColor.TextPrimary
            )
        }
    } else {
        IconButton(
            onClick = onSendClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = VerifAiColor.Primary
            )
        }
    }
}