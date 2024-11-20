package mp.verif_ai.data.repository;

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.Notification
import mp.verif_ai.domain.repository.InboxRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InboxRepositoryImpl @Inject constructor(
    // TODO: 필요한 의존성 추가
    // private val notificationApi: NotificationApi,
    // private val notificationDao: NotificationDao,
    // private val connectivityChecker: ConnectivityChecker
) : InboxRepository {

    override suspend fun getNotifications(): List<Notification> {
        // TODO: Implementation
        // 1. 네트워크 연결 확인
        // 2. 연결된 경우 API에서 최신 데이터 가져오기
        // 3. 로컬 DB 업데이트
        // 4. 연결 안 된 경우 로컬 DB에서 데이터 가져오기
        return emptyList()
    }

    override suspend fun markAsRead(notificationId: String) {
        // TODO: Implementation
        // 1. 로컬 DB에서 알림 읽음 표시
        // 2. 네트워크 연결된 경우 서버에 동기화
    }

    override suspend fun markAllAsRead() {
        // TODO: Implementation
        // 1. 로컬 DB의 모든 알림 읽음 표시
        // 2. 네트워크 연결된 경우 서버에 동기화
    }

    override suspend fun deleteNotification(notificationId: String) {
        // TODO: Implementation
        // 1. 로컬 DB에서 알림 삭제
        // 2. 네트워크 연결된 경우 서버에 동기화
    }

    override fun observeNotifications(): Flow<List<Notification>> {
        // TODO: Implementation
        // 1. 로컬 DB의 알림 목록을 Flow로 관찰
        // 2. Entity -> Domain 모델 변환
        TODO("Not yet implemented")
    }

    override suspend fun getUnreadCount(): Int {
        // TODO: Implementation
        // 1. 로컬 DB에서 읽지 않은 알림 개수 조회
        return 0
    }

    override suspend fun getUnreadCount(userId: String): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun clearAllNotifications(userId: String) {
        TODO("Not yet implemented")
    }
}