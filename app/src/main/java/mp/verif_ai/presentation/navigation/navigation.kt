package mp.verif_ai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.OnBoardingScreen
import mp.verif_ai.presentation.screens.auth.VerificationScreen
import mp.verif_ai.presentation.screens.auth.SignInScreen
import mp.verif_ai.presentation.screens.auth.SignUpScreen
import mp.verif_ai.presentation.screens.home.HomeScreen
import mp.verif_ai.presentation.screens.home.question.QuestionDetailScreen
import mp.verif_ai.presentation.screens.inbox.InboxQuestionDetailScreen
import mp.verif_ai.presentation.screens.inbox.InboxScreen
import mp.verif_ai.presentation.screens.question.QuestionCreateScreen
import mp.verif_ai.presentation.screens.settings.SettingsScreen
import mp.verif_ai.presentation.screens.settings.notification.NotificationSettingsScreen
import mp.verif_ai.presentation.screens.settings.payment.PaymentMethodsScreen
import mp.verif_ai.presentation.screens.settings.payment.SubscriptionScreen
import mp.verif_ai.presentation.screens.settings.profile.ProfileEditScreen
import mp.verif_ai.presentation.screens.settings.profile.ProfileViewScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    isAuthenticated: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Screen.MainNav.route else Screen.Auth.route
    ) {
        authNavigation(navController)
        mainNavigation(navController)
    }
}

private fun NavGraphBuilder.authNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.Auth.OnBoarding.route,
        route = Screen.Auth.route
    ) {
        composable(Screen.Auth.OnBoarding.route) {
            OnBoardingScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.Auth.SignIn.route)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.Auth.SignUp.route)
                }
            )
        }

        composable(Screen.Auth.SignIn.route) {
            SignInScreen(
                modifier = Modifier,
                onSignInSuccess = { navController.navigateToMain() },
                onSignUpClick = { navController.navigate(Screen.Auth.SignUp.route) }
            )
        }
        composable(Screen.Auth.SignUp.route) {
            SignUpScreen(
                modifier = Modifier,
                navController = navController,
                onSignUpSuccess = { email ->
                    navController.navigate(Screen.Auth.Verification.createRoute(email.toString()))
                },
                onNavigateBack = {
                    navController.navigate(Screen.Auth.OnBoarding.route) {
                        // OnBoarding 화면으로 이동하면서 백스택에서 SignUp 화면 제거
                        popUpTo(Screen.Auth.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Auth.Verification.route,
            arguments = listOf(navArgument(Screen.ARG_EMAIL) { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(Screen.ARG_EMAIL)
            VerificationScreen(
                email = email,
                onVerified = { navController.navigateToMain() }
            )
        }
    }
}


private fun NavGraphBuilder.mainNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Home.route,
        route = "main_graph"
    ) {
        // Home
        composable(Screen.MainNav.Home.route) {
            HomeScreen(
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Question.Detail.createRoute(questionId))
                }
            )
        }

        // Questions
        composable(Screen.MainNav.Question.Create.route) {
            QuestionCreateScreen(
                onQuestionCreated = { questionId ->
                    navController.navigate(Screen.MainNav.Question.Detail.createRoute(questionId)) {
                        popUpTo(Screen.MainNav.Question.Create.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.MainNav.Question.Detail.route,
            arguments = listOf(navArgument(Screen.ARG_QUESTION_ID) { type = NavType.StringType })
        ) {
            QuestionDetailScreen(
                questionId = it.arguments?.getString(Screen.ARG_QUESTION_ID)
            )
        }

        // Inbox
        composable(Screen.MainNav.Inbox.Main.route) {
            InboxScreen(
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Inbox.QuestionDetail.createRoute(questionId))
                }
            )
        }
        composable(
            route = Screen.MainNav.Inbox.QuestionDetail.route,
            arguments = listOf(navArgument(Screen.ARG_QUESTION_ID) { type = NavType.StringType })
        ) {
            InboxQuestionDetailScreen(
                questionId = it.arguments?.getString(Screen.ARG_QUESTION_ID)
            )
        }

        // Settings
        settingsNavigation(navController)
    }
}

private fun NavGraphBuilder.settingsNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Settings.Main.route,
        route = "settings_graph"
    ) {
        composable(Screen.MainNav.Settings.Main.route) {
            SettingsScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.MainNav.Settings.Profile.View.route)
                },
                onNavigateToPayment = {
                    navController.navigate(Screen.MainNav.Settings.Payment.Subscription.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.MainNav.Settings.Notifications.route)
                }
            )
        }

        // Profile
        composable(Screen.MainNav.Settings.Profile.View.route) {
            ProfileViewScreen(
                onEdit = { navController.navigate(Screen.MainNav.Settings.Profile.Edit.route) }
            )
        }
        composable(Screen.MainNav.Settings.Profile.Edit.route) {
            ProfileEditScreen(
                onComplete = { navController.navigateUp() }
            )
        }

        // Payment & Subscription
        composable(Screen.MainNav.Settings.Payment.Subscription.route) {
            SubscriptionScreen(
                onManagePayment = {
                    navController.navigate(Screen.MainNav.Settings.Payment.Methods.route)
                }
            )
        }
        composable(Screen.MainNav.Settings.Payment.Methods.route) {
            PaymentMethodsScreen(
                onAddMethod = {
                    navController.navigate(
                        Screen.MainNav.Settings.Payment.AddMethod.createRoute(
                            Screen.MainNav.Settings.Payment.Methods.route))
                }
            )
        }

        // Notifications
        composable(Screen.MainNav.Settings.Notifications.route) {
            NotificationSettingsScreen(
                onComplete = { navController.navigateUp() }
            )
        }
    }
}

private fun NavHostController.navigateToMain() {
    navigate(Screen.MainNav.Home.route) {
        popUpTo(Screen.Auth.route) { inclusive = true }  // "auth_graph" 대신 mp.verif_ai.presentation.screens.Screen.Auth.route 사용
    }
}