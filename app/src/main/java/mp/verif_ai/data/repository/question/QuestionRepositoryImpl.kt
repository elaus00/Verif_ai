package mp.verif_ai.data.repository.question

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.di.IoDispatcher
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
    }

    private val questionsCollection = firestore.collection("questions")

    override suspend fun createQuestion(question: Question): Result<String> {
        return try {
            val questionData = hashMapOf(
                "title" to question.title,
                "content" to question.content,
                "authorId" to question.authorId,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "viewCount" to question.viewCount,
                "points" to question.points,
                "status" to question.status.name,
                "tags" to question.tags
            )

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
            val snapshot = questionsCollection.document(questionId).get().await()

            if (!snapshot.exists()) {
                return Result.failure(NoSuchElementException("Question not found with id: $questionId"))
            }

            val question = try {
                Question(
                    id = snapshot.id,
                    title = snapshot.getString("title") ?: "",
                    content = snapshot.getString("content") ?: "",
                    authorId = snapshot.getString("authorId") ?: "",
                    createdAt = snapshot.getTimestamp("createdAt")?.toDate()?.time
                        ?: System.currentTimeMillis(),
                    viewCount = snapshot.getLong("viewCount")?.toInt() ?: 0,
                    points = snapshot.getLong("points")?.toInt() ?: 0,
                    status = try {
                        QuestionStatus.valueOf(snapshot.getString("status") ?: QuestionStatus.OPEN.name)
                    } catch (e: IllegalArgumentException) {
                        QuestionStatus.OPEN
                    },
                    tags = (snapshot.get("tags") as? List<String>) ?: emptyList(),
                    answers = emptyList()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error converting question document", e)
                throw e
            }

            Result.success(question)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting question", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun getTrendingQuestions(limit: Int): Flow<List<TrendingQuestion>> = callbackFlow {
        val listener = questionsCollection
            .orderBy("viewCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
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
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
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
                            content = doc.getString("content") ?: "",
                            authorId = doc.getString("authorId") ?: "",
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
                                ?: System.currentTimeMillis(),
                            viewCount = doc.getLong("viewCount")?.toInt() ?: 0,
                            points = doc.getLong("points")?.toInt() ?: 0
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

    override suspend fun updateQuestion(question: Question): Result<Unit> {
        return try {
            val questionData = hashMapOf(
                "title" to question.title,
                "content" to question.content,
                "authorId" to question.authorId,
                "viewCount" to question.viewCount,
                "points" to question.points,
                "status" to question.status.name,
                "tags" to question.tags
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