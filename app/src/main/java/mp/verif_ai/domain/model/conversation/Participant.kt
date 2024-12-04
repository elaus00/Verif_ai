package mp.verif_ai.domain.model.conversation

import mp.verif_ai.domain.model.expert.ExpertProfile
import mp.verif_ai.domain.model.auth.UserType

sealed class Participant {
    abstract val id: String
    abstract val name: String

    data class Expert(
        override val id: String,
        override val name: String,
        val profile: ExpertProfile,
        val participationStatus: ParticipationStatus = ParticipationStatus.ACTIVE
    ) : Participant()

    data class Assistant(
        override val id: String,
        override val name: String = "AI Assistant"
    ) : Participant()

    data class User(
        override val id: String,
        override val name: String,
        val type: UserType = UserType.NORMAL,
        val participationStatus: ParticipationStatus = ParticipationStatus.ACTIVE
    ) : Participant()
}

data class ParticipantFirestoreDto(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val status: String = "",
    val participantData: String = ""
) {
    fun toRoomEntity(conversationId: String) = ParticipantRoomEntity(
        id = id,
        conversationId = conversationId,
        name = name,
        type = type,
        status = status,
        participantData = participantData
    )
}

enum class ParticipationStatus {
    ACTIVE,     // 활성 상태
    INACTIVE,   // 비활성 상태
    LEFT,       // 대화방 나감
    BANNED      // 차단됨
}

enum class MediaType {
    IMAGE,
    AUDIO,
    VIDEO,
    DOCUMENT
}