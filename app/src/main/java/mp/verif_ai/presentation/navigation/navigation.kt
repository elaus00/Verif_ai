package mp.verif_ai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import mp.verif_ai.presentation.screens.Screen
import mp.verif_ai.presentation.screens.auth.signup.EmailVerificationScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertOnboardingScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertSubmitScreen
import mp.verif_ai.presentation.screens.auth.expertsignup.ExpertVerificationScreen
import mp.verif_ai.presentation.screens.auth.OnBoardingScreen
import mp.verif_ai.presentation.screens.auth.SignInScreen
import mp.verif_ai.presentation.screens.auth.signup.SignUpFormScreen
import mp.verif_ai.presentation.screens.auth.signup.SignUpScreen
import mp.verif_ai.presentation.screens.home.HomeScreen
import mp.verif_ai.presentation.screens.home.question.QuestionDetailScreen
import mp.verif_ai.presentation.screens.inbox.InboxQuestionDetailScreen
import mp.verif_ai.presentation.screens.inbox.InboxScreen
import mp.verif_ai.presentation.screens.prompt.PromptDetailScreen
import mp.verif_ai.presentation.screens.prompt.PromptHistoryScreen
import mp.verif_ai.presentation.screens.prompt.PromptScreen
import mp.verif_ai.presentation.screens.prompt.PromptSettingsScreen
import mp.verif_ai.presentation.screens.prompt.PromptTemplatesScreen
import mp.verif_ai.presentation.screens.question.QuestionCreateScreen
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
        // Home
        composable(Screen.MainNav.Home.route) {
            HomeScreen(
                navController = navController,
                onQuestionClick = { questionId ->
                    navController.navigate(Screen.MainNav.Question.Detail.createRoute(questionId))
                },
                onCreateQuestion = {
                    navController.navigate(Screen.MainNav.Question.Create.route)
                },
                onSeeMoreQuestions = {
                    // Implement see more questions navigation
                },
                onSeeMoreConversations = {
                    // Implement see more conversations navigation
                },
                onSeeMoreTrending = {
                    // Implement see more trending navigation
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
                onNotificationClick = { questionId ->
                    navController.navigate(Screen.MainNav.Inbox.QuestionDetail.createRoute(questionId))
                },
                navController = navController
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

        promptNavigation(navController)
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
                            Screen.MainNav.Settings.Payment.Methods.route
                        )
                    )
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

private fun NavGraphBuilder.promptNavigation(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainNav.Prompt.Main.route,
        route = "prompt_navigation_graph"  // 변경: 고유한 그래프 route 사용
    ) {
        // 메인 프롬프트 화면
        composable(Screen.MainNav.Prompt.Main.route) {
            PromptScreen()
        }

        // 프롬프트 히스토리 화면
        composable(Screen.MainNav.Prompt.History.route) {
            PromptHistoryScreen(
                onPromptClick = { promptId ->
                    navController.navigate(Screen.MainNav.Prompt.Detail.createRoute(promptId))
                }
            )
        }

        // 프롬프트 상세 화면
        composable(
            route = Screen.MainNav.Prompt.Detail.route,
            arguments = listOf(
                navArgument(Screen.ARG_PROMPT_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            PromptDetailScreen(
                promptId = it.arguments?.getString(Screen.ARG_PROMPT_ID) ?: ""
            )
        }

        // 프롬프트 템플릿 화면
        composable(Screen.MainNav.Prompt.Templates.route) {
            PromptTemplatesScreen(
                onTemplateSelect = { templateId ->
                    navController.navigateUp()
                }
            )
        }

        // 프롬프트 설정 화면
        composable(Screen.MainNav.Prompt.Settings.route) {
            PromptSettingsScreen(
                onComplete = {
                    navController.navigateUp()
                }
            )
        }
    }
}

fun NavController.navigateToMain() {
    navigate(Screen.MainNav.Home.route) {
        // Auth 관련 백스택을 모두 제거하여 뒤로가기 시 Auth 화면으로 돌아가지 않도록 함
        popUpTo(Screen.Auth.route) {
            inclusive = true
            saveState = false
        }
        launchSingleTop = true
        // Home 화면의 상태를 복원하지 않고 새로 시작
        restoreState = false
    }
}