package mp.verif_ai.domain.repository

interface ChatRepository {
    suspend fun createShareLink(conversationId: String): Result<String>
    suspend fun importConversation(shareId: String): Result<String>
}
