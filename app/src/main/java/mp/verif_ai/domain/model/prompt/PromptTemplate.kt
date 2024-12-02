package mp.verif_ai.domain.model.prompt

data class PromptTemplate(
    val id: String,
    val name: String,
    val description: String,
    val content: String,
    val requiredParameters: List<String> = emptyList(),
    val category: PromptCategory,
    val createdAt: Long = System.currentTimeMillis()
)