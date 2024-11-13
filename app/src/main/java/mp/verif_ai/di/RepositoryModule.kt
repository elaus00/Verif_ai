package mp.verif_ai.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.repository.InboxRepositoryImpl
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindInboxRepository(
        inboxRepositoryImpl: InboxRepositoryImpl
    ): InboxRepository
}