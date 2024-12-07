package mp.verif_ai.data.repository.conversation

import android.util.Log
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.util.ConversationMapper
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.data.util.LocalDataSource
import mp.verif_ai.data.util.SyncManager
import mp.verif_ai.di.ApplicationScope
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.repository.ConversationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localDataSource: LocalDataSource,
    private val syncManager: SyncManager,
    private val errorHandler: FirestoreErrorHandler,
    private val conversationMapper: ConversationMapper,
    @ApplicationScope private val scope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ConversationRepository {
    private val conversationsCollection = firestore.collection("conversations")

    companion object {
        private const val TAG = "ConversationRepo"
        private const val MAX_RETRIES = 3
    }

    init {
        syncManager.startSync()
    }

    override suspend fun observeConversation(conversationId: String): Flow<Conversation> = callbackFlow {
        try {
            Log.d(TAG, "Starting conversation observation: $conversationId")

            // 로컬 데이터 먼저 로드
            localDataSource.getConversation(conversationId)?.let { localData ->
                Log.d(TAG, "Loaded initial data from local DB")
                trySend(localData)
            }

            // Firestore 구독
            val listener = conversationMapper.createConversationListener(
                conversationId = conversationId,
                collection = conversationsCollection
            ) { conversation ->
                trySend(conversation)
            }

            syncManager.registerListener(listener)

            // 코루틴 컨텍스트를 사용하여 suspension function 호출
            awaitClose {
                Log.d(TAG, "Cleaning up conversation listener")
                scope.launch(dispatcher) {
                    syncManager.removeListener(listener)
                }
            }
        } catch (e: Exception) {
            val error = errorHandler.handleFirestoreError(e)
            Log.e(TAG, "Error observing conversation", error)
            close(error)
        }
    }.flowOn(dispatcher)

    override suspend fun sendMessage(
        conversationId: String,
        message: Message
    ): Result<String> = errorHandler.runWithRetry(
        times = MAX_RETRIES,
        operation = {
            Log.d(TAG, "Sending message: ${message.id}")
            saveMessageToFirestore(conversationId, message).also {
                localDataSource.saveMessage(message, conversationId)
            }
        }
    )

    override suspend fun getConversationHistory(
        userId: String,
        limit: Int,
        offset: Int
    ): Result<List<Conversation>> = errorHandler.runWithRetry {
        Log.d(TAG, "Getting conversation history for user: $userId")

        // 로컬 데이터 먼저 시도
        val localConversations = localDataSource.getConversationHistory(limit, offset)
        if (localConversations.isNotEmpty()) {
            Log.d(TAG, "Returning ${localConversations.size} conversations from local DB")
            return@runWithRetry localConversations
        }

        // Firestore에서 조회
        val firestoreConversations = conversationMapper.getFirestoreConversations(
            collection = conversationsCollection,
            userId = userId,
            limit = limit
        )

        // 로컬 DB에 동기화
        firestoreConversations.forEach { conversation ->
            localDataSource.saveConversation(conversation)
        }

        Log.d(TAG, "Returning ${firestoreConversations.size} conversations from Firestore")
        firestoreConversations
    }

    override suspend fun getFullConversation(
        conversationId: String
    ): Result<Conversation> = errorHandler.runWithRetry {
        Log.d(TAG, "Getting full conversation: $conversationId")

        localDataSource.getConversation(conversationId)
            ?: conversationMapper.getFirestoreConversation(
                collection = conversationsCollection,
                conversationId = conversationId
            ).also {
                localDataSource.saveConversation(it)
            }
    }

    private suspend fun saveMessageToFirestore(
        conversationId: String,
        message: Message
    ): String {
        val messageRef = conversationsCollection
            .document(conversationId)
            .collection("messages")
            .document(message.id)

        messageRef.set(message.toMap()).await()
        return message.id
    }
}
