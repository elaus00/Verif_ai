package mp.verif_ai.data.repository.chat

import android.util.Log
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mp.verif_ai.domain.model.prompt.PromptException
import mp.verif_ai.domain.model.prompt.PromptResponse
import mp.verif_ai.domain.model.prompt.PromptTemplate
import mp.verif_ai.domain.model.prompt.UserPrompt
import mp.verif_ai.domain.repository.PromptRepository

@Singleton
class PromptRepositoryImpl @Inject constructor(
    private val openAI: OpenAI
) : PromptRepository {
    override suspend fun sendPrompt(prompt: UserPrompt): Flow<PromptResponse> = flow {
        val chatMessages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = prompt.content
            )
        )

        val request = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = chatMessages,
        )

        try {
            Log.d("PromptRepositoryImpl", "Sending request with model: ${request.model}")
            Log.d("PromptRepositoryImpl", "Request content: ${prompt.content}")

            openAI.chatCompletions(request).collect { chunk ->
                Log.d("PromptRepositoryImpl", "Received chunk: $chunk")

                chunk.choices.firstOrNull()?.let { choice ->
                    Log.d("PromptRepositoryImpl", "Processing choice: $choice")

                    emit(
                        PromptResponse(
                            id = chunk.id,
                            promptId = prompt.id,
                            content = choice.delta?.content ?: ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PromptRepositoryImpl", "Error sending prompt", e)
            e.printStackTrace()  // 스택 트레이스 전체 출력

            // 네트워크 관련 예외인 경우 추가 정보 로깅
            when (e) {
                is java.net.UnknownHostException -> {
                    Log.e("PromptRepositoryImpl", "Network error - Unable to resolve host", e)
                }
                is java.io.IOException -> {
                    Log.e("PromptRepositoryImpl", "Network error - IO Exception", e)
                }
                else -> {
                    Log.e("PromptRepositoryImpl", "Unexpected error: ${e.javaClass.simpleName}", e)
                }
            }

            throw PromptException("Failed to send prompt: ${e.message}", e)
        }
    }
    // 다른 메서드들은 나중에 구현
    override suspend fun getPromptTemplates(): Flow<List<PromptTemplate>> = flow {
        emit(emptyList())
    }

    override suspend fun getPromptTemplate(templateId: String): PromptTemplate? = null

    override suspend fun getPromptHistory(limit: Int): Flow<List<UserPrompt>> = flow {
        emit(emptyList())
    }
}