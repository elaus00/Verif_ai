package mp.verif_ai.domain.model

data class Conversation(
    val id: String,
    val questionId: String,
    val userId: String,
    val lastMessage: String,
    val participantCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val formattedDate: String
)
