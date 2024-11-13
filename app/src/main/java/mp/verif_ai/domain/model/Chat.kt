package mp.verif_ai.domain.model

data class Chat(
    val id: String,
    val questionId: String,
    val participants: List<String>,
    val status: ChatStatus,
    val lastMessageAt: Long,
    val createdAt: Long,
    val updatedAt: Long
)

enum class ChatStatus { ACTIVE, CLOSED }