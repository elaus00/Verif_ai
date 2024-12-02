package mp.verif_ai.domain.model.question

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val formattedDate: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        .format(Date(System.currentTimeMillis()))
)

enum class QuestionStatus { DRAFT, PUBLISHED, CLOSED, DELETED, CONTROVERSIAL }