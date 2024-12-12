package mp.verif_ai.data.repository.conversation

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.service.AIServiceFactory
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.repository.ResponseRepository
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.domain.util.dto.ConversationFirestoreDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val aiServiceFactory: AIServiceFactory,
    private val errorHandler: FirestoreErrorHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ResponseRepository {

    companion object {
        private const val TAG = "ResponseRepo"
    }

    private val conversationsCollection = firestore.collection("conversations")

    override suspend fun getAiResponse(
        model: AIModel,
        prompt: String
    ): Flow<String> = flow {
        Log.d(TAG, "Getting AI response for model: $model")

        val aiService = aiServiceFactory.getService(model)
        aiService.generateResponse(prompt, model)
            .catch { e ->
                Log.e(TAG, "Error in AI response", e)
                throw errorHandler.handleFirestoreError(e as Exception)
            }
            .collect { response ->
                emit(response)
            }
    }.flowOn(dispatcher)

    override suspend fun requestExpertReview(
        conversationId: String,
        points: Int
    ): Result<Unit> = errorHandler.runWithRetry {
        Log.d(TAG, "Requesting expert review for conversation: $conversationId")

        val conversationRef = conversationsCollection.document(conversationId)
        firestore.runTransaction { transaction ->
            val conversation = transaction.get(conversationRef)
                .toObject(ConversationFirestoreDto::class.java)
                ?: throw IllegalStateException("Conversation not found")

            transaction.update(
                conversationRef,
                mapOf(
                    "expertReviewRequested" to true,
                    "expertReviewRequestedAt" to FieldValue.serverTimestamp()
                )
            )
        }.await()

        Unit
    }

    override suspend fun getExpertReviews(
        conversationId: String
    ): Flow<List<ExpertReview>> = callbackFlow {
        Log.d(TAG, "Getting expert reviews for conversation: $conversationId")

        val listener = conversationsCollection
            .document(conversationId)
            .collection("expertReviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to expert reviews", error)
                    close(error)
                    return@addSnapshotListener
                }

                val reviews = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(ExpertReview::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting review document", e)
                        null
                    }
                } ?: emptyList()

                trySend(reviews)
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(dispatcher)
}