package mp.verif_ai.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.repository.InboxRepositoryImpl
import mp.verif_ai.data.repository.mock.MockInboxRepositoryImpl
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryModule {
//    @Binds
//    @Singleton
//    abstract fun bindInboxRepository(
//        inboxRepositoryImpl: InboxRepositoryImpl
//    ): InboxRepository
//}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideInboxRepository(): InboxRepository {
        return MockInboxRepositoryImpl()
    }
}