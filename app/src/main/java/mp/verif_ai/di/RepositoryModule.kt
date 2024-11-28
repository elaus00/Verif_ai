package mp.verif_ai.di

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mp.verif_ai.data.auth.AuthRepositoryImpl
import mp.verif_ai.data.repository.InboxRepositoryImpl
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.repository.PassKeyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        passKeyRepository: PassKeyRepository,
//        activity: ComponentActivity
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore, passKeyRepository)
    }

    @Provides
    @Singleton
    fun provideInboxRepository(
    ): InboxRepository = InboxRepositoryImpl()

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}