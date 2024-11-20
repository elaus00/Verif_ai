package mp.verif_ai.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import mp.verif_ai.domain.repository.ChatRepository

class FirebaseChatRepositoryImpl(
    private val functions: FirebaseFunctions = Firebase.functions,
    private val firestore: FirebaseFirestore = Firebase.firestore
) : ChatRepository {

    override suspend fun createShareLink(conversationId: String): Result<String> {
        return try {
            val data = hashMapOf("conversationId" to conversationId)
            val result = functions
                .getHttpsCallable("createShareableLink")
                .call(data)
                .await()

            val shareUrl = (result.getData() as Map<*, *>)["shareUrl"] as String
            Result.success(shareUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun importConversation(shareId: String): Result<String> {
        return try {
            val data = hashMapOf("shareId" to shareId)
            val result = functions
                .getHttpsCallable("importSharedConversation")
                .call(data)
                .await()

            val conversationId = (result.getData() as Map<*, *>)["conversationId"] as String
            Result.success(conversationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "ChatRepositoryImpl"
    }
}