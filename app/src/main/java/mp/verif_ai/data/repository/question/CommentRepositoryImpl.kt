package mp.verif_ai.data.repository.question

import android.util.Log
import com.google.firebase.firestore.FieldValue
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
import mp.verif_ai.domain.model.question.Comment
import mp.verif_ai.domain.model.question.CommentParentType
import mp.verif_ai.domain.model.question.CommentStatus
import mp.verif_ai.domain.repository.CommentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val errorHandler: FirestoreErrorHandler,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CommentRepository {

    companion object {
        private const val TAG = "CommentRepo"
        private const val COMMENTS_COLLECTION = "comments"
        private const val QUESTIONS_COLLECTION = "questions"
        private const val ANSWERS_COLLECTION = "answers"
    }

    private val commentsCollection = firestore.collection(COMMENTS_COLLECTION)

    override suspend fun createComment(comment: Comment): Result<String> = try {
        val commentData = comment.toMap().toMutableMap().apply {
            put("createdAt", com.google.firebase.Timestamp.now())
            put("updatedAt", com.google.firebase.Timestamp.now())
        }

        val documentRef = commentsCollection.document()
        documentRef.set(commentData).await()

        // 부모 문서의 댓글 수 증가
        val parentCollection = when (comment.parentType) {
            CommentParentType.QUESTION -> firestore.collection(QUESTIONS_COLLECTION)
            CommentParentType.ANSWER -> firestore.collection(ANSWERS_COLLECTION)
        }
        parentCollection.document(comment.parentId)
            .update("commentCount", FieldValue.increment(1))
            .await()

        Result.success(documentRef.id)
    } catch (e: Exception) {
        Log.e(TAG, "Error creating comment", e)
        Result.failure(errorHandler.handleFirestoreError(e))
    }

    override suspend fun getComment(commentId: String): Result<Comment> = try {
        val snapshot = commentsCollection.document(commentId).get().await()

        if (!snapshot.exists()) {
            Result.failure(NoSuchElementException("Comment not found with id: $commentId"))
        } else {
            val commentMap = snapshot.data?.plus(mapOf("id" to snapshot.id)) ?: emptyMap()
            Result.success(Comment.fromMap(commentMap))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error getting comment", e)
        Result.failure(errorHandler.handleFirestoreError(e))
    }

    override suspend fun observeComments(
        parentId: String,
        parentType: CommentParentType
    ): Flow<List<Comment>> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("parentId", parentId)
            .whereEqualTo("parentType", parentType.name)
            .whereNotEqualTo("status", CommentStatus.DELETED.name)
            .orderBy("status")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing comments", error)
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Comment.fromMap(doc.data?.plus(mapOf("id" to doc.id)) ?: emptyMap())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting comment document", e)
                        null
                    }
                } ?: emptyList()

                trySend(comments)
            }

        awaitClose { listener.remove() }
    }.flowOn(dispatcher)

    override suspend fun updateComment(comment: Comment): Result<Unit> = try {
        val commentData = comment.toMap().plus(
            mapOf("updatedAt" to com.google.firebase.Timestamp.now())
        )

        commentsCollection.document(comment.id)
            .update(commentData as Map<String, Any>)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error updating comment", e)
        Result.failure(errorHandler.handleFirestoreError(e))
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            val commentDoc = commentsCollection.document(commentId).get().await()
            if (!commentDoc.exists()) {
                return Result.failure(NoSuchElementException("Comment not found"))
            }

            commentsCollection.document(commentId)
                .update(
                    mapOf(
                        "status" to CommentStatus.DELETED.name,
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()

            // 부모 문서의 댓글 수 감소
            val parentId = commentDoc.getString("parentId") ?: return Result.success(Unit)
            val parentType = CommentParentType.valueOf(commentDoc.getString("parentType") ?: return Result.success(Unit))

            val parentCollection = when (parentType) {
                CommentParentType.QUESTION -> firestore.collection(QUESTIONS_COLLECTION)
                CommentParentType.ANSWER -> firestore.collection(ANSWERS_COLLECTION)
            }

            parentCollection.document(parentId)
                .update("commentCount", FieldValue.increment(-1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting comment", e)
            Result.failure(errorHandler.handleFirestoreError(e))
        }
    }

    override suspend fun reportComment(commentId: String, reporterId: String): Result<Unit> = try {
        val commentRef = commentsCollection.document(commentId)

        firestore.runTransaction { transaction ->
            val commentDoc = transaction.get(commentRef)
            if (!commentDoc.exists()) {
                throw NoSuchElementException("Comment not found")
            }

            val currentReportCount = commentDoc.getLong("reportCount")?.toInt() ?: 0
            val newReportCount = currentReportCount + 1

            transaction.update(
                commentRef,
                mapOf(
                    "reportCount" to newReportCount,
                    "isReported" to true,
                    "status" to if (newReportCount >= 3) CommentStatus.HIDDEN.name else CommentStatus.ACTIVE.name,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error reporting comment", e)
        Result.failure(errorHandler.handleFirestoreError(e))
    }
}