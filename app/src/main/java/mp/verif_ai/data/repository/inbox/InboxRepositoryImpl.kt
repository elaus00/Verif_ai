package mp.verif_ai.data.repository.inbox

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.data.room.dao.NotificationDao
import mp.verif_ai.data.room.dao.toDomain
import mp.verif_ai.data.util.FirestoreErrorHandler
import mp.verif_ai.data.util.NetworkMonitor
import mp.verif_ai.data.util.NotificationSyncWorker
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.notification.Notification
import mp.verif_ai.domain.model.notification.NotificationApi
import mp.verif_ai.domain.model.notification.NotificationChange
import mp.verif_ai.domain.repository.InboxRepository
import mp.verif_ai.domain.room.NotificationEntity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InboxRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificationDao: NotificationDao,
    private val networkMonitor: NetworkMonitor,
    private val workManager: WorkManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val errorHandler: FirestoreErrorHandler
) : InboxRepository {

    private val notificationsCollection = firestore.collection("notifications")

    override fun getNotifications(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<List<Notification>> = flow {
        // 로컬 DB에서 데이터 방출
        emitAll(
            notificationDao.getPagedNotifications(userId, pageSize, page * pageSize)
                .map { entities -> entities.map { it.toDomain() } }
        )

        // 온라인 상태면 Firestore에서 데이터 가져와서 DB 업데이트
        if (networkMonitor.isOnline()) {
            try {
                val notifications = notificationsCollection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc -> doc.toObject(NotificationEntity::class.java) }

                notificationDao.upsertNotifications(notifications)
            } catch (e: Exception) {
                throw errorHandler.handleFirestoreError(e)
            }
        }
    }.flowOn(dispatcher)

    override fun getGroupedNotifications(groupId: String): Flow<List<Notification>> =
        notificationDao.getNotificationsByGroupId(groupId)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(dispatcher)

    override suspend fun markAsRead(notificationIds: List<String>)  = withContext(dispatcher) {
        try {
            // 로컬 DB 업데이트
            notificationDao.updateNotificationsReadStatus(notificationIds, true)

            // 온라인이면 Firestore도 업데이트
            if (networkMonitor.isOnline()) {
                firestore.runBatch { batch ->
                    notificationIds.forEach { id ->
                        batch.update(
                            notificationsCollection.document(id),
                            mapOf(
                                "isRead" to true,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                }.await()
            } else {
                // 오프라인이면 나중에 동기화하도록 작업 예약
                enqueueSyncWork()
            }
        } catch (e: Exception) {
            throw errorHandler.handleFirestoreError(e)
        }
    }

    override suspend fun markAllAsRead() = withContext(dispatcher) {
        try {
            // 로컬 DB 업데이트
            notificationDao.markAllNotificationsAsRead()

            // 온라인이면 Firestore도 업데이트
            if (networkMonitor.isOnline()) {
                val notifications = notificationsCollection
                    .whereEqualTo("isRead", false)
                    .get()
                    .await()

                firestore.runBatch { batch ->
                    notifications.documents.forEach { doc ->
                        batch.update(
                            doc.reference,
                            mapOf(
                                "isRead" to true,
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                }.await()
            } else {
                enqueueSyncWork()
            }
        } catch (e: Exception) {
            throw errorHandler.handleFirestoreError(e)
        }
    }

    override suspend fun deleteNotifications(notificationIds: List<String>) = withContext(dispatcher) {
        try {
            // 로컬 DB에서 삭제
            notificationDao.deleteNotificationsByIds(notificationIds)

            // 온라인이면 Firestore에서도 삭제
            if (networkMonitor.isOnline()) {
                firestore.runBatch { batch ->
                    notificationIds.forEach { id ->
                        batch.delete(notificationsCollection.document(id))
                    }
                }.await()
            } else {
                enqueueSyncWork()
            }
        } catch (e: Exception) {
            throw errorHandler.handleFirestoreError(e)
        }
    }

    override fun getUnreadCount(userId: String): Flow<Int> =
        notificationDao.getUnreadCountByUserId(userId)
            .flowOn(dispatcher)

    override suspend fun clearAllNotifications(userId: String) = withContext(dispatcher) {
        try {
            // 로컬 DB에서 삭제
            notificationDao.deleteAllNotificationsByUserId(userId)

            // 온라인이면 Firestore에서도 삭제
            if (networkMonitor.isOnline()) {
                val notifications = notificationsCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                firestore.runBatch { batch ->
                    notifications.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                }.await()
            } else {
                enqueueSyncWork()
            }
        } catch (e: Exception) {
            throw errorHandler.handleFirestoreError(e)
        }
    }

    override suspend fun sync() = withContext(dispatcher) {
        if (!networkMonitor.isOnline()) return@withContext

        try {
            // 최신 데이터로 로컬 DB 업데이트
            val notifications = notificationsCollection
                .get()
                .await()
                .documents
                .mapNotNull { doc -> doc.toObject(NotificationEntity::class.java) }

            notificationDao.syncNotifications(notifications)
        } catch (e: Exception) {
            throw errorHandler.handleFirestoreError(e)
        }
    }

    private fun enqueueSyncWork() {
        val syncWorkRequest = OneTimeWorkRequestBuilder<NotificationSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )
    }

    companion object {
        private const val SYNC_WORK_NAME = "notification_sync_work"
    }
}