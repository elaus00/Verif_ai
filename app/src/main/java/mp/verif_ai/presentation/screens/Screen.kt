package mp.verif_ai.presentation.screens

sealed class Screen(val route: String) {
    // Auth Flow
    sealed class Auth(route: String) : Screen(route) {
        data object OnBoarding : Auth("auth/onboarding")  // 변경 없음
        data object SignIn : Auth("auth/signin")          // 변경 없음
        data object SignUp : Auth("auth/signup")          // "auth/signup" -> "signup"
        data object Verification : Auth("verification/{$ARG_EMAIL}") {  // "auth/verification/" -> "verification/"
            fun createRoute(email: String) = "verification/$email"
        }

        companion object {
            const val route = "auth"  // 추가: Auth Navigation Graph의 route
        }
    }

    // Main Navigation (Bottom Nav)
    sealed class MainNav(route: String) : Screen(route) {
        companion object {
            const val route = "main"  // 추가: Main Navigation Graph의 route
        }

        data object Home : MainNav("main/home")  // "home" -> "main/home" (명확한 계층 구조)

        sealed class Question(route: String) : MainNav(route) {
            companion object {
                const val route = "main/question"  // 추가: Question Navigation Graph의 route
            }

            data object Create : Question("main/question/create")
            data class Detail(val questionId: String) : Question("main/question/detail/$questionId") {
                companion object {
                    const val route = "main/question/detail/{$ARG_QUESTION_ID}"
                    fun createRoute(questionId: String) = "main/question/detail/$questionId"
                }
            }
        }

        sealed class Inbox(route: String) : MainNav(route) {
            companion object {
                const val route = "main/inbox"  // 추가: Inbox Navigation Graph의 route
            }

            data object Main : Inbox("main/inbox")
            data class QuestionDetail(val questionId: String) : Inbox("main/inbox/question/$questionId") {
                companion object {
                    const val route = "main/inbox/question/{$ARG_QUESTION_ID}"
                    fun createRoute(questionId: String) = "main/inbox/question/$questionId"
                }
            }
        }

        sealed class Settings(route: String) : MainNav(route) {
            companion object {
                const val route = "main/settings"  // 추가: Settings Navigation Graph의 route
            }

            data object Main : Settings("main/settings")

            sealed class Profile(route: String) : Settings(route) {
                data object View : Profile("main/settings/profile")
                data object Edit : Profile("main/settings/profile/edit")
            }

            sealed class Payment(route: String) : Settings(route) {
                data object Subscription : Payment("main/settings/payment/subscription")
                data object Methods : Payment("main/settings/payment/methods")
                data object AddMethod : Payment("main/settings/payment/methods/add") {
                    fun createRoute(returnRoute: String) =
                        "main/settings/payment/methods/add?return=$returnRoute"
                }
            }

            data object Notifications : Settings("main/settings/notifications")
        }
    }

    companion object {
        // Route Arguments
        const val ARG_QUESTION_ID = "questionId"
        const val ARG_EMAIL = "email"
        const val ARG_RETURN_ROUTE = "return"
    }
}