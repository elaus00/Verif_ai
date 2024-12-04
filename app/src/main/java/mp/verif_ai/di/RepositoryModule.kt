package mp.verif_ai.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import mp.verif_ai.data.auth.AuthRepositoryImpl
import mp.verif_ai.data.local.dao.ConversationDao
import mp.verif_ai.data.local.dao.MessageDao
import mp.verif_ai.data.local.dao.ParticipantDao
import mp.verif_ai.data.repository.conversation.ConversationRepositoryImpl
import mp.verif_ai.data.repository.inbox.InboxRepositoryImpl
import mp.verif_ai.data.repository.point.PointRepositoryImpl
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.repository.PassKeyRepository
import mp.verif_ai.domain.repository.PointRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    companion object {
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
        ): AuthRepository {
            return AuthRepositoryImpl(auth, firestore, passKeyRepository)
        }

        @Provides
        @Singleton
        fun provideInboxRepository(): InboxRepository = InboxRepositoryImpl()

        @Provides
        @Singleton
        fun provideApplicationContext(@ApplicationContext context: Context): Context {
            return context
        }

        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }

        @Provides
        @Singleton
        fun provideConversationRepository(
            firestore: FirebaseFirestore,
            storage: FirebaseStorage,
            conversationDao: ConversationDao,
            messageDao: MessageDao,
            participantDao: ParticipantDao,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): ConversationRepository = ConversationRepositoryImpl(
            firestore = firestore,
            storage = storage,
            conversationDao = conversationDao,
            messageDao = messageDao,
            participantDao = participantDao,
            scope = scope,
            dispatcher = dispatcher
        )

        @Provides
        @Singleton
        fun providePointRepository(
            firestore: FirebaseFirestore,
            authRepository: AuthRepository,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): PointRepository {
            return PointRepositoryImpl(firestore, authRepository, dispatcher)
        }
    }
}