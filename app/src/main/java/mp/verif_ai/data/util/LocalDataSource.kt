package mp.verif_ai.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mp.verif_ai.data.local.dao.ConversationDao
import mp.verif_ai.data.local.dao.MessageDao
import mp.verif_ai.data.local.dao.ParticipantDao
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.toRoomEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val participantDao: ParticipantDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun saveConversation(conversation: Conversation) = withContext(dispatcher) {
        conversationDao.insertConversation(conversation.toRoomEntity())
    }

    suspend fun saveMessage(message: Message, conversationId: String) = withContext(dispatcher) {
        messageDao.insertMessage(message.toRoomEntity(conversationId))
    }

    suspend fun getConversation(conversationId: String): Conversation? = withContext(dispatcher) {
        conversationDao.getConversationWithDetails(conversationId)?.toDomainModel()
    }

    suspend fun getConversationHistory(limit: Int, offset: Int): List<Conversation> = withContext(dispatcher) {
        conversationDao.getConversationsWithDetails(limit, offset).map { it.toDomainModel() }
    }
}