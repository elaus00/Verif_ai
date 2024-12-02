package mp.verif_ai.domain.model.extension

import com.google.firebase.firestore.DocumentSnapshot
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.auth.User
import mp.verif_ai.domain.model.auth.UserStatus
import mp.verif_ai.domain.model.auth.UserType

fun User.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "email" to email,
    "phoneNumber" to phoneNumber,
    "nickname" to nickname,
    "type" to type.name,
    "status" to status.name,
    "points" to points,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

fun Question.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "title" to title,
    "content" to content,
    "status" to status.name,
    "category" to category,
    "isPublic" to isPublic,
    "reward" to reward,
    "attachments" to attachments,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

fun DocumentSnapshot.toUser(): User? {
    return try {
        val data = data ?: return null
        User(
            id = id,
            email = data["email"] as String,
            phoneNumber = data["phoneNumber"] as String?,
            nickname = data["nickname"] as String,
            type = UserType.valueOf(data["type"] as String),
            status = UserStatus.valueOf(data["status"] as String),
            points = (data["points"] as Long).toInt(),
            createdAt = data["createdAt"] as Long,
            updatedAt = data["updatedAt"] as Long
        )
    } catch (e: Exception) {
        null
    }
}