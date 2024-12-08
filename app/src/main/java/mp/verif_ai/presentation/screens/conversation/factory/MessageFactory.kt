package mp.verif_ai.presentation.screens.conversation.factory

import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.MessageSource
import mp.verif_ai.domain.model.conversation.SourceType
import mp.verif_ai.domain.service.AIModel
import java.util.UUID

object MessageFactory {
    fun createMessage(
        content: String,
        type: SourceType,
        senderId: String,
        model: AIModel? = null
    ): Message.Text = Message.Text(
        id = UUID.randomUUID().toString(),
        content = content,
        senderId = senderId,
        messageSource = MessageSource(
            type = type,
            model = model
        )
    )
}