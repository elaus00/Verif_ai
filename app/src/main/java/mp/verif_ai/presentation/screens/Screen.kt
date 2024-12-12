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

        sealed class Home(route: String) : MainNav(route) {
            companion object {
                const val route = "main/home_nav"  // 네비게이션 그래프용 route
            }

            data object HomeScreen : Home("main/home_screen")  // 실제 홈 화면용 route
            data object ConversationScreen : Home("main/home/conversation") // 대화 화면용 route

            data class ConversationDetail(val conversationId: String) :
                Home("main/home/conversation/$conversationId") {
                companion object {
                    const val route = "main/home/conversation/{$ARG_CONVERSATION_ID}"
                    fun createRoute(conversationId: String) =
                        "main/home/conversation/$conversationId"
                }
            }

            data class ExpertProfile(val expertId: String) :
                Home("main/home/expert/$expertId") {
                companion object {
                    const val route = "main/home/expert/{$ARG_EXPERT_ID}"
                    fun createRoute(expertId: String) =
                        "main/home/expert/$expertId"
                }
            }
        }

        // 2. Inbox Section
        sealed class Inbox(route: String) : MainNav(route) {
            companion object {
                const val route = "main/inbox_nav"  // 네비게이션 그래프용 route
            }

            data object InboxScreen : Inbox("main/inbox_screen")  // 실제 inbox 화면용 route
            data class QuestionDetail(val questionId: String) :
                Inbox("main/inbox/question/$questionId") {
                companion object {
                    const val route = "main/inbox/question/{$ARG_QUESTION_ID}"
                    fun createRoute(questionId: String) =
                        "main/inbox/question/$questionId"
                }
            }

            data class NotificationDetail(val notificationId: String) :
                Inbox("main/inbox/notification/$notificationId") {
                companion object {
                    const val route = "main/inbox/notification/{$ARG_NOTIFICATION_ID}"
                    fun createRoute(notificationId: String) =
                        "main/inbox/notification/$notificationId"
                }
            }
        }

        // 3. Explore Section (Question Creation & Browse)
        sealed class Explore(route: String) : MainNav(route) {
            companion object {
                const val route = "main/explore_nav"
            }

            data object ExploreScreen : Explore("main/explore_screen")

            // Question 관련 route들
            sealed class Question(route: String) : Explore(route) {
                data object Create : Question("main/explore/question/create")
                data object List : Question("main/explore/question/list")
                data object Trending : Question("main/explore/question/trending")
                data object QuestionScreen : Question("main/explore/question/view")

                data class Detail(val questionId: String) :
                    Question("main/explore/question/detail/$questionId") {
                    companion object {
                        const val route = "main/explore/question/detail/{$ARG_QUESTION_ID}"
                        fun createRoute(questionId: String) =
                            "main/explore/question/detail/$questionId"
                    }
                }

                data class CreateAnswer(val questionId: String) :
                    Question("main/explore/question/answer/create/$questionId") {
                    companion object {
                        const val route = "main/explore/question/answer/create/{$ARG_QUESTION_ID}"
                        fun createRoute(questionId: String) =
                            "main/explore/question/answer/create/$questionId"
                    }
                }

                data class AnswerDetail(val questionId: String, val answerId: String) :
                    Question("main/explore/question/detail/$questionId/answer/$answerId") {
                    companion object {
                        const val route = "main/explore/question/detail/{$ARG_QUESTION_ID}/answer/{$ARG_ANSWER_ID}"
                        fun createRoute(questionId: String, answerId: String) =
                            "main/explore/question/detail/$questionId/answer/$answerId"
                    }
                }

                companion object {
                    const val ARG_ANSWER_ID = "answerId"
                }
            }
        }

        // 4. Settings Section
        sealed class Settings(route: String) : MainNav(route) {
            companion object {
                const val route = "main/settings_nav"  // 네비게이션 그래프용 route
            }

            data object SettingsScreen : Settings("main/settings_screen")  // 실제 settings 화면용 route

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
        const val ARG_USER_ID = "userId"
        const val ARG_RETURN_ROUTE = "return"
        const val ARG_CONVERSATION_ID = "conversationId"
        const val ARG_EXPERT_ID = "expertId"
        const val ARG_NOTIFICATION_ID = "notificationId"
    }
}