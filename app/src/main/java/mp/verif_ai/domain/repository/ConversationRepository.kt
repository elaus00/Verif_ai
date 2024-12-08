package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.conversation.Message

/**
 * Repository interface for managing conversations between users and the AI.
 * Handles conversation data persistence, retrieval and real-time updates.
 */
interface ConversationRepository {
    /**
     * Observes changes to a specific conversation in real-time.
     * @param conversationId Unique identifier of the conversation to observe
     * @return Flow of Conversation updates
     */
    suspend fun observeConversation(conversationId: String): Flow<Conversation>

    /**
     * Sends a new message to a specific conversation.
     * @param conversationId ID of the conversation to send the message to
     * @param message Message content to be sent
     * @return Result containing the message ID if successful
     */
    suspend fun sendMessage(conversationId: String, message: Message): Result<String>

    /**
     * Retrieves paginated conversation history for a specific user.
     * @param userId ID of the user whose conversations to retrieve
     * @param limit Maximum number of conversations to retrieve (default: 20)
     * @param offset Number of conversations to skip (default: 0)
     * @return Result containing list of conversations if successful
     */
    suspend fun getConversationHistory(
        userId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Conversation>>

    /**
     * Retrieves complete conversation details including all messages.
     * @param conversationId ID of the conversation to retrieve
     * @return Result containing the complete conversation if successful
     */
    suspend fun getFullConversation(conversationId: String): Result<Conversation>

    suspend fun createConversation(conversation: Conversation): Result<Unit>
    suspend fun updateConversation(conversation: Conversation): Result<Unit>
    suspend fun updateConversationStatus(
        conversationId: String,
        status: ConversationStatus
    ): Result<Unit>
}