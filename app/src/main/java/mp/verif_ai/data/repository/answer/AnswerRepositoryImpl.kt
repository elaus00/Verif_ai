package mp.verif_ai.data.repository.answer

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.answer.AnswerStatus
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.repository.AnswerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnswerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val errorHandler: FirestoreErrorHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : AnswerRepository {

    companion object {
        private const val TAG = "AnswerRepo"
        private const val ANSWERS_COLLECTION = "answers"
        private const val QUESTIONS_COLLECTION = "questions"
    }

    private val answersCollection = firestore.collection(ANSWERS_COLLECTION)
    private val questionsCollection = firestore.collection(QUESTIONS_COLLECTION)

    override suspend fun createAnswer(answer: Answer): Result<String> = withContext(dispatcher) {
        try {
            val answerData = answer.toMap().plus(
                mapOf(
                    "createdAt" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                )
            )

            val documentRef = answersCollection.document()
            documentRef.set(answerData).await()

            // 질문의 답변 수 업데이트
            questionsCollection.document(answer.questionId)
                .update("commentCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating answer", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun getAnswer(answerId: String): Result<Answer> = withContext(dispatcher) {
        try {
            val snapshot = answersCollection.document(answerId).get().await()

            if (!snapshot.exists()) {
                return@withContext Result.failure(NoSuchElementException("Answer not found with id: $answerId"))
            }

            val answerMap = snapshot.data?.plus(mapOf("id" to snapshot.id)) ?: emptyMap()
            Result.success(Answer.fromMap(answerMap))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting answer", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun getAnswersForQuestion(questionId: String): Flow<List<Answer>> = callbackFlow {
        val listener = answersCollection
            .whereEqualTo("questionId", questionId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting answers for question", error)
                    close(error)
                    return@addSnapshotListener
                }

                val answers = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Answer.fromMap(doc.data?.plus(mapOf("id" to doc.id)) ?: emptyMap())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting answer document", e)
                        null
                    }
                } ?: emptyList()

                trySend(answers)
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun getExpertAnswers(
        expertId: String,
        limit: Int
    ): Flow<List<Answer>> = callbackFlow {
        val listener = answersCollection
            .whereEqualTo("expertId", expertId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting expert answers", error)
                    close(error)
                    return@addSnapshotListener
                }

                val answers = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Answer.fromMap(doc.data?.plus(mapOf("id" to doc.id)) ?: emptyMap())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting answer document", e)
                        null
                    }
                } ?: emptyList()

                trySend(answers)
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun updateAnswer(answer: Answer): Result<Unit> = withContext(dispatcher) {
        try {
            val answerData = answer.toMap().plus(
                mapOf("updatedAt" to Timestamp.now())
            )

            answersCollection.document(answer.id)
                .update(answerData as Map<String, Any>)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating answer", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun updateAnswerStatus(
        answerId: String,
        status: AnswerStatus
    ): Result<Unit> = withContext(dispatcher) {
        try {
            answersCollection.document(answerId)
                .update(
                    mapOf(
                        "status" to status.name,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating answer status", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun adoptAnswer(
        answerId: String,
        questionId: String
    ): Result<Unit> = withContext(dispatcher) {
        try {
            firestore.runTransaction { transaction ->
                val answerRef = answersCollection.document(answerId)
                val questionRef = questionsCollection.document(questionId)

                // 답변 상태 업데이트
                transaction.update(
                    answerRef,
                    mapOf(
                        "isAdopted" to true,
                        "adoptedAt" to Timestamp.now(),
                        "updatedAt" to Timestamp.now()
                    )
                )

                // 질문 상태 업데이트
                transaction.update(
                    questionRef,
                    mapOf(
                        "status" to QuestionStatus.CLOSED.name,
                        "selectedAnswerId" to answerId,
                        "updatedAt" to Timestamp.now()
                    )
                )
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adopting answer", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }
}