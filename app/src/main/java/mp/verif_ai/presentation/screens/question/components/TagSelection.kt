package mp.verif_ai.presentation.screens.question.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelection(
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTags = remember { getAllTags() }

    Column(modifier = modifier) {
        Text(
            text = "Choose a tag",
            style = MaterialTheme.typography.titleMedium,
            color = VerifAiColor.TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allTags.forEach { tag ->
                FilterChip(
                    selected = tag in selectedTags,
                    onClick = { onTagSelected(tag) },
                    label = { Text(tag) },
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}

private fun getAllTags() = listOf(
    "Technology", "Science", "Mathematics", "Education", "Culture",
    "Economy", "Politics", "Society", "History", "Philosophy",
    "Health", "Medicine", "Sports", "Art", "Music",
    "Hobbies", "Travel", "Cooking", "Workplace", "Daily Life"
)
