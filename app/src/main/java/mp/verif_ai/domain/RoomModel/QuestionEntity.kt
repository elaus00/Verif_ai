package mp.verif_ai.domain.RoomModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val status: String, // Store as a string, or use an enum type converter
    val category: String,
    val isPublic: Boolean,
    val reward: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val author: String
)


