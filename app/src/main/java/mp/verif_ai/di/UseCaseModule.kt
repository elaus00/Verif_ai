package mp.verif_ai.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.UserRepository
import mp.verif_ai.domain.usecase.auth.SignInUseCase
import mp.verif_ai.domain.usecase.user.GetUserUseCase

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideSignInUseCase(
        authRepository: AuthRepository,
    ): SignInUseCase = SignInUseCase(authRepository)

    @Provides
    fun provideGetUserUseCase(
        userRepository: UserRepository
    ): GetUserUseCase = GetUserUseCase(userRepository)
}