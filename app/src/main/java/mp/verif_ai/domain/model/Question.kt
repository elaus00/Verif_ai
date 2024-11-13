package mp.verif_ai.domain.model

data class Question(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val status: QuestionStatus = QuestionStatus.DRAFT,
    val category: String = "General",
    val isPublic: Boolean = true,
    val reward: Int = 0,
    val attachments: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val author: String = "Anonymous",
    val formattedDate: String = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(System.currentTimeMillis()))
)

enum class QuestionStatus { DRAFT, PUBLISHED, CLOSED, DELETED, CONTROVERSIAL }