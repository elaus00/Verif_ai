package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.notification.Notification

interface InboxRepository {
    /**
     * 사용자의 알림 목록을 페이징하여 가져옵니다.
     * 로컬 DB에서 먼저 데이터를 방출하고, 온라인 상태면 서버에서 최신 데이터를 가져와 DB를 업데이트합니다.
     */
    fun getNotifications(
        userId: String,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<List<Notification>>

    /**
     * 특정 그룹에 속한 알림 목록을 가져옵니다.
     * 로컬 DB에서만 데이터를 조회합니다.
     */
    fun getGroupedNotifications(groupId: String): Flow<List<Notification>>


    /**
     * 지정된 알림들을 읽음 처리합니다.
     * 오프라인 상태면 로컬 DB만 업데이트하고 동기화 작업을 예약합니다.
     */
    suspend fun markAsRead(notificationIds: List<String>): Any?


    /**
     * 모든 알림을 읽음 처리합니다.
     * 오프라인 상태면 로컬 DB만 업데이트하고 동기화 작업을 예약합니다.
     */
    suspend fun markAllAsRead(): Any?

    /**
     * 지정된 알림들을 삭제합니다.
     * 오프라인 상태면 로컬 DB만 업데이트하고 동기화 작업을 예약합니다.
     */
    suspend fun deleteNotifications(notificationIds: List<String>): Any?

    /**
     * 읽지 않은 알림 개수를 가져옵니다.
     * 로컬 DB에서만 데이터를 조회합니다.
     */
    fun getUnreadCount(userId: String): Flow<Int>

    /**
     * 사용자의 모든 알림을 삭제합니다.
     * 오프라인 상태면 로컬 DB만 업데이트하고 동기화 작업을 예약합니다.
     */
    suspend fun clearAllNotifications(userId: String): Any?

    /**
     * 로컬 DB와 서버 데이터를 동기화합니다.
     * 온라인 상태에서만 동작합니다.
     */
    suspend fun sync()
}