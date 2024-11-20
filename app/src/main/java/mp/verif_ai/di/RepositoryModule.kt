package mp.verif_ai.di

import com.google.firebase.functions.FirebaseFunctions
import mp.verif_ai.data.repository.PromptRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.repository.InboxRepositoryImpl
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.repository.PromptRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindInboxRepository(
        inboxRepositoryImpl: InboxRepositoryImpl
    ): InboxRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ProvidesRepositoryModule {
    @Provides
    @Singleton
    fun providePromptRepository(
        firebaseFunctions: FirebaseFunctions
    ): PromptRepository {
        return PromptRepositoryImpl(firebaseFunctions)
    }
}