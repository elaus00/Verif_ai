package mp.verif_ai.presentation.screens.question.comment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Input a comment...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = VerifAiColor.TextTertiary
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerifAiColor.Navy.Deep,
                    unfocusedBorderColor = VerifAiColor.DividerColor,
                    cursorColor = VerifAiColor.Navy.Deep
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(8.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Submit Button
            IconButton(
                onClick = onSubmit,
                enabled = value.isNotBlank(),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank())
                        VerifAiColor.Navy.Deep
                    else
                        VerifAiColor.TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Dismiss Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = VerifAiColor.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}