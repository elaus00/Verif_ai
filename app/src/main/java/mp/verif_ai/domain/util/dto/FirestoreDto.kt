package mp.verif_ai.domain.util.dto

import kotlinx.serialization.Serializable
import mp.verif_ai.domain.model.conversation.Conversation

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
