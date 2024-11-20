package mp.verif_ai.data.repository

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.domain.model.chat.PromptImport
import mp.verif_ai.domain.repository.PromptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptRepositoryImpl @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PromptRepository {
    override suspend fun importPrompt(promptText: String): PromptImport = withContext(dispatcher) {
        try {
            val data = hashMapOf("promptText" to promptText)
            val result = firebaseFunctions
                .getHttpsCallable("importPrompt")
                .call(data)
                .await()

            val response = result.getData() as HashMap<*, *>

            PromptImport(
                conversationId = response["conversationId"] as String,
                confidence = (response["confidence"] as Number).toDouble(),
                messageCount = (response["messageCount"] as Number).toInt()
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createQuestionFromPrompt(conversationId: String): String =
        withContext(dispatcher) {
            try {
                val data = hashMapOf("conversationId" to conversationId)
                val result = firebaseFunctions
                    .getHttpsCallable("createQuestionFromPrompt")
                    .call(data)
                    .await()

                val response = result.getData() as HashMap<*, *>
                response["questionId"] as String
            } catch (e: Exception) {
                throw e
            }
        }
}