package mp.verif_ai.presentation.screens.conversation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.presentation.screens.theme.VerifAiColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import mp.verif_ai.presentation.components.CustomDropdownMenu

@Composable
fun AIModelSelector(
    models: List<AIModel>,
    selectedModel: AIModel?,
    onModelSelect: (AIModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CustomDropdownMenu(
            items = models.map { it.displayName },
            selectedItem = selectedModel?.displayName ?: "AI 모델을 선택하세요",
            onItemSelected = { selectedName ->
                models.find { it.displayName == selectedName }?.let { onModelSelect(it) }
            },
            enabled = true,
            iconContent = {
                Icon(
                    imageVector = Icons.Default.Mode,
                    contentDescription = null,
                    tint = it, // CustomDropdownMenu에서 제공하는 적절한 tint color
                    modifier = Modifier.size(20.dp)
                )
            },
            itemContent = { model ->
                val currentModel = models.find { it.displayName == model }
                Column {
                    Text(
                        text = model,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    currentModel?.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = VerifAiColor.TextSecondary
                        )
                    }
                }
            }
        )
    }
}