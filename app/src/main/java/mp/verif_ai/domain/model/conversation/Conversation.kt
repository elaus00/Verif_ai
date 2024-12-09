package mp.verif_ai.domain.model.conversation

import mp.verif_ai.domain.model.expert.ExpertFields

data class Conversation(
    val id: String = "",
    val title: String = "",
    val participantIds: List<String> = emptyList(),
    val participantTypes: Map<String, ParticipantType> = emptyMap(),
    val participantStatuses: Map<String, ParticipationStatus> = emptyMap(),
    val messages: List<Message> = emptyList(),
    val type: ConversationType = ConversationType.AI_CHAT,
    val status: ConversationStatus = ConversationStatus.ACTIVE,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "participantIds" to participantIds,
        "participantTypes" to participantTypes.mapValues { it.value.name },
        "participantStatuses" to participantStatuses.mapValues { it.value.name },
        "messages" to messages.map { it.toMap() },  // Message를 Map으로 변환
        "type" to type.name,
        "status" to status.name,
        "category" to category,
        "tags" to tags,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Conversation = Conversation(
            id = map["id"] as? String ?: "",
            title = map["title"] as? String ?: "",
            participantIds = (map["participantIds"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            participantTypes = (map["participantTypes"] as? Map<*, *>)?.mapNotNull { entry ->
                val id = entry.key as? String ?: return@mapNotNull null
                val type = ParticipantType.valueOf(entry.value as? String ?: return@mapNotNull null)
                id to type
            }?.toMap() ?: emptyMap(),
            participantStatuses = (map["participantStatuses"] as? Map<*, *>)?.mapNotNull { entry ->
                val id = entry.key as? String ?: return@mapNotNull null
                val status = ParticipationStatus.valueOf(entry.value as? String ?: return@mapNotNull null)
                id to status
            }?.toMap() ?: emptyMap(),
            messages = (map["messages"] as? List<*>)?.mapNotNull {
                (it as? Map<*, *>)?.let { messageMap ->
                    Message.Text.fromMap(messageMap.mapKeys { entry -> entry.key.toString() })
                }
            } ?: emptyList(),
            type = ConversationType.valueOf(map["type"] as? String ?: ConversationType.AI_CHAT.name),
            status = ConversationStatus.valueOf(map["status"] as? String ?: ConversationStatus.ACTIVE.name),
            category = map["category"] as? String,
            tags = (map["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}

enum class ParticipantType {
    USER,
    EXPERT,
    ASSISTANT
}

enum class ConversationType {
    AI_CHAT,        // AI와의 대화
    QNA,            // 질문-답변
    EXPERT_CHAT     // 전문가와 1:1 채팅
}

enum class ConversationStatus {
    ACTIVE,     // 진행중
    COMPLETED,  // 완료됨
    EXPIRED,    // 만료됨
    DELETED     // 삭제됨
}

fun Conversation.addMessage(message: Message): Conversation {
    return this.copy(
        messages = messages + message,
        updatedAt = System.currentTimeMillis()
    )
}

fun Conversation.updateStatus(status: ConversationStatus): Conversation {
    return this.copy(
        status = status,
        updatedAt = System.currentTimeMillis()
    )
}

fun Conversation.updateParticipantStatus(
    participantId: String,
    status: ParticipationStatus
): Conversation {
    return this.copy(
        participantStatuses = participantStatuses + (participantId to status),
        updatedAt = System.currentTimeMillis()
    )
}