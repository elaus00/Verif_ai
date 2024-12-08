package mp.verif_ai.presentation.screens.conversation.factory

import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.conversation.ConversationType
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.ParticipantType
import mp.verif_ai.domain.model.conversation.ParticipationStatus
import java.util.UUID

object ConversationFactory {
    fun createNewConversation(
        userMessage: Message,
        userId: String,
        type: ConversationType = ConversationType.AI_CHAT
    ): Conversation {
        return Conversation(
            id = UUID.randomUUID().toString(),
            title = generateTitleFromMessage(userMessage.content),
            participantIds = listOf(userId, "assistant"),
            participantTypes = mapOf(
                userId to ParticipantType.USER,
                "assistant" to ParticipantType.ASSISTANT
            ),
            participantStatuses = mapOf(
                userId to ParticipationStatus.ACTIVE,
                "assistant" to ParticipationStatus.ACTIVE
            ),
            messages = listOf(userMessage),
            type = type,
            status = ConversationStatus.ACTIVE
        )
    }

    private fun generateTitleFromMessage(content: String): String {
        return content.take(50).let {
            if (content.length > 50) "$it..." else it
        }
    }
}