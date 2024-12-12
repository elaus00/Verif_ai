package mp.verif_ai.data.repository.question

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
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.QuestionStatus
import mp.verif_ai.domain.model.question.TrendingQuestion
import mp.verif_ai.domain.repository.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val errorHandler: FirestoreErrorHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : QuestionRepository {

    companion object {
        private const val TAG = "QuestionRepo"
        private const val QUESTIONS_COLLECTION = "questions"
        private const val ANSWERS_COLLECTION = "answers"
    }

    private val questionsCollection = firestore.collection(QUESTIONS_COLLECTION)
    private val answersCollection = firestore.collection(ANSWERS_COLLECTION)

    override suspend fun createQuestion(question: Question): Result<String> {
        return try {
            val questionData = question.toMap().toMutableMap().apply {
                put("createdAt", com.google.firebase.Timestamp.now())
                put("updatedAt", com.google.firebase.Timestamp.now())
            }

            val documentRef = questionsCollection.document()
            documentRef.set(questionData).await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating question", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun getQuestion(questionId: String): Result<Question> {
        return try {
            val questionDoc = questionsCollection.document(questionId).get().await()

            if (!questionDoc.exists()) {
                return Result.failure(NoSuchElementException("Question not found with id: $questionId"))
            }

            // 답변 목록 조회
            val answersSnapshot = answersCollection
                .whereEqualTo("questionId", questionId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val answers = answersSnapshot.documents.mapNotNull { doc ->
                try {
                    Answer.fromMap(doc.data?.plus(mapOf("id" to doc.id)) ?: emptyMap())
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting answer document", e)
                    null
                }
            }

            val questionMap = questionDoc.data?.plus(mapOf("id" to questionDoc.id)) ?: emptyMap()
            val question = Question(
                id = questionDoc.id,
                title = questionDoc.getString("title") ?: "",
                category = questionDoc.getString("category") ?: "",
                tags = (questionDoc.get("tags") as? List<String>) ?: emptyList(),
                content = questionDoc.getString("content") ?: "",
                aiConversationId = questionDoc.getString("aiConversationId"),
                authorId = questionDoc.getString("authorId") ?: "",
                authorName = questionDoc.getString("authorName") ?: "",
                answers = answers,
                selectedAnswerId = questionDoc.getString("selectedAnswerId"),
                status = try {
                    QuestionStatus.valueOf(questionDoc.getString("status") ?: QuestionStatus.OPEN.name)
                } catch (e: IllegalArgumentException) {
                    QuestionStatus.OPEN
                },
                points = questionDoc.getLong("points")?.toInt() ?: Adoption.EXPERT_REVIEW_POINTS,
                viewCount = questionDoc.getLong("viewCount")?.toInt() ?: 0,
                commentCount = questionDoc.getLong("commentCount")?.toInt() ?: 0,
                createdAt = questionDoc.getTimestamp("createdAt")?.toDate()?.time
                    ?: System.currentTimeMillis(),
                updatedAt = questionDoc.getTimestamp("updatedAt")?.toDate()?.time
                    ?: System.currentTimeMillis()
            )

            Result.success(question)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting question", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun getTrendingQuestions(limit: Int): Flow<List<TrendingQuestion>> = callbackFlow {
        val listener = questionsCollection
            .orderBy("viewCount", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting trending questions", error)
                    close(error)
                    return@addSnapshotListener
                }

                val questions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        TrendingQuestion(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
                            commentCount = doc.getLong("commentCount")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting question document", e)
                        null
                    }
                } ?: emptyList()

                trySend(questions)
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(dispatcher)

    override suspend fun getMyQuestions(userId: String, limit: Int): Flow<List<Question>> = callbackFlow {
        val listener = questionsCollection
            .whereEqualTo("authorId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting user questions", error)
                    close(error)
                    return@addSnapshotListener
                }

                val questions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Question(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            category = doc.getString("category") ?: "",
                            tags = (doc.get("tags") as? List<String>) ?: emptyList(),
                            content = doc.getString("content") ?: "",
                            aiConversationId = doc.getString("aiConversationId"),
                            authorId = doc.getString("authorId") ?: "",
                            authorName = doc.getString("authorName") ?: "",
                            selectedAnswerId = doc.getString("selectedAnswerId"),
                            status = try {
                                QuestionStatus.valueOf(doc.getString("status") ?: QuestionStatus.OPEN.name)
                            } catch (e: IllegalArgumentException) {
                                QuestionStatus.OPEN
                            },
                            points = doc.getLong("points")?.toInt() ?: Adoption.EXPERT_REVIEW_POINTS,
                            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
                            commentCount = doc.getLong("commentCount")?.toInt() ?: 0,
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                                ?: System.currentTimeMillis(),
                            updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting question document", e)
                        null
                    }
                } ?: emptyList()

                trySend(questions)
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun updateQuestion(question: Question): Result<Unit> {
        return try {
            val questionData = question.toMap().plus(
                mapOf("updatedAt" to com.google.firebase.Timestamp.now())
            )

            questionsCollection.document(question.id)
                .update(questionData as Map<String, Any>)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating question", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }
}