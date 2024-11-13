package mp.verif_ai.presentation.screens

sealed class Screen(val route: String) {
    // Main Navigation Routes
    sealed class MainNav(route: String) : Screen(route) {
        data object Home : MainNav("main/home")
        data object Inbox : MainNav("main/inbox")
        data object Explore : MainNav("main/explore")
        data object Settings : MainNav("main/settings")
    }

    // Onboarding Flow
    sealed class Onboarding(route: String) : Screen(route) {
        data object Welcome : Onboarding("onboarding/welcome")
        data object Features : Onboarding("onboarding/features")
        data object PersonalizeExperience : Onboarding("onboarding/personalize")
    }

    // Authentication Flow
    sealed class Auth(route: String) : Screen(route) {
        data object SignIn : Auth("auth/signin")
        data object SignUp : Auth("auth/signup")
        data object EmailVerification : Auth("auth/email-verification/{$ARG_EMAIL}")
        data object PhoneVerification : Auth("auth/phone-verification/{$ARG_PHONE}")

        sealed class Expert(route: String) : Auth(route) {
            data object Auth : Expert("auth/expert-auth")
            data object Verification : Expert("auth/expert-verification/{$ARG_EXPERT_ID}")

            companion object {
                fun createVerificationRoute(expertId: String) = "auth/expert-verification/$expertId"
            }
        }

        companion object {
            fun createEmailVerificationRoute(email: String) = "auth/email-verification/$email"
            fun createPhoneVerificationRoute(phone: String) = "auth/phone-verification/$phone"
        }
    }

    // Question Flow
    sealed class Question(route: String) : Screen(route) {
        sealed class Create(route: String) : Question(route) {
            data object Text : Create("question/create/text")
            data object Upload : Create("question/upload")

            sealed class AI(route: String) : Create(route) {
                data object ChatGPT : AI("question/create/chatgpt")
                data object Claude : AI("question/create/claude")
            }
        }

        sealed class Browse(route: String) : Question(route) {
            data object List : Browse("question/list")
            data object Trending : Browse("question/trending")
            data object Recent : Browse("question/recent")
        }

        data class Detail(val questionId: String) : Question("question/detail/$questionId") {
            companion object {
                const val route = "question/detail/{$ARG_QUESTION_ID}"
                fun createRoute(questionId: String) = "question/detail/$questionId"
            }
        }
    }

    // Inbox Flow
    sealed class Inbox(route: String) : Screen(route) {
        sealed class MyContent(route: String) : Inbox(route) {
            data object Questions : MyContent("inbox/questions")
            data object Answers : MyContent("inbox/answers")
            data object Interactions : MyContent("inbox/interactions")
        }

        sealed class Messages(route: String) : Inbox(route) {
            data object List : Messages("inbox/messages")
            data class Detail(val messageId: String) : Messages("inbox/message/$messageId") {
                companion object {
                    const val route = "inbox/message/{$ARG_MESSAGE_ID}"
                    fun createRoute(messageId: String) = "inbox/message/$messageId"
                }
            }
        }
    }

    // Answer Flow
    sealed class Answer(route: String) : Screen(route) {
        data object List : Answer("answer/list")
        data object Dispute : Answer("answer/dispute")

        data class Create(val questionId: String) : Answer("answer/create/$questionId") {
            companion object {
                const val route = "answer/create/{$ARG_QUESTION_ID}"
                fun createRoute(questionId: String) = "answer/create/$questionId"
            }
        }

        data class Detail(val answerId: String) : Answer("answer/detail/$answerId") {
            companion object {
                const val route = "answer/detail/{$ARG_ANSWER_ID}"
                fun createRoute(answerId: String) = "answer/detail/$answerId"
            }
        }
    }

    // Explore Flow
    sealed class Explore(route: String) : Screen(route) {
        data object Categories : Explore("explore/categories")
        data object Trending : Explore("explore/trending")
        data object Experts : Explore("explore/experts")
        data object Search : Explore("explore/search")

        data class CategoryDetail(val categoryId: String) : Explore("explore/category/$categoryId") {
            companion object {
                const val route = "explore/category/{$ARG_CATEGORY_ID}"
                fun createRoute(categoryId: String) = "explore/category/$categoryId"
            }
        }
    }

    // Settings Flow
    sealed class Settings(route: String) : Screen(route) {
        sealed class Profile(route: String) : Settings(route) {
            data object View : Profile("settings/profile")
            data object Edit : Profile("settings/profile/edit")
            data object Expert : Profile("settings/profile/expert")
        }

        sealed class Preferences(route: String) : Settings(route) {
            data object Theme : Preferences("settings/theme")
            data object Language : Preferences("settings/language")
            data object Notification : Preferences("settings/notification")
        }

        sealed class Security(route: String) : Settings(route) {
            data object Main : Security("settings/security")
            data object Privacy : Security("settings/privacy")
            data object Password : Security("settings/security/password")
        }

        sealed class Payment(route: String) : Settings(route) {
            data object History : Payment("settings/payment/history")
            data object Methods : Payment("settings/payment/methods")
            data object Subscription : Payment("settings/payment/subscription")
            data object AddMethod : Payment("settings/payment/methods/add")
        }
    }

    companion object {
        // Arguments used in routes
        const val ARG_QUESTION_ID = "questionId"
        const val ARG_ANSWER_ID = "answerId"
        const val ARG_NOTIFICATION_ID = "notificationId"
        const val ARG_EXPERT_ID = "expertId"
        const val ARG_MESSAGE_ID = "messageId"
        const val ARG_EMAIL = "email"
        const val ARG_PHONE = "phone"
        const val ARG_CATEGORY_ID = "categoryId"
    }
}