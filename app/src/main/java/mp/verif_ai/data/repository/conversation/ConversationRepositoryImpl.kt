package mp.verif_ai.data.repository.conversation

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import mp.verif_ai.data.local.dao.*
import mp.verif_ai.di.IoDispatcher
import mp.verif_ai.domain.model.*
import mp.verif_ai.domain.model.conversation.AIModel
import mp.verif_ai.domain.repository.ConversationRepository
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.conversation.ParticipantFirestoreDto
import mp.verif_ai.domain.model.conversation.toRoomEntity
import mp.verif_ai.domain.model.expert.ExpertReview
import mp.verif_ai.domain.repository.FileInfo
import mp.verif_ai.domain.repository.ImageInfo
import mp.verif_ai.domain.util.dto.ConversationFirestoreDto
import java.util.UUID
import kotlin.coroutines.resumeWithException

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val participantDao: ParticipantDao,
    private val scope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ConversationRepository {

    private val conversationsCollection = firestore.collection("conversations")
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    init {
        // Firestore 변경사항 구독 및 Room DB 동기화
        scope.launch(dispatcher) {
            subscribeToFirestoreChanges()
        }
    }

    override suspend fun observeConversation(conversationId: String): Flow<Conversation> = flow {
        // Room DB에서 먼저 데이터 방출
        conversationDao.observeConversationWithDetails(conversationId).collect { localData ->
            emit(localData.toDomainModel())
        }

        // Firestore 실시간 업데이트 구독
        val snapshotListener = suspendCancellableCoroutine<Unit> { continuation ->
            val listener = conversationsCollection.document(conversationId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(error)
                        }
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        scope.launch(dispatcher) {
                            // Firestore 데이터를 Room에 동기화
                            syncConversationToRoom(it)
                        }
                    }
                }

            continuation.invokeOnCancellation {
                listener.remove()
            }
        }
    }.flowOn(dispatcher)

    override suspend fun sendMessage(
        conversationId: String,
        message: Message
    ): Result<String> = withContext(dispatcher) {
        runCatching {
            var retryCount = 0
            var lastError: Exception? = null

            while (retryCount < 3) {
                try {
                    // Firestore에 메시지 저장
                    val messageRef = conversationsCollection
                        .document(conversationId)
                        .collection("messages")
                        .document(message.id)

                    messageRef.set(message.toMap()).await()

                    // Room DB에도 저장
                    messageDao.insertMessage(message.toRoomEntity(conversationId))

                    return@runCatching message.id
                } catch (e: Exception) {
                    lastError = e
                    retryCount++
                    delay(1000 * retryCount.toLong())
                }
            }
            throw lastError ?: Exception("Failed to send message after 3 retries")
        }
    }

    override suspend fun getConversationHistory(
        userId: String,
        limit: Int,
        offset: Int
    ): Result<List<Conversation>> = withContext(dispatcher) {
        runCatching {
            // Room DB에서 먼저 조회
            val localConversations = conversationDao.getConversationsWithDetails(limit, offset)

            if (localConversations.isNotEmpty()) {
                return@runCatching localConversations.map { it.toDomainModel() }
            }

            // Room DB에 없는 경우 Firestore에서 조회
            val firestoreConversations = conversationsCollection
                .whereArrayContains("participants", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            // Firestore 데이터를 Room DB에 동기화
            firestoreConversations.documents.map { doc ->
                syncConversationToRoom(doc)
                doc.toObject(ConversationFirestoreDto::class.java)?.toDomainModel()
            }.filterNotNull()
        }
    }

    private suspend fun syncConversationToRoom(snapshot: DocumentSnapshot) {
        val conversation = snapshot.toObject(ConversationFirestoreDto::class.java)
            ?: return

        withContext(dispatcher) {
            // Room DB에 대화 정보 저장
//            conversationDao.insertConversation(conversation.toRoomEntity())

            // 참가자 정보 동기화
            snapshot.reference.collection("participants")
                .get()
                .await()
                .documents
                .forEach { participantDoc ->
                    val participant = participantDoc.toObject(ParticipantFirestoreDto::class.java)
                    participant?.let {
                        participantDao.insertParticipant(it.toRoomEntity(snapshot.id))
                    }
                }

            // 메시지 정보 동기화
            snapshot.reference.collection("messages")
                .get()
                .await()
                .documents
//                .forEach { messageDoc ->
//                    val message = messageDoc.toObject(MessageFirestoreDto::class.java)
//                    message?.let {
////                        messageDao.insertMessage(it.toRoomEntity(snapshot.id))
//                    }
//                }
        }
    }

    override suspend fun uploadFile(uri: Uri, fileName: String): Result<FileInfo> =
        withContext(dispatcher) {
            runCatching {
                val fileRef = storage.reference.child("files/$fileName")
                val uploadTask = fileRef.putFile(uri).await()

                FileInfo(
                    id = fileRef.name,
                    name = fileName,
                    mimeType = uploadTask.metadata?.contentType ?: "",
                    size = uploadTask.metadata?.sizeBytes ?: 0
                )
            }
        }

    override suspend fun uploadImage(uri: Uri): Result<ImageInfo> = withContext(dispatcher) {
        runCatching {
            val fileName = "images/${UUID.randomUUID()}"
            val imageRef = storage.reference.child(fileName)
            val uploadTask = imageRef.putFile(uri).await()

            val downloadUrl = imageRef.downloadUrl.await()

            ImageInfo(
                id = imageRef.name,
                url = downloadUrl.toString(),
                width = 0, // 실제 구현시 이미지 메타데이터에서 가져와야 함
                height = 0
            )
        }
    }

    override suspend fun getAiResponse(
        model: AIModel,
        prompt: String
    ): Flow<String> = callbackFlow {
        val responseRef = firestore.collection("ai_responses")
            .document()

        val request = hashMapOf(
            "model" to model.name,
            "prompt" to prompt,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // 요청 문서 생성
        responseRef.set(request).await()

        // 응답 리스너 설정
        val listener = responseRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let { doc ->
                // completion 필드가 업데이트될 때마다 방출
                val completion = doc.getString("completion")
                if (completion != null) {
                    trySend(completion)
                }

                // isComplete 플래그로 스트림 종료 여부 확인
                if (doc.getBoolean("isComplete") == true) {
                    close()
                }
            }
        }

        // Flow가 취소될 때 리스너 제거
        awaitClose {
            listener.remove()
        }
    }.flowOn(dispatcher)

    override suspend fun requestExpertReview(
        conversationId: String,
        points: Int
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            val conversationRef = conversationsCollection.document(conversationId)

            firestore.runTransaction { transaction ->
                val conversation = transaction.get(conversationRef)
                    .toObject(ConversationFirestoreDto::class.java)
                    ?: throw IllegalStateException("Conversation not found")

                // 전문가 검토 요청 상태 업데이트
                transaction.update(conversationRef, mapOf(
                    "expertReviewRequested" to true,
                    "expertReviewRequestedAt" to FieldValue.serverTimestamp()
                ))
            }.await()

            Unit  // 명시적으로 Unit 반환
        }
    }

    override suspend fun getExpertReviews(
        conversationId: String
    ): Flow<List<ExpertReview>> = callbackFlow {
//        val listener = conversationsCollection
//            .document(conversationId)
//            .collection("expertReviews")
//            .addSnapshotListener { snapshot, error ->
//                if (error != null) {
//                    close(error)
//                    return@addSnapshotListener
//                }
//
//                val reviews = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(ExpertReviewDto::class.java)?.toDomainModel()
//                } ?: emptyList()
//
//                trySend(reviews)
//            }
//
//        awaitClose { listener.remove() }
    }

    override suspend fun getFullConversation(
        conversationId: String
    ): Result<Conversation> = withContext(dispatcher) {
        runCatching {
            // 먼저 Room DB에서 조회
            val localConversation = conversationDao.getConversationWithDetails(conversationId)
            if (localConversation != null) {
                return@runCatching localConversation.toDomainModel()
            }

            // Room DB에 없으면 Firestore에서 조회
            val conversation = conversationsCollection
                .document(conversationId)
                .get()
                .await()
                .toObject(ConversationFirestoreDto::class.java)
                ?: throw IllegalStateException("Conversation not found")

            // Room DB에 동기화
            syncConversationToRoom(conversation)

            conversation.toDomainModel()
        }
    }

    private suspend fun syncConversationToRoom(conversation: ConversationFirestoreDto) {
//        withContext(dispatcher) {
//            conversationDao.insertConversation(conversation.toRoomEntity())
//        }
    }


    private suspend fun subscribeToFirestoreChanges() {
        conversationsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                snapshot?.documentChanges?.forEach { change ->
                    scope.launch(dispatcher) {
                        syncConversationToRoom(change.document)
                    }
                }
            }
    }
}