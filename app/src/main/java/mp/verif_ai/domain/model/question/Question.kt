package mp.verif_ai.domain.model.question

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Question(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val content: String = "",
    val aiConversationId: String? = null,
    val authorId: String = "",
    val status: QuestionStatus = QuestionStatus.OPEN,
    val points: Int = Adoption.EXPERT_REVIEW_POINTS,
    val viewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class QuestionStatus {
    OPEN,      // 답변 받기 가능
    CLOSED,    // 답변 채택 완료
    EXPIRED,   // 기간 만료
    DELETED    // 삭제됨
}