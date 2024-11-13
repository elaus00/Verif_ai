package mp.verif_ai.domain.model

data class Question(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val status: QuestionStatus,
    val category: String,
    val isPublic: Boolean,
    val reward: Int,
    val attachments: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val author : String,
    val formattedDate : String,
)

enum class QuestionStatus { DRAFT, PUBLISHED, CLOSED, DELETED, CONTROVERSIAL }