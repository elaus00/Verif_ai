package mp.verif_ai.data.repository.point

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.payment.PointTransaction
import mp.verif_ai.domain.model.payment.TransactionType
import mp.verif_ai.domain.repository.AuthRepository
import mp.verif_ai.domain.repository.PointRepository
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : PointRepository {

    private val transactionsCollection = firestore.collection("transactions")
    private val usersCollection = firestore.collection("users")

    override suspend fun recordTransaction(
        userId: String,
        amount: Int,
        type: TransactionType,
        relatedId: String?
    ): Result<Unit> = runCatching {
        val transaction = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "type" to type.name,
            "timestamp" to Date(),
            "relatedId" to relatedId
        )

        firestore.runTransaction { transaction ->
            val userRef = usersCollection.document(userId)
            val userSnapshot = transaction.get(userRef)
            val currentPoints = userSnapshot.getLong("points")?.toInt() ?: 0
            val newPoints = currentPoints + amount

            if (newPoints < 0) {
                throw IllegalStateException("포인트가 부족합니다")
            }

            transaction.update(userRef, "points", newPoints)
            transaction.set(transactionsCollection.document(), transaction)
        }.await()
    }

    override suspend fun getTransactionHistory(userId: String): Flow<List<PointTransaction>> = flow {
        val snapshot = transactionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val transactions = snapshot.documents.mapNotNull { doc ->
            try {
                PointTransaction(
                    id = doc.id,
                    userId = doc.getString("userId") ?: return@mapNotNull null,
                    amount = doc.getLong("amount")?.toInt() ?: return@mapNotNull null,
                    type = TransactionType.valueOf(doc.getString("type") ?: return@mapNotNull null),
                    timestamp = doc.getDate("timestamp")?.time
                        ?: return@mapNotNull null,  // Date를 Long으로 변환
                    relatedId = doc.getString("relatedId"),
                    description = TODO(),
                    status = TODO()
                )
            } catch (e: Exception) {
                null
            }
        }
        emit(transactions)
    }.catch { e ->
        emit(emptyList())
    }.flowOn(dispatcher)

    // 추후 확인 필요
    override suspend fun observeUserPoints(userId: String): Flow<Int> = callbackFlow {
        val userDoc = usersCollection.document(userId)
        val subscription = userDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val points = snapshot?.getLong("points")?.toInt() ?: 0
            trySend(points)
        }

        // Flow collection이 끝날 때 listener 해제
        awaitClose { subscription.remove() }
    }.catch { e ->
        emit(0)
    }.flowOn(dispatcher)

    override suspend fun getUserPoints(): Result<Int> = runCatching {
        val user = authRepository.getCurrentUser()
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")

        val userDoc = usersCollection.document(user.id).get().await()
        userDoc.getLong("points")?.toInt() ?: 0
    }

    companion object {
        private const val TAG = "PointRepository"
    }
}