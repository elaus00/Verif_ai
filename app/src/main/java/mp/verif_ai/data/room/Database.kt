package mp.verif_ai.data.room


import androidx.room.Database
import androidx.room.RoomDatabase
import mp.verif_ai.data.room.dao.NotificationDao
import mp.verif_ai.domain.RoomModel.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 1)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}
