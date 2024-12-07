package mp.verif_ai.data.service

import android.graphics.Bitmap
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.domain.service.AIService
import mp.verif_ai.domain.service.ChatMessage
import mp.verif_ai.domain.service.Role
import javax.inject.Inject

class OpenAIService @Inject constructor(
    private val openAI: OpenAI
) : AIService {
    override suspend fun generateResponse(
        prompt: String,
        model: AIModel,
        images: List<Bitmap>?
    ): Flow<String> = flow {
        if (images != null && images.isNotEmpty()) {
            throw UnsupportedOperationException("Image input is not supported for this model")
        }

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model.apiName),
            messages = listOf(
                ChatMessage(
                    role = Role.System,
                    content = "You are a helpful assistant."
                ),
                ChatMessage(
                    role = Role.User,
                    content = prompt
                )
            ).map { it.toOpenAIChatMessage() }
        )

        openAI.chatCompletions(chatCompletionRequest).collect { chunk ->
            chunk.choices.firstOrNull()?.delta?.content?.let { content ->
                if (content.isNotBlank()) {
                    emit(content)
                }
            }
        }
    }

    override suspend fun chat(
        messages: List<ChatMessage>,
        model: AIModel
    ): Flow<String> = flow {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model.apiName),
            messages = messages.map { it.toOpenAIChatMessage() }
        )

        openAI.chatCompletions(chatCompletionRequest).collect { chunk ->
            chunk.choices.firstOrNull()?.delta?.content?.let { content ->
                if (content.isNotBlank()) {
                    emit(content)
                }
            }
        }
    }

    override fun supportsModel(model: AIModel): Boolean {
        return model in setOf(
            AIModel.GPT_4O,
            AIModel.GPT_4O_MINI
        )
    }

    private fun ChatMessage.toOpenAIChatMessage(): com.aallam.openai.api.chat.ChatMessage {
        return com.aallam.openai.api.chat.ChatMessage(
            role = when (role) {
                Role.User -> com.aallam.openai.api.chat.ChatRole.User
                Role.Assistant -> com.aallam.openai.api.chat.ChatRole.Assistant
                Role.System -> com.aallam.openai.api.chat.ChatRole.System
                Role.Function -> com.aallam.openai.api.chat.ChatRole.Function
                Role.Tool -> com.aallam.openai.api.chat.ChatRole.Tool
                else -> throw IllegalArgumentException("Unsupported role: ${role.value}")
            },
            content = content
        )
    }
}