package mp.verif_ai.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import mp.verif_ai.domain.model.*
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.ConversationRoomEntity
import mp.verif_ai.domain.model.conversation.ConversationStatus
import mp.verif_ai.domain.model.conversation.ConversationType
import mp.verif_ai.domain.model.conversation.MessageRoomEntity
import mp.verif_ai.domain.model.conversation.ParticipantRoomEntity
import mp.verif_ai.domain.model.conversation.ParticipantType
import mp.verif_ai.domain.model.conversation.ParticipationStatus

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationRoomEntity)

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationRoomEntity?

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getConversations(limit: Int, offset: Int): List<ConversationRoomEntity>

    @Delete
    suspend fun deleteConversation(conversation: ConversationRoomEntity)

    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    fun observeConversationWithDetails(conversationId: String): Flow<ConversationWithDetails>

    @Transaction
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getConversationsWithDetails(limit: Int, offset: Int): List<ConversationWithDetails>

    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationWithDetails(conversationId: String): ConversationWithDetails?
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageRoomEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: String): Flow<List<MessageRoomEntity>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageRoomEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessages(conversationId: String, limit: Int, offset: Int): List<MessageRoomEntity>

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: String)
}

@Dao
interface ParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ParticipantRoomEntity)

    @Query("SELECT * FROM participants WHERE conversationId = :conversationId")
    fun observeParticipants(conversationId: String): Flow<List<ParticipantRoomEntity>>

    @Query("SELECT * FROM participants WHERE id = :participantId")
    suspend fun getParticipantById(participantId: String): ParticipantRoomEntity?

    @Query("UPDATE participants SET type = :type, status = :status WHERE id = :participantId")
    suspend fun updateParticipant(participantId: String, type: String, status: String)
}

/**
 * Room 관계 매핑을 위한 데이터 클래스
 */
data class ConversationWithDetails(
    @Embedded val conversation: ConversationRoomEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "conversationId"
    )
    val messages: List<MessageRoomEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "conversationId"
    )
    val participants: List<ParticipantRoomEntity>
) {
    fun toDomainModel(): Conversation {
        val participantTypes = participants.associate {
            it.id to ParticipantType.valueOf(it.type)
        }
        val participantStatuses = participants.associate {
            it.id to ParticipationStatus.valueOf(it.status)
        }

        return Conversation(
            id = conversation.id,
            title = conversation.title,
            participantIds = participants.map { it.id },
            participantTypes = participantTypes,
            participantStatuses = participantStatuses,
            type = ConversationType.valueOf(conversation.type),
            status = ConversationStatus.valueOf(conversation.status),
            category = conversation.category,
            tags = json.decodeFromString<List<String>>(conversation.tags), // JSON 문자열을 List<String>으로 변환
            createdAt = conversation.createdAt,
            updatedAt = conversation.updatedAt
        )
    }
}