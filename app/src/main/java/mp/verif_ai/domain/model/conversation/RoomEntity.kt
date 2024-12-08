package mp.verif_ai.domain.model.conversation

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mp.verif_ai.domain.model.auth.UserType
import mp.verif_ai.domain.model.expert.ExpertProfile
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption

@Entity(tableName = "conversations")
data class ConversationRoomEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,    // 추가: ConversationType enum을 문자열로 저장
    val status: String,  // 추가: ConversationStatus enum을 문자열로 저장
    val category: String? = null,
    val tags: String,    // List<String>을 JSON으로 저장
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "participants")
data class ParticipantRoomEntity(
    @PrimaryKey val id: String,
    val conversationId: String,  // 외래 키
    val name: String,
    val type: String,  // "EXPERT", "ASSISTANT", "USER"
    val status: String, // 추가: ParticipationStatus enum을 문자열로 저장
    val participantData: String  // 각 타입별 추가 데이터를 JSON으로 저장
)

@Entity(tableName = "messages")
data class MessageRoomEntity(
    @PrimaryKey val id: String,
    val conversationId: String,  // 외래 키
    val content: String,
    val senderId: String,
    val replyTo: String?,
    val messageSource: String?, // MessageSource JSON
    val status: String,
    val additionalData: String,  // 추가 데이터 (expertReviews, adoption, isVerified, references 등) JSON
    val timestamp: Long
)

// Room 관련 변환 함수들
private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

fun Conversation.toRoomEntity() = ConversationRoomEntity(
    id = id,
    title = title,
    type = type.name,    // 추가: enum을 문자열로 변환
    status = status.name, // 추가: enum을 문자열로 변환
    category = category?.toString(),
    tags = json.encodeToString(tags),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Participant.toRoomEntity(conversationId: String) = ParticipantRoomEntity(
    id = id,
    conversationId = conversationId,
    name = name,
    type = when (this) {
        is Participant.Expert -> "EXPERT"
        is Participant.Assistant -> "ASSISTANT"
        is Participant.User -> "USER"
    },
    status = when (this) {
        is Participant.Expert -> participationStatus.name
        is Participant.User -> participationStatus.name
        is Participant.Assistant -> ParticipationStatus.ACTIVE.name // 또는 적절한 기본값
    },
    participantData = when (this) {
        is Participant.Expert -> json.encodeToString(ParticipantExpertData.serializer(),
            ParticipantExpertData(profile, participationStatus))
        is Participant.User -> json.encodeToString(ParticipantUserData.serializer(),
            ParticipantUserData(type, participationStatus))
        is Participant.Assistant -> "{}"
    }
)


fun Message.toRoomEntity(conversationId: String) = MessageRoomEntity(
    id = id,
    conversationId = conversationId,
    content = content,
    senderId = senderId,
    replyTo = replyTo,
    messageSource = messageSource?.let { json.encodeToString(it) },
    status = (this as Message.Text).status.name,
    additionalData = json.encodeToString(MessageTextData(
        expertReviews = (this as Message.Text).expertReviews,
        adoption = (this as Message.Text).adoption,
        isVerified = (this as Message.Text).isVerified,
        references = (this as Message.Text).references
    )),
    timestamp = timestamp
)


// 추가 데이터를 위한 직렬화 클래스들
@Serializable
private data class ParticipantExpertData(
    val profile: ExpertProfile,
    val status: ParticipationStatus
)

@Serializable
private data class ParticipantUserData(
    val type: UserType,
    val status: ParticipationStatus
)

@Serializable
private data class MessageTextData(
    val expertReviews: List<ExpertReview>,
    val adoption: Adoption?,
    val isVerified: Boolean,
    val references: List<String>
)