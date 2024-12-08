package mp.verif_ai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.OnBoardingScreen
import mp.verif_ai.presentation.screens.auth.SignInScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertOnboardingScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertSubmitScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertVerificationScreen
import mp.verif_ai.presentation.screens.auth.signup.EmailVerificationScreen
import mp.verif_ai.presentation.screens.auth.signup.SignUpFormScreen
import mp.verif_ai.presentation.screens.auth.signup.SignUpScreen
import mp.verif_ai.presentation.screens.conversation.ConversationDetailScreen
import mp.verif_ai.presentation.screens.explore.ExpertProfileScreen
import mp.verif_ai.presentation.screens.explore.ExploreScreen
import mp.verif_ai.presentation.screens.home.HomeScreen
import mp.verif_ai.presentation.screens.inbox.InboxScreen
import mp.verif_ai.presentation.screens.inbox.NotificationDetailScreen
import mp.verif_ai.presentation.screens.question.QuestionCreateScreen
import mp.verif_ai.presentation.screens.question.QuestionDetailScreen
import mp.verif_ai.presentation.screens.settings.*
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
        // OnBoarding
        composable(Screen.Auth.OnBoarding.route) {
            OnBoardingScreen(
                navController = navController,
            )
        }

        // Sign In
        composable(Screen.Auth.SignIn.route) {
            SignInScreen(
                modifier = Modifier,
                navController = navController
            )
        }

        // Sign Up Flow
        composable(Screen.Auth.SignUp.route) {
            SignUpScreen(
                modifier = Modifier,
                onNavigateToMain = navController::navigateToMain
            )
        }

        composable(Screen.Auth.SignUpDetail.route) {
            SignUpFormScreen(
                onSignUpComplete = { email ->
                    navController.navigate(Screen.Auth.Verification.createRoute(email))
                }
            )
        }

        composable(
            route = Screen.Auth.Verification.route,
            arguments = listOf(
                navArgument(Screen.ARG_EMAIL) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            EmailVerificationScreen(
                email = it.arguments?.getString(Screen.ARG_EMAIL),
                onVerificationComplete = { userId ->
                    navController.navigate(Screen.Auth.CertificationOnBoarding.createRoute(userId.toString())) {
                        popUpTo(Screen.Auth.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        // Expert Certification Flow
        composable(
            route = Screen.Auth.CertificationOnBoarding.route,
            arguments = listOf(
                navArgument(Screen.ARG_USER_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            ExpertOnboardingScreen(navController)
        }

        composable(
            route = Screen.Auth.ExpertCertification.route,
            arguments = listOf(
                navArgument(Screen.ARG_USER_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            ExpertVerificationScreen(
                userId = it.arguments?.getString(Screen.ARG_USER_ID) ?: "",
                navController = navController
            )
        }

        composable(
            route = Screen.Auth.ExpertSubmit.route,
            arguments = listOf(
                navArgument(Screen.ARG_USER_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            ExpertSubmitScreen(
                userId = it.arguments?.getString(Screen.ARG_USER_ID) ?: ""
            )
        }
    }
}

private fun NavGraphBuilder.mainNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Home.route,
        route = Screen.MainNav.route
    ) {
        // 1. Home Section
        homeNavigation(navController)

        // 2. Inbox Section
        inboxNavigation(navController)

        // 3. Explore Section
        exploreNavigation(navController)

        // 4. Settings Section
        settingsNavigation(navController)
    }
}


private fun NavGraphBuilder.homeNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Home.HomeScreen.route,
        route = Screen.MainNav.Home.route
    ) {
        composable(Screen.MainNav.Home.HomeScreen.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                modifier = Modifier
            )
        }

        composable(
            route = Screen.MainNav.Home.ConversationDetail.route,
            arguments = listOf(
                navArgument(Screen.ARG_CONVERSATION_ID) { type = NavType.StringType }
            )
        ) {
            ConversationDetailScreen(
                conversationId = it.arguments?.getString(Screen.ARG_CONVERSATION_ID) ?: ""
            )
        }

        composable(
            route = Screen.MainNav.Home.ExpertProfile.route,
            arguments = listOf(
                navArgument(Screen.ARG_EXPERT_ID) { type = NavType.StringType }
            )
        ) {
            ExpertProfileScreen(
                expertId = it.arguments?.getString(Screen.ARG_EXPERT_ID) ?: ""
            )
        }
    }
}

private fun NavGraphBuilder.inboxNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Inbox.InboxScreen.route,
        route = Screen.MainNav.Inbox.route
    ) {
        composable(Screen.MainNav.Inbox.InboxScreen.route) {
            InboxScreen(
                navController = navController,
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Inbox.QuestionDetail.createRoute(questionId))
                },
                onNotificationClick = { notificationId ->
                    navController.navigate(Screen.MainNav.Inbox.NotificationDetail.createRoute(notificationId))
                }
            )
        }

        composable(
            route = Screen.MainNav.Inbox.QuestionDetail.route,
            arguments = listOf(
                navArgument(Screen.ARG_QUESTION_ID) { type = NavType.StringType }
            )
        ) {
        }

        composable(
            route = Screen.MainNav.Inbox.NotificationDetail.route,
            arguments = listOf(
                navArgument(Screen.ARG_NOTIFICATION_ID) { type = NavType.StringType }
            )
        ) {
            NotificationDetailScreen(
                notificationId = it.arguments?.getString(Screen.ARG_NOTIFICATION_ID) ?: ""
            )
        }
    }
}

private fun NavGraphBuilder.exploreNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Explore.ExploreScreen.route,
        route = Screen.MainNav.Explore.route
    ) {
        composable(Screen.MainNav.Explore.ExploreScreen.route) {
            ExploreScreen(
                modifier = Modifier,
                navController = navController,
                onCreateQuestion = {
                    navController.navigate(Screen.MainNav.Explore.Create.route)
                },
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Explore.Detail.createRoute(questionId))
                },
            )
        }

        composable(Screen.MainNav.Explore.Create.route) {
            QuestionCreateScreen(
                onQuestionCreated = { questionId ->
                    navController.navigate(Screen.MainNav.Explore.Detail.createRoute(questionId)) {
                        popUpTo(Screen.MainNav.Explore.Create.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.MainNav.Explore.Detail.route,
            arguments = listOf(
                navArgument(Screen.ARG_QUESTION_ID) { type = NavType.StringType }
            )
        ) {
            QuestionDetailScreen(
                questionId = it.arguments?.getString(Screen.ARG_QUESTION_ID) ?: "",
                viewModel = hiltViewModel()
            )
        }
    }
}

private fun NavGraphBuilder.settingsNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Settings.SettingsScreen.route,
        route = Screen.MainNav.Settings.route
    ) {
        composable(Screen.MainNav.Settings.SettingsScreen.route) {
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

        // Profile Navigation
        composable(Screen.MainNav.Settings.Profile.View.route) {
            ProfileViewScreen(
                onEdit = {
                    navController.navigate(Screen.MainNav.Settings.Profile.Edit.route)
                }
            )
        }

        composable(Screen.MainNav.Settings.Profile.Edit.route) {
            ProfileEditScreen(
                onComplete = { navController.navigateUp() }
            )
        }

        // Payment Navigation
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
                            Screen.MainNav.Settings.Payment.Methods.route
                        )
                    )
                }
            )
        }

        // Notifications Settings
        composable(Screen.MainNav.Settings.Notifications.route) {
            NotificationSettingsScreen(
                onComplete = { navController.navigateUp() }
            )
        }
    }
}

fun NavController.navigateToMain() {
    navigate(Screen.MainNav.Home.HomeScreen.route) {
        popUpTo(Screen.Auth.route) {
            inclusive = true
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }
}