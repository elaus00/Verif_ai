package mp.verif_ai.presentation.screens.explore


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChips(
    categories: Map<String, List<String>>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedCategory by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        // Main categories
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = {
                        onCategorySelected("")
                        expandedCategory = null
                    },
                    label = { Text("Total") }
                )
            }

            items(categories.keys.toList()) { category ->
                FilterChip(
                    selected = expandedCategory == category,
                    onClick = {
                        expandedCategory = if (expandedCategory == category) null else category
                    },
                    label = { Text(category) }
                )
            }
        }

        // Subcategories for expanded category
        AnimatedVisibility(
            visible = expandedCategory != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            expandedCategory?.let { category ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(categories[category] ?: emptyList()) { subCategory ->
                        FilterChip(
                            selected = selectedCategory == subCategory,
                            onClick = { onCategorySelected(subCategory) },
                            label = { Text(subCategory) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ExpertHeader(
    expertId: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Surface(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(24.dp)
            )
        }

        // Name and Title
        Text(
            text = "Expert Name",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Field of Expertise",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}