package mp.verif_ai.domain.model.question

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val parentId: String = "",
    val parentType: CommentParentType = CommentParentType.QUESTION,
    val reportCount: Int = 0,
    val isReported: Boolean = false,
    val status: CommentStatus = CommentStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "content" to content,
        "authorId" to authorId,
        "authorName" to authorName,
        "parentId" to parentId,
        "parentType" to parentType.name,
        "reportCount" to reportCount,
        "isReported" to isReported,
        "status" to status.name,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Comment = Comment(
            id = map["id"] as? String ?: UUID.randomUUID().toString(),
            content = map["content"] as? String ?: "",
            authorId = map["authorId"] as? String ?: "",
            authorName = map["authorName"] as? String ?: "",
            parentId = map["parentId"] as? String ?: "",
            parentType = try {
                CommentParentType.valueOf(map["parentType"] as? String ?: "")
            } catch (e: IllegalArgumentException) {
                CommentParentType.QUESTION
            },
            reportCount = (map["reportCount"] as? Number)?.toInt() ?: 0,
            isReported = map["isReported"] as? Boolean ?: false,
            status = try {
                CommentStatus.valueOf(map["status"] as? String ?: "")
            } catch (e: IllegalArgumentException) {
                CommentStatus.ACTIVE
            },
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}

enum class CommentParentType {
    QUESTION,
    ANSWER
}

enum class CommentStatus {
    ACTIVE,
    DELETED,
    HIDDEN
}