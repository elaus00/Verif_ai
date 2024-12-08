package mp.verif_ai.domain.model.conversation

import kotlinx.serialization.Serializable
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.service.AIModel

sealed class Message {
    abstract val id: String
    abstract val content: String
    abstract val senderId: String
    abstract val timestamp: Long
    abstract val replyTo: String? // 답변 대상 메시지 ID
    abstract val messageSource: MessageSource? // 메시지 출처
    abstract fun toMap(): Map<String, Any?>

    data class Text(
        override val id: String,
        override val content: String,
        override val senderId: String,
        override val timestamp: Long = System.currentTimeMillis(),
        override val replyTo: String? = null,
        override val messageSource: MessageSource? = null,
        val expertReviews: List<ExpertReview> = emptyList(),
        val adoption: Adoption? = null,
        val isVerified: Boolean = false,
        val references: List<String> = emptyList()
    ) : Message() {
        override fun toMap(): Map<String, Any?> = mapOf(
            "id" to id,
            "content" to content,
            "senderId" to senderId,
            "timestamp" to timestamp,
            "replyTo" to replyTo,
            "messageSource" to messageSource?.toMap(),
            "type" to "TEXT",
            "expertReviews" to expertReviews.map { it.toMap() },
            "adoption" to adoption?.toMap(),
            "isVerified" to isVerified,
            "references" to references
        )

        companion object {
            fun fromMap(map: Map<String, Any?>): Message {
                return when (map["type"] as? String) {
                    "TEXT" -> Text(
                        id = map["id"] as? String ?: "",
                        content = map["content"] as? String ?: "",
                        senderId = map["senderId"] as? String ?: "",
                        timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        replyTo = map["replyTo"] as? String,
                        messageSource = (map["messageSource"] as? Map<*, *>)?.let {
                            MessageSource.fromMap(it.mapKeys { entry -> entry.key.toString() })
                        },
                        expertReviews = (map["expertReviews"] as? List<*>)?.mapNotNull {
                            (it as? Map<*, *>)?.let { reviewMap ->
                                ExpertReview.fromMap(reviewMap.mapKeys { entry -> entry.key.toString() })
                            }
                        } ?: emptyList(),
                        adoption = (map["adoption"] as? Map<*, *>)?.let {
                            Adoption.fromMap(it.mapKeys { entry -> entry.key.toString() })
                        },
                        isVerified = map["isVerified"] as? Boolean ?: false,
                        references = (map["references"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    )
                    else -> throw IllegalArgumentException("Unknown message type: ${map["type"]}")
                }
            }
        }
    }
}

@Serializable
data class MessageSource(
    val type: SourceType,
    val model: AIModel? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "type" to type.name,
        "model" to model?.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): MessageSource {
            return MessageSource(
                type = SourceType.valueOf(map["type"] as? String ?: SourceType.USER.name),
                model = (map["model"] as? String)?.let { modelName ->
                    AIModel.entries.find { it.name == modelName }
                }
            )
        }
    }
}

enum class SourceType {
    USER,
    EXPERT,
    AI
}
