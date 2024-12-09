package mp.verif_ai.domain.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import mp.verif_ai.data.util.MapConverter

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["userId", "timestamp"]),
        Index(value = ["groupId"]),
        Index(value = ["isRead"])
    ]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val priority: Int,
    val userId: String,
    val groupId: String?,
    val deepLink: String,
    @TypeConverters(MapConverter::class)
    val metadata: Map<String, Any>?,
    val typeMetadata: String // JSON string for type-specific data
)
