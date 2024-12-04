package mp.verif_ai.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: String, // Store as a string, or use an enum type converter
    val isRead: Boolean,
    val deepLink: String?,
    val createdAt: Long
)