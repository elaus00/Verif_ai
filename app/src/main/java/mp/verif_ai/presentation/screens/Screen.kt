package mp.verif_ai.presentation.screens

sealed class Screen(val route: String) {
    // Auth Flow
    sealed class Auth(route: String) : Screen(route) {
        data object OnBoarding : Auth("auth/onboarding")
        data object SignIn : Auth("auth/signin")
        data object SignUp : Auth("auth/signup")
        data object SignUpDetail : Auth("auth/signup/detail")
        data object Verification : Auth("auth/verification/{$ARG_EMAIL}") {
            fun createRoute(email: String) = "auth/verification/$email"
        }
        data object CertificationOnBoarding : Auth("auth/certification/onboarding/{$ARG_USER_ID}") {
            fun createRoute(userId: String) = "auth/certification/onboarding/$userId"
        }
        data object ExpertCertification : Auth("auth/expert/certification/{$ARG_USER_ID}") {
            fun createRoute(userId: String) = "auth/expert/certification/$userId"
        }
        data object ExpertSubmit : Auth("auth/expert/submit/{$ARG_USER_ID}") {
            fun createRoute(userId: String) = "auth/expert/submit/$userId"
        }

        companion object {
            const val route = "auth_graph"
        }
    }

    // Main Navigation (Bottom Nav)
    sealed class MainNav(route: String) : Screen(route) {
        companion object {
            const val route = "main_graph"
        }

        data object Home : MainNav("main/home")

        sealed class Question(route: String) : MainNav(route) {
            companion object {
                const val route = "main/question"
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
                const val route = "main/inbox"
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
                const val route = "main/settings"
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

        sealed class Prompt(route: String) : MainNav(route) {
            companion object {
                const val route = "main/prompt"
            }

            // 메인 프롬프트 화면
            data object Main : Prompt("main/prompt")

            // 프롬프트 기록/히스토리 화면
            data object History : Prompt("main/prompt/history")

            // 특정 프롬프트 대화 상세 화면
            data class Detail(val promptId: String) : Prompt("main/prompt/detail/$promptId") {
                companion object {
                    const val route = "main/prompt/detail/{$ARG_PROMPT_ID}"
                    fun createRoute(promptId: String) = "main/prompt/detail/$promptId"
                }
            }

            // 프롬프트 템플릿 선택 화면
            data object Templates : Prompt("main/prompt/templates")

            // 프롬프트 설정 화면 (API 키 설정 등)
            data object Settings : Prompt("main/prompt/settings")
        }
    }

    companion object {
        // Route Arguments
        const val ARG_QUESTION_ID = "questionId"
        const val ARG_EMAIL = "email"
        const val ARG_USER_ID = "userId"
        const val ARG_RETURN_ROUTE = "return"
        const val ARG_PROMPT_ID = "promptId"
    }
}