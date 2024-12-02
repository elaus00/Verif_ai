package mp.verif_ai.di

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.repository.auth.PassKeyRepositoryImpl
import mp.verif_ai.domain.repository.PassKeyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
data object PassKeyModule {

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager = CredentialManager.create(context)

    @Provides
    @Singleton
    fun providePassKeyRepository(
        impl: PassKeyRepositoryImpl
    ): PassKeyRepository = impl
}