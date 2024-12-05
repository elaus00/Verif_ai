package mp.verif_ai.domain.model.chat

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val type: MessageType,
    val sentAt: Long,
    val readBy: List<String>
)

enum class MessageType { TEXT, IMAGE, FILE }