package mp.verif_ai.data.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mp.verif_ai.domain.model.ExpertProfile
import mp.verif_ai.domain.model.User
import mp.verif_ai.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Firebase Firestore를 사용하여 사용자 정보를 관리하는 Repository 구현체입니다.
 * 이 클래스는 사용자 프로필 관리, 포인트 관리, 전문가 프로필 관리 등
 * 사용자 데이터와 관련된 모든 작업을 처리합니다.
 *
 * @property firestore Firestore 데이터베이스 인스턴스
 * @property auth Firebase Authentication 인스턴스 (현재 사용자 정보 조회용)
 */
class FirebaseUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    /**
     * User 컬렉션에 대한 참조입니다.
     * 사용자의 프로필 정보를 저장하고 관리하는데 사용됩니다.
     */
    private val usersCollection = firestore.collection("User")

    /**
     * 현재 로그인된 사용자의 정보를 실시간으로 모니터링하여 Flow로 제공합니다.
     * Firestore의 실시간 리스너를 사용하여 사용자 정보 변경을 감지합니다.
     *
     * @return 현재 사용자 정보를 담은 Flow
     */
    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // 사용자가 로그인된 경우에만 Firestore 리스너 설정
                val subscription = usersCollection.document(currentUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        val user = snapshot?.toObject(User::class.java)
                        trySend(user)
                    }
            } else {
                // 로그아웃 상태면 null 전송
                trySend(null)
            }
        }

        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    /**
     * 특정 사용자의 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자 정보를 담은 Result
     * @throws IllegalStateException 사용자를 찾을 수 없는 경우
     */
    override suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: throw IllegalStateException("User not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 사용자 프로필 정보를 업데이트합니다.
     * 업데이트 시 자동으로 updatedAt 시간이 현재 시간으로 설정됩니다.
     *
     * @param user 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보를 담은 Result
     */
    override suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val updatedUser = user.copy(updatedAt = System.currentTimeMillis())
            usersCollection.document(user.id)
                .set(updatedUser.toMap())
                .await()

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 사용자의 포인트를 실시간으로 모니터링합니다.
     * Firestore의 실시간 리스너를 사용하여 포인트 변경을 감지합니다.
     *
     * @param userId 포인트를 모니터링할 사용자의 ID
     * @return 사용자의 포인트를 담은 Flow
     */
    override fun getUserPoints(userId: String): Flow<Int> = callbackFlow {
        val subscription = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val points = snapshot?.getLong("points")?.toInt() ?: 0
                trySend(points)
            }

        awaitClose { subscription.remove() }
    }

    /**
     * 전문가 프로필을 생성하거나 업데이트합니다.
     * ExpertProfile 컬렉션에 프로필 정보를 저장합니다.
     *
     * @param userId 전문가 프로필을 생성할 사용자의 ID
     * @param profile 생성할 전문가 프로필 정보
     * @return 생성된 전문가 프로필을 담은 Result
     */
    override suspend fun createExpertProfile(
        userId: String,
        profile: ExpertProfile
    ): Result<ExpertProfile> = withContext(Dispatchers.IO) {
        try {
            val expertProfilesCollection = firestore.collection("ExpertProfile")

            expertProfilesCollection.document(userId)
                .set(profile)
                .await()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}