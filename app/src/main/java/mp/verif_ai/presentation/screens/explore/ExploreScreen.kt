package mp.verif_ai.presentation.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import mp.verif_ai.domain.model.expert.ExpertFields
import mp.verif_ai.presentation.navigation.AppBottomNavigation
import mp.verif_ai.presentation.screens.components.CustomSnackbar
import mp.verif_ai.presentation.screens.explore.components.ExploreSearchBar
import mp.verif_ai.presentation.screens.question.QuestionUiState
import mp.verif_ai.presentation.screens.question.QuestionViewModel
import mp.verif_ai.presentation.screens.question.components.ExploreQuestionList
import mp.verif_ai.presentation.screens.theme.VerifAiColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    onCreateQuestion: () -> Unit,
    onQuestionClick: (String) -> Unit,
    viewModel: QuestionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = ExpertFields.getFieldsByCategory()
    val snackbarHostState = remember { SnackbarHostState() }

    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(vertical = 8.dp),
                title = {
                    Text(
                        text = "Explore",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = VerifAiColor.Navy.Dark
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController)
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                CustomSnackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding().times(0.64f),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            // Search Bar
            ExploreSearchBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 8.dp)
            )

            // Categories
            CategoryChips(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    viewModel.onCategorySelected(category)
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Question List with Error Handling
            when (uiState) {
                is QuestionUiState.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                is QuestionUiState.Success -> {
                    ExploreQuestionList(
                        onQuestionClick = onQuestionClick,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                is QuestionUiState.Error -> {
                    LaunchedEffect(uiState) {
                        snackbarHostState.showSnackbar(
                            message = (uiState as QuestionUiState.Error).message,
                            actionLabel = "Dismiss"
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = VerifAiColor.Navy.Deep,
            modifier = Modifier.size(48.dp)
        )
    }
}