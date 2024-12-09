package mp.verif_ai.data.local.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import mp.verif_ai.data.room.dao.NotificationDao
import mp.verif_ai.data.util.MapConverter
import mp.verif_ai.domain.model.conversation.ConversationRoomEntity
import mp.verif_ai.domain.model.conversation.MessageRoomEntity
import mp.verif_ai.domain.model.conversation.MessageSource
import mp.verif_ai.domain.model.conversation.ParticipantRoomEntity
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.model.question.Adoption
import mp.verif_ai.domain.room.NotificationEntity
import java.util.Date

@Database(
    entities = [
        ConversationRoomEntity::class,
        MessageRoomEntity::class,
        ParticipantRoomEntity::class,
        NotificationEntity::class
    ],
    version = 4,
    exportSchema = true
)

@TypeConverters(
    DateConverter::class,
    JsonConverter::class,
    MapConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun participantDao(): ParticipantDao
    abstract fun notificationDao(): NotificationDao  // 추가

    companion object {
        const val DATABASE_NAME = "verif_ai_db"
    }

    abstract class Callback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }
    }
}

// Converters.kt
object DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

object JsonConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @TypeConverter
    fun fromMessageSource(value: MessageSource?): String? {
        return value?.let { json.encodeToString(MessageSource.serializer(), it) }
    }

    @TypeConverter
    fun toMessageSource(value: String?): MessageSource? {
        return value?.let { json.decodeFromString(MessageSource.serializer(), it) }
    }

    @TypeConverter
    fun fromExpertReviews(value: List<ExpertReview>): String {
        return json.encodeToString(ListSerializer(ExpertReview.serializer()), value)
    }

    @TypeConverter
    fun toExpertReviews(value: String): List<ExpertReview> {
        return json.decodeFromString(ListSerializer(ExpertReview.serializer()), value)
    }

    @TypeConverter
    fun fromAdoption(value: Adoption?): String? {
        return value?.let { json.encodeToString(Adoption.serializer(), it) }
    }

    @TypeConverter
    fun toAdoption(value: String?): Adoption? {
        return value?.let { json.decodeFromString(Adoption.serializer(), it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(ListSerializer(String.serializer()), value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(ListSerializer(String.serializer()), value)
    }
}