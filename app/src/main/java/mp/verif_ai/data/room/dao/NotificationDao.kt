package mp.verif_ai.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.data.room.model.NotificationEntity

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    suspend fun getUnreadCount(): Int

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)
}
