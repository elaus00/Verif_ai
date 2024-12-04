package mp.verif_ai.domain.util.dto

import kotlinx.serialization.Serializable
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.MessageStatus

@Serializable
data class ConversationFirestoreDto(
    val id: String = "",
    val title: String = "",
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    fun toDomainModel() = Conversation(
        id = id,
        title = title,
        category = category?.let { /* category parsing logic */ }.toString(),
        tags = tags,
        createdAt = createdAt,
        updatedAt = updatedAt,
        participantIds = TODO(),
        participantTypes = TODO(),
        participantStatuses = TODO(),
        type = TODO(),
        status = TODO()
    )
}

//@Serializable
//data class MessageFirestoreDto(
//    val id: String = "",
//    val content: String = "",
//    val senderId: String = "",
//    val replyTo: String? = null,
//    val messageSource: MessageSourceDto? = null,
//    val status: String = MessageStatus.PENDING.name,
//    val expertReviews: List<ExpertReviewDto> = emptyList(),
//    val adoption: AdoptionDto? = null,
//    val isVerified: Boolean = false,
//    val references: List<String> = emptyList(),
//    val timestamp: Long = 0
//) {
//    fun toDomainModel() = Message.Text(
//        id = id,
//        content = content,
//        senderId = senderId,
//        replyTo = replyTo,
//        messageSource = messageSource?.toDomainModel(),
//        status = MessageStatus.valueOf(status),
//        expertReviews = expertReviews.map { it.toDomainModel() },
//        adoption = adoption?.toDomainModel(),
//        isVerified = isVerified,
//        references = references,
//        timestamp = timestamp
//    )
//}