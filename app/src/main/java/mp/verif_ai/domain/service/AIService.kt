package mp.verif_ai.domain.service

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

// Core interfaces
interface AIService {
    suspend fun generateResponse(
        prompt: String,
        model: AIModel,
        images: List<Bitmap>? = null
    ): Flow<String>

    suspend fun chat(
        messages: List<ChatMessage>,
        model: AIModel
    ): Flow<String>

    fun supportsModel(model: AIModel): Boolean
}

// Data classes and enums
data class ChatMessage(
    val role: Role,
    val content: String,
    val images: List<Bitmap>? = null
)


@JvmInline
@Serializable
value class Role(val value: String) {
    companion object {
        val System = Role("system")
        val User = Role("user")
        val Assistant = Role("assistant")
        val Function = Role("function")
        val Tool = Role("tool")
    }
}


// Custom exception for AI service errors
class AIServiceException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)


enum class AIModel(
    val apiName: String,
    val displayName: String,
    val description: String = ""
) {
    // OpenAI
    GPT_4O(
        apiName = "gpt-4o",
        displayName = "GPT-4o",
        description = "A model that provides the most powerful performance. Suitable for complex tasks and handling specialized content."
    ),
    GPT_4O_MINI(
        apiName = "gpt-4o-mini",
        displayName = "GPT-4o Mini",
        description = "A lightweight version of GPT-4. Fast response and efficient resource usage."
    ),

    // Google Gemini
    GEMINI_1_5_PRO(
        apiName = "gemini-1.5-pro",
        displayName = "Gemini 1.5 Pro",
        description = "Google's latest large language model. High accuracy and multilingual support."
    ),
    GEMINI_1_5_FLASH(
        apiName = "gemini-1.5-flash",
        displayName = "Gemini 1.5 Flash",
        description = "A Gemini model optimized for fast response times. Suitable for real-time conversations."
    );

    override fun toString(): String = apiName
}