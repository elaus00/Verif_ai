package mp.verif_ai.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.usecase.ClearAllNotificationsUseCase
import mp.verif_ai.domain.usecase.DeleteNotificationsUseCase
import mp.verif_ai.domain.usecase.GetNotificationsUseCase
import mp.verif_ai.domain.usecase.GetUnreadCountUseCase
import mp.verif_ai.domain.usecase.MarkAllNotificationsAsReadUseCase
import mp.verif_ai.domain.usecase.MarkNotificationsAsReadUseCase
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
}