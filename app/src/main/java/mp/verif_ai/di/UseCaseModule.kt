package mp.verif_ai.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.domain.repository.AnswerRepository
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.repository.QuestionRepository
import mp.verif_ai.domain.usecase.ClearAllNotificationsUseCase
import mp.verif_ai.domain.usecase.DeleteNotificationsUseCase
import mp.verif_ai.domain.usecase.GetNotificationsUseCase
import mp.verif_ai.domain.usecase.GetUnreadCountUseCase
import mp.verif_ai.domain.usecase.MarkAllNotificationsAsReadUseCase
import mp.verif_ai.domain.usecase.MarkNotificationsAsReadUseCase
import mp.verif_ai.domain.usecase.answer.AdoptAnswerUseCase
import mp.verif_ai.domain.usecase.answer.CreateAnswerUseCase
import mp.verif_ai.domain.usecase.answer.GetAnswerUseCase
import mp.verif_ai.domain.usecase.answer.GetAnswersForQuestionUseCase
import mp.verif_ai.domain.usecase.answer.GetExpertAnswersUseCase
import mp.verif_ai.domain.usecase.answer.UpdateAnswerStatusUseCase
import mp.verif_ai.domain.usecase.answer.UpdateAnswerUseCase
import mp.verif_ai.domain.usecase.question.CreateQuestionUseCase
import mp.verif_ai.domain.usecase.question.GetMyQuestionsUseCase
import mp.verif_ai.domain.usecase.question.GetQuestionUseCase
import mp.verif_ai.domain.usecase.question.GetTrendingQuestionsUseCase
import mp.verif_ai.domain.usecase.question.UpdateQuestionUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetNotificationsUseCase(
        inboxRepository: InboxRepository,
        authRepository: AuthRepository
    ): GetNotificationsUseCase = GetNotificationsUseCase(inboxRepository, authRepository)

    @Provides
    @Singleton
    fun provideDeleteNotificationsUseCase(
        inboxRepository: InboxRepository
    ): DeleteNotificationsUseCase = DeleteNotificationsUseCase(inboxRepository)

    @Provides
    @Singleton
    fun provideMarkNotificationsAsReadUseCase(
        inboxRepository: InboxRepository
    ): MarkNotificationsAsReadUseCase = MarkNotificationsAsReadUseCase(inboxRepository)

    @Provides
    @Singleton
    fun provideGetUnreadCountUseCase(
        inboxRepository: InboxRepository,
        authRepository: AuthRepository
    ): GetUnreadCountUseCase = GetUnreadCountUseCase(inboxRepository, authRepository)

    @Provides
    @Singleton
    fun provideMarkAllNotificationsAsReadUseCase(
        inboxRepository: InboxRepository
    ): MarkAllNotificationsAsReadUseCase = MarkAllNotificationsAsReadUseCase(inboxRepository)

    @Provides
    @Singleton
    fun provideClearAllNotificationsUseCase(
        inboxRepository: InboxRepository,
        authRepository: AuthRepository
    ): ClearAllNotificationsUseCase = ClearAllNotificationsUseCase(inboxRepository, authRepository)

    // 질문, 답변 관련 유즈케이스
    @Provides
    fun provideCreateQuestionUseCase(repository: QuestionRepository) =
        CreateQuestionUseCase(repository)

    @Provides
    fun provideGetQuestionUseCase(repository: QuestionRepository) =
        GetQuestionUseCase(repository)

    @Provides
    fun provideGetTrendingQuestionsUseCase(repository: QuestionRepository) =
        GetTrendingQuestionsUseCase(repository)

    @Provides
    fun provideGetMyQuestionsUseCase(repository: QuestionRepository) =
        GetMyQuestionsUseCase(repository)

    @Provides
    fun provideUpdateQuestionUseCase(repository: QuestionRepository) =
        UpdateQuestionUseCase(repository)

    // Answer UseCases
    @Provides
    fun provideCreateAnswerUseCase(repository: AnswerRepository) =
        CreateAnswerUseCase(repository)

    @Provides
    fun provideGetAnswerUseCase(repository: AnswerRepository) =
        GetAnswerUseCase(repository)

    @Provides
    fun provideGetAnswersForQuestionUseCase(repository: AnswerRepository) =
        GetAnswersForQuestionUseCase(repository)

    @Provides
    fun provideGetExpertAnswersUseCase(repository: AnswerRepository) =
        GetExpertAnswersUseCase(repository)

    @Provides
    fun provideUpdateAnswerUseCase(repository: AnswerRepository) =
        UpdateAnswerUseCase(repository)

    @Provides
    fun provideUpdateAnswerStatusUseCase(repository: AnswerRepository) =
        UpdateAnswerStatusUseCase(repository)

    @Provides
    fun provideAdoptAnswerUseCase(repository: AnswerRepository) =
        AdoptAnswerUseCase(repository)
}