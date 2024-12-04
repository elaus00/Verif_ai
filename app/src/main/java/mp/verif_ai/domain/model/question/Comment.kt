package mp.verif_ai.domain.model.question

data class Comment(
    val id: String,
    val messageId: String,
    val authorId: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)