package mp.verif_ai.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import mp.verif_ai.data.repository.PromptRepositoryImpl
import mp.verif_ai.domain.model.chat.PromptImport
import mp.verif_ai.domain.repository.PromptRepository

class FirebaseChatRepositoryImpl(
    private val functions: FirebaseFunctions = Firebase.functions,
    private val firestore: FirebaseFirestore = Firebase.firestore
) : PromptRepository {


    companion object {
        private const val TAG = "ChatRepositoryImpl"
    }

    override suspend fun importPrompt(promptText: String): PromptImport {
        TODO("Not yet implemented")
    }

    override suspend fun createQuestionFromPrompt(conversationId: String): String {
        TODO("Not yet implemented")
    }
}