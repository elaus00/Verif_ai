package mp.verif_ai.domain.model.prompt

data class PromptResponse(
    val id: String,
    val promptId: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)