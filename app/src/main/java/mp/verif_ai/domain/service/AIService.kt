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
        description = "가장 강력한 성능을 제공하는 모델. 복잡한 작업과 전문적인 내용 처리에 적합"
    ),
    GPT_4O_MINI(
        apiName = "gpt-4o-mini",
        displayName = "GPT-4o Mini",
        description = "GPT-4의 경량화 버전. 빠른 응답과 효율적인 리소스 사용"
    ),

    // Google Gemini
    GEMINI_1_5_PRO(
        apiName = "gemini-1.5-pro",
        displayName = "Gemini 1.5 Pro",
        description = "Google의 최신 대규모 언어 모델. 높은 정확도와 다국어 지원"
    ),
    GEMINI_1_5_FLASH(
        apiName = "gemini-1.5-flash",
        displayName = "Gemini 1.5 Flash",
        description = "빠른 응답 시간에 최적화된 Gemini 모델. 실시간 대화에 적합"
    );

    override fun toString(): String = apiName
}