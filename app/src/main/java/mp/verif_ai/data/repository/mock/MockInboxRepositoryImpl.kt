package mp.verif_ai.data.repository.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import mp.verif_ai.domain.model.Notification
import mp.verif_ai.domain.model.NotificationType
import mp.verif_ai.domain.repository.InboxRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockInboxRepositoryImpl @Inject constructor() : InboxRepository {
    private val notifications = MutableStateFlow<MutableMap<String, Notification>>(mutableMapOf())

    init {
        // 초기 더미 데이터 추가
        val currentTime = System.currentTimeMillis()

        // REPLY 알림들
        addMockNotification(
            type = NotificationType.REPLY,
            questionId = "q1",
            questionTitle = "[모바일프로그래밍] 과제2 RecyclerView 구현 질문",
            actorId = "prof_jinwoo",
            actorName = "정진우 교수님",
            receiverId = "myUserId",
            createdAt = currentTime - 3600000 // 1시간 전
        )

        addMockNotification(
            type = NotificationType.REPLY,
            questionId = "q2",
            questionTitle = "[모바일프로그래밍] 중간고사 3번 문제 관련 질문",
            actorId = "ta_helper",
            actorName = "모프 조교",
            receiverId = "myUserId",
            createdAt = currentTime - 7200000 // 2시간 전
        )

        // COMMENT 알림들
        addMockNotification(
            type = NotificationType.COMMENT,
            questionId = "q1",
            questionTitle = "[모바일프로그래밍] 과제2 RecyclerView 구현 질문",
            actorId = "student1",
            actorName = "김컴공",
            receiverId = "myUserId",
            createdAt = currentTime - 86400000 // 1일 전
        )

        addMockNotification(
            type = NotificationType.COMMENT,
            questionId = "q3",
            questionTitle = "[모바일프로그래밍] Jetpack Compose 보너스 과제 질문",
            actorId = "prof_jinwoo",
            actorName = "정진우 교수님",
            receiverId = "myUserId",
            createdAt = currentTime - 172800000 // 2일 전
        )

        // UPVOTE 알림들
        addMockNotification(
            type = NotificationType.UPVOTE,
            questionId = "q4",
            questionTitle = "[모바일프로그래밍] Fragment 생명주기 정리",
            actorId = "prof_jinwoo",
            actorName = "정진우 교수님",
            receiverId = "myUserId",
            createdAt = currentTime - 259200000 // 3일 전
        )
    }

    // 테스트용 더미 데이터 생성 함수
    fun addMockNotification(
        id: String = UUID.randomUUID().toString(),
        type: NotificationType = NotificationType.REPLY,
        questionId: String = "question_${UUID.randomUUID()}",
        questionTitle: String = "Sample Question Title",
        actorId: String = "actor_${UUID.randomUUID()}",
        actorName: String = "Test User",
        receiverId: String = "receiver_${UUID.randomUUID()}",
        isRead: Boolean = false,
        createdAt: Long = System.currentTimeMillis()
    ): Notification {
        val notification = Notification(
            id = id,
            type = type,
            questionId = questionId,
            questionTitle = questionTitle,
            actorId = actorId,
            actorName = actorName,
            receiverId = receiverId,
            isRead = isRead,
            createdAt = createdAt
        )
        notifications.value = notifications.value.toMutableMap().apply {
            put(id, notification)
        }
        return notification
    }

    override suspend fun getNotifications(): List<Notification> {
        return notifications.value.values.toList().sortedByDescending { it.createdAt }
    }

    override suspend fun markAsRead(notificationId: String) {
        notifications.value = notifications.value.toMutableMap().apply {
            get(notificationId)?.let { notification ->
                put(notificationId, notification.copy(isRead = true))
            }
        }
    }

    override suspend fun markAllAsRead() {
        notifications.value = notifications.value.toMutableMap().apply {
            forEach { (id, notification) ->
                put(id, notification.copy(isRead = true))
            }
        }
    }

    override suspend fun deleteNotification(notificationId: String) {
        notifications.value = notifications.value.toMutableMap().apply {
            remove(notificationId)
        }
    }

    override fun observeNotifications(): Flow<List<Notification>> {
        return notifications.map { notificationMap ->
            notificationMap.values.toList().sortedByDescending { it.createdAt }
        }
    }

    override suspend fun getUnreadCount(): Int {
        return notifications.value.values.count { !it.isRead }
    }

    override suspend fun getUnreadCount(userId: String): Flow<Int> {
        return notifications.map { notificationMap ->
            notificationMap.values.count { !it.isRead && it.receiverId == userId }
        }
    }

    override suspend fun clearAllNotifications(userId: String) {
        notifications.value = notifications.value.toMutableMap().apply {
            values.removeAll { it.receiverId == userId }
        }
    }

    // 테스트용 편의 메소드들

    fun reset() {
        notifications.value = mutableMapOf()
    }

    // 특정 타입의 알림만 추가하는 헬퍼 메소드들
    fun addMockReplyNotification(
        questionId: String = "question_${UUID.randomUUID()}",
        questionTitle: String = "Sample Question",
        actorId: String = "actor_${UUID.randomUUID()}",
        actorName: String = "Replier",
        receiverId: String = "receiver_${UUID.randomUUID()}"
    ) = addMockNotification(
        type = NotificationType.REPLY,
        questionId = questionId,
        questionTitle = questionTitle,
        actorId = actorId,
        actorName = actorName,
        receiverId = receiverId
    )

    fun addMockCommentNotification(
        questionId: String = "question_${UUID.randomUUID()}",
        questionTitle: String = "Sample Question",
        actorId: String = "actor_${UUID.randomUUID()}",
        actorName: String = "Commenter",
        receiverId: String = "receiver_${UUID.randomUUID()}"
    ) = addMockNotification(
        type = NotificationType.COMMENT,
        questionId = questionId,
        questionTitle = questionTitle,
        actorId = actorId,
        actorName = actorName,
        receiverId = receiverId
    )

    fun addMockUpvoteNotification(
        questionId: String = "question_${UUID.randomUUID()}",
        questionTitle: String = "Sample Question",
        actorId: String = "actor_${UUID.randomUUID()}",
        actorName: String = "Voter",
        receiverId: String = "receiver_${UUID.randomUUID()}"
    ) = addMockNotification(
        type = NotificationType.UPVOTE,
        questionId = questionId,
        questionTitle = questionTitle,
        actorId = actorId,
        actorName = actorName,
        receiverId = receiverId
    )
}