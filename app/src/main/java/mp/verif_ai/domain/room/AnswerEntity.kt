package mp.verif_ai.domain.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class AnswerEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val expertId: String,
    val content: String,
    val type: String,  // Enum은 문자열로 저장
    val status: String, // Enum은 문자열로 저장
    val accuracy: Int?, // Nullable 지원
    val isAdopted: Boolean,
    val helpfulCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)