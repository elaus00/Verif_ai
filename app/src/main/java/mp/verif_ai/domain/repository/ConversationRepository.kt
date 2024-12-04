package mp.verif_ai.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.conversation.AIModel
import mp.verif_ai.domain.model.conversation.Conversation
import mp.verif_ai.domain.model.conversation.Message
import mp.verif_ai.domain.model.expert.ExpertReview

interface ConversationRepository {
    /**
     * 특정 대화의 상태를 실시간으로 관찰합니다.
     * 대화 내용, 참여자, 메시지 등의 변경사항이 발생할 때마다 새로운 값을 emit합니다.
     *
     * @param conversationId 관찰할 대화의 고유 ID
     * @return 대화 정보를 포함하는 Flow
     */
    suspend fun observeConversation(conversationId: String): Flow<Conversation>

    /**
     * 대화방에 새 메시지를 전송합니다.
     * 전송 실패 시 최대 3회까지 재시도합니다.
     *
     * @param conversationId 대화방 ID
     * @param message 전송할 메시지
     * @return 성공 시 메시지 ID, 실패 시 에러
     */
    suspend fun sendMessage(conversationId: String, message: Message): Result<String>

    /**
     * 파일을 서버에 업로드합니다.
     * 지원되는 파일 형식: PDF, DOC, DOCX, TXT
     *
     * @param uri 업로드할 파일의 URI
     * @param fileName 저장될 파일명
     * @return 업로드된 파일 정보
     */
    suspend fun uploadFile(uri: Uri, fileName: String): Result<FileInfo>

    /**
     * 이미지를 서버에 업로드합니다.
     * 지원되는 이미지 형식: JPG, PNG, GIF
     *
     * @param uri 업로드할 이미지의 URI
     * @return 업로드된 이미지 정보
     */
    suspend fun uploadImage(uri: Uri): Result<ImageInfo>

    /**
     * 전문가 검토를 요청합니다.
     * 요청 시 필요한 포인트가 차감됩니다.
     *
     * @param conversationId 검토 요청할 대화의 ID
     * @param points 차감될 포인트
     * @return 요청 성공 여부
     */
    suspend fun requestExpertReview(conversationId: String, points: Int): Result<Unit>

    /**
     * 특정 대화에 대한 전문가 리뷰들을 조회합니다.
     *
     * @param conversationId 조회할 대화의 ID
     * @return 전문가 리뷰 리스트를 포함하는 Flow
     */
    suspend fun getExpertReviews(conversationId: String): Flow<List<ExpertReview>>

    /**
     * 사용자의 대화 내역을 페이징하여 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param limit 한 페이지당 조회할 대화 수
     * @param offset 시작 위치 (페이지네이션)
     * @return 대화 내역 리스트
     */
    suspend fun getConversationHistory(
        userId: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Conversation>>

    /**
     * 특정 대화의 전체 내용을 가져옵니다.
     * 대화 정보, 메시지들, 참여자 정보 등이 포함됩니다.
     *
     * @param conversationId 가져올 대화의 ID
     * @return 대화의 전체 정보
     */
    suspend fun getFullConversation(conversationId: String): Result<Conversation>

    suspend fun getAiResponse(
        model: AIModel,
        prompt: String
    ): Flow<String>
}

data class FileInfo(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: Long
)

data class ImageInfo(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)