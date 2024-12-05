package mp.verif_ai.domain.model.chat

data class Answer(
    val id: String,
    val questionId: String,
    val expertId: String,
    val content: String,
    val type: AnswerType,
    val status: AnswerStatus,
    val accuracy: Int?,
    val isAdopted: Boolean,
    val helpfulCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

enum class AnswerType { TEXT, VOICE, VIDEO }
enum class AnswerStatus { PENDING, ACCEPTED, REJECTED, CONTROVERSIAL }