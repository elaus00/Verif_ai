package mp.verif_ai.data.service

import mp.verif_ai.domain.service.AIModel
import mp.verif_ai.domain.service.AIService
import javax.inject.Inject

class AIServiceFactory @Inject constructor(
    private val openAIService: OpenAIService,
    private val geminiService: GeminiService
) {
    fun getService(model: AIModel): AIService {
        return when {
            openAIService.supportsModel(model) -> openAIService
            geminiService.supportsModel(model) -> geminiService
            else -> throw IllegalArgumentException("Unsupported AI model: ${model.name}")
        }
    }
}