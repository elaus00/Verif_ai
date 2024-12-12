package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.answer.Answer
import mp.verif_ai.domain.model.answer.AnswerStatus

interface AnswerRepository {
    /**
     * 새로운 답변을 생성합니다.
     * @param answer 생성할 답변 정보
     * @return 생성된 답변의 ID
     */
    suspend fun createAnswer(answer: Answer): Result<String>

    /**
     * 특정 ID의 답변을 조회합니다.
     * @param answerId 조회할 답변 ID
     * @return 답변 정보
     */
    suspend fun getAnswer(answerId: String): Result<Answer>

    /**
     * 특정 질문의 답변 목록을 조회합니다.
     * @param questionId 질문 ID
     * @return 답변 목록
     */
    suspend fun getAnswersForQuestion(questionId: String): Flow<List<Answer>>

    /**
     * 특정 전문가의 답변 목록을 조회합니다.
     * @param expertId 전문가 ID
     * @param limit 조회할 답변 수
     * @return 전문가의 답변 목록
     */
    suspend fun getExpertAnswers(expertId: String, limit: Int = 10): Flow<List<Answer>>

    /**
     * 답변을 업데이트합니다.
     * @param answer 업데이트할 답변 정보
     */
    suspend fun updateAnswer(answer: Answer): Result<Unit>

    /**
     * 답변의 상태를 변경합니다.
     * @param answerId 답변 ID
     * @param status 변경할 상태
     */
    suspend fun updateAnswerStatus(answerId: String, status: AnswerStatus): Result<Unit>

    /**
     * 답변을 채택합니다.
     * @param answerId 채택할 답변 ID
     * @param questionId 해당 질문 ID
     */
    suspend fun adoptAnswer(answerId: String, questionId: String): Result<Unit>
}