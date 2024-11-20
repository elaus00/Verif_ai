package mp.verif_ai.domain.model.chat

data class PromptImport(
    val conversationId: String,
    val confidence: Double,
    val messageCount: Int
)
