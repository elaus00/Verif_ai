package mp.verif_ai.di

import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
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
import mp.verif_ai.data.repository.answer.AnswerRepositoryImpl
import mp.verif_ai.data.repository.conversation.ConversationRepositoryImpl
import mp.verif_ai.data.repository.conversation.MediaRepositoryImpl
import mp.verif_ai.data.repository.conversation.ResponseRepositoryImpl
import mp.verif_ai.data.repository.inbox.InboxRepositoryImpl
import mp.verif_ai.data.repository.point.PointRepositoryImpl
import mp.verif_ai.data.repository.question.CommentRepositoryImpl
import mp.verif_ai.data.repository.question.QuestionRepositoryImpl
import mp.verif_ai.data.service.AIServiceFactory
import mp.verif_ai.data.util.ConversationMapper
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.data.util.LocalDataSource
import mp.verif_ai.data.util.SyncManager
import mp.verif_ai.domain.repository.AnswerRepository
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.CommentRepository
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.repository.MediaRepository
import mp.verif_ai.domain.repository.PassKeyRepository
import mp.verif_ai.domain.repository.PointRepository
import mp.verif_ai.domain.repository.QuestionRepository
import mp.verif_ai.domain.repository.ResponseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInboxRepository(
        inboxRepositoryImpl: InboxRepositoryImpl
    ): InboxRepository

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
        fun providePointRepository(
            firestore: FirebaseFirestore,
            authRepository: AuthRepository,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): PointRepository = PointRepositoryImpl(firestore, authRepository, dispatcher)


        @Provides
        @Singleton
        fun provideConversationMapper(): ConversationMapper = ConversationMapper()

        @Provides
        @Singleton
        fun provideSyncManager(
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): SyncManager = SyncManager(scope, dispatcher)

        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context
        ): WorkManager {
            return WorkManager.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideFirestoreErrorHandler(): FirestoreErrorHandler = FirestoreErrorHandler()

        @Provides
        @Singleton
        fun provideLocalDataSource(
            conversationDao: ConversationDao,
            messageDao: MessageDao,
            participantDao: ParticipantDao,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): LocalDataSource =
            LocalDataSource(conversationDao, messageDao, participantDao, dispatcher)

        @Provides
        @Singleton
        fun provideConversationRepository(
            firestore: FirebaseFirestore,
            localDataSource: LocalDataSource,
            errorHandler: FirestoreErrorHandler,
            @ApplicationScope scope: CoroutineScope,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): ConversationRepository = ConversationRepositoryImpl(
            firestore,
            localDataSource,
            errorHandler,
            dispatcher
        )

        @Provides
        @Singleton
        fun provideMediaRepository(
            storage: FirebaseStorage,
            errorHandler: FirestoreErrorHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): MediaRepository = MediaRepositoryImpl(storage, errorHandler)

        @Provides
        @Singleton
        fun provideResponseRepository(
            firestore: FirebaseFirestore,
            aiServiceFactory: AIServiceFactory,
            errorHandler: FirestoreErrorHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): ResponseRepository = ResponseRepositoryImpl(
            firestore,
            aiServiceFactory,
            errorHandler,
            dispatcher
        )

        @Provides
        @Singleton
        fun provideQuestionRepository(
            firestore: FirebaseFirestore,
            errorHandler: FirestoreErrorHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): QuestionRepository = QuestionRepositoryImpl(
            firestore,
            errorHandler,
            dispatcher
        )

        @Provides
        @Singleton
        fun provideAnswerRepository(
            firestore: FirebaseFirestore,
            errorHandler: FirestoreErrorHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): AnswerRepository {
            return AnswerRepositoryImpl(firestore, errorHandler, dispatcher)
        }

        @Provides
        @Singleton
        fun provideCommentRepository(
            firestore: FirebaseFirestore,
            errorHandler: FirestoreErrorHandler,
            @IoDispatcher dispatcher: CoroutineDispatcher
        ): CommentRepository {
            return CommentRepositoryImpl(firestore, errorHandler, dispatcher)
        }
    }
}