package mp.verif_ai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.home.HomeScreen
import mp.verif_ai.presentation.screens.inbox.InboxScreen
import mp.verif_ai.presentation.screens.settings.SettingsScreen
import mp.verif_ai.presentation.viewmodel.SettingsViewModel as SettingsViewModel1

fun NavHostController.navigateToMain() {
    this.navigate(Screen.MainNav.Home.route) {
        popUpTo(Screen.Auth.route) { inclusive = true }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainNav.Home.route
    ) {
        // HomeScreen with necessary parameters
        composable(Screen.MainNav.Home.route) {
            HomeScreen(
                navController = navController,
                onCreateQuestion = { /* Handle question creation */ },
                onQuestionClick = { questionId -> /* Handle question click */ },
                onSeeMoreConversations = { /* Handle "See More Conversations" */ },
                onSeeMoreQuestions = { /* Handle "See More Questions" */ },
                onSeeMoreTrending = { /* Handle "See More Trending" */ }
            )
        }

        // InboxScreen with necessary parameters
        composable(Screen.MainNav.Inbox.Main.route) {
            InboxScreen(
                onQuestionClick = { questionId ->
                    // Handle question click logic here
                }
            )
        }

        // Settings Screen
        composable(Screen.MainNav.Settings.Main.route) {
            SettingsScreen(
                navController = navController,
                viewModel = SettingsViewModel1() // Provide the view model, potentially via DI
            )
        }
    }
}

