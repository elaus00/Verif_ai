package mp.verif_ai.data.service

import javax.inject.Inject
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import android.graphics.Bitmap
import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.domain.service.AIService
import mp.verif_ai.domain.service.AIServiceException
import mp.verif_ai.domain.service.ChatMessage
import mp.verif_ai.domain.service.Role


class GeminiService @Inject constructor(
    private val geminiClient: GenerativeModel
) : AIService {
    override suspend fun generateResponse(
        prompt: String,
        model: AIModel,
        images: List<Bitmap>?
    ): Flow<String> = flow {
        try {
            val inputContent = when {
                !images.isNullOrEmpty() -> content {
                    images.forEach { image(it) }
                    text(prompt)
                }
                else -> content { text(prompt) }
            }

            geminiClient.generateContentStream(inputContent)
                .collect { chunk ->
                    chunk.text?.let { emit(it) }
                }
        } catch (e: Exception) {
            throw AIServiceException(
                message = "Failed to generate response from Gemini: ${e.message}",
                cause = e
            )
        }
    }

    override suspend fun chat(
        messages: List<ChatMessage>,
        model: AIModel
    ): Flow<String> = flow {
        try {
            val chat = geminiClient.startChat(
                history = messages.map { message ->
                    content(role = message.role.toGeminiRole()) {
                        text(message.content)
                        message.images?.forEach { image(it) }
                    }
                }
            )

            val latestMessage = messages.lastOrNull()?.content ?: throw AIServiceException(
                message = "No messages provided for chat"
            )

            chat.sendMessageStream(latestMessage)
                .collect { chunk ->
                    chunk.text?.let { emit(it) }
                }
        } catch (e: Exception) {
            throw AIServiceException(
                message = "Failed to generate chat response from Gemini: ${e.message}",
                cause = e
            )
        }
    }

    override fun supportsModel(model: AIModel): Boolean {
        return model in setOf(
            AIModel.GEMINI_1_5_PRO,
            AIModel.GEMINI_1_5_FLASH
        )
    }

    private fun Role.toGeminiRole(): String = when (this) {
        Role.User -> "user"
        Role.Assistant -> "model"
        Role.System -> "model" // Gemini doesn't support system role
        else -> "user" // Default to user for unsupported roles
    }
}
