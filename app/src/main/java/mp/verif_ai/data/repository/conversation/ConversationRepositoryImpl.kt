package mp.verif_ai.data.repository.conversation

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.data.util.LocalDataSource
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.repository.ConversationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val localDataSource: LocalDataSource,
    private val errorHandler: FirestoreErrorHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ConversationRepository {

    companion object {
        private const val TAG = "ConversationRepo"
        private const val CONVERSATIONS_COLLECTION = "conversations"
    }

    private val conversationsCollection = firestore.collection(CONVERSATIONS_COLLECTION)

    override suspend fun observeConversation(conversationId: String): Flow<Conversation> = callbackFlow {
        val listener = conversationsCollection.document(conversationId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing conversation", error)
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.data?.let { data ->
                    try {
                        val conversation = Conversation.fromMap(data + mapOf("id" to conversationId))
                        trySend(conversation)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing conversation", e)
                        close(e)
                    }
                }
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun sendMessage(conversationId: String, message: Message): Result<String> =
        errorHandler.runCatching {
            val conversationRef = conversationsCollection.document(conversationId)

            // 트랜잭션으로 메시지 추가 및 업데이트 처리
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(conversationRef)
                if (!snapshot.exists()) {
                    throw IllegalStateException("Conversation not found: $conversationId")
                }

                val currentMessages = snapshot.get("messages") as? List<Map<String, Any>> ?: emptyList()
                val updatedMessages = currentMessages + message.toMap()

                transaction.update(
                    conversationRef,
                    mapOf(
                        "messages" to updatedMessages,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
            }.await()

            message.id
        }

    override suspend fun getConversationHistory(
        userId: String,
        limit: Int,
        offset: Int
    ): Result<List<Conversation>> = errorHandler.runCatching {
        // 먼저 로컬에서 시도
        val localConversations = localDataSource.getConversationHistory(limit, offset)
        if (localConversations.isNotEmpty()) {
            return@runCatching localConversations
        }

        // 로컬에 없으면 Firestore에서 가져오기
        val remoteConversations = conversationsCollection
            .whereArrayContains("participantIds", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                try {
                    Conversation.fromMap(doc.data.orEmpty() + mapOf("id" to doc.id))
                } catch (e: Exception) {
                    null
                }
            }

        // 가져온 데이터 로컬에 저장
        remoteConversations.forEach { conversation ->
            localDataSource.saveConversation(conversation)
        }

        remoteConversations
    }

    override suspend fun getFullConversation(conversationId: String): Result<Conversation> =
        errorHandler.runCatching {
            val doc = conversationsCollection
                .document(conversationId)
                .get()
                .await()

            if (!doc.exists()) {
                throw IllegalStateException("Conversation not found: $conversationId")
            }

            Conversation.fromMap(doc.data.orEmpty() + mapOf("id" to doc.id))
        }

    override suspend fun createConversation(conversation: Conversation): Result<Unit> =
        errorHandler.runCatching {
            conversationsCollection
                .document(conversation.id)
                .set(conversation.toMap())
                .await()

            localDataSource.saveConversation(conversation)
        }

    override suspend fun updateConversation(conversation: Conversation): Result<Unit> =
        errorHandler.runCatching {
            conversationsCollection
                .document(conversation.id)
                .set(conversation.toMap())
                .await()

            localDataSource.saveConversation(conversation)
        }

    override suspend fun updateConversationStatus(
        conversationId: String,
        status: ConversationStatus
    ): Result<Unit> = errorHandler.runCatching {
        conversationsCollection
            .document(conversationId)
            .update(
                mapOf(
                    "status" to status.name,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()
    }
}