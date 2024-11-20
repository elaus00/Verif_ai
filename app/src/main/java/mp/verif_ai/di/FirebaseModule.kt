package mp.verif_ai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.firebase.repository.FirebaseAuthRepositoryImpl
import mp.verif_ai.data.firebase.repository.FirebaseUserRepositoryImpl
import mp.verif_ai.data.firebase.repository.FirebaseChatRepositoryImpl
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.PromptRepository
import mp.verif_ai.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = FirebaseAuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): UserRepository = FirebaseUserRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideChatRepository(
        functions: FirebaseFunctions,
        firestore: FirebaseFirestore
    ): PromptRepository = FirebaseChatRepositoryImpl(functions, firestore)
}