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
    val allTags = remember { getAllTags() }  // 태그 목록을 가져오는 함수

    Column(modifier = modifier) {
        Text(
            text = "태그 선택",
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
    "기술", "과학", "수학", "교육", "문화",
    "경제", "정치", "사회", "역사", "철학",
    "건강", "의학", "스포츠", "예술", "음악",
    "취미", "여행", "요리", "직장", "일상"
)