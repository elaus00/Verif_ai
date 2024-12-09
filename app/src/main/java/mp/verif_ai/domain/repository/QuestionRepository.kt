package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.TrendingQuestion

interface QuestionRepository {
    /**
     * 새로운 질문을 생성합니다.
     * @param question 생성할 질문 정보
     * @return 생성된 질문의 ID
     */
    suspend fun createQuestion(question: Question): Result<String>

    /**
     * 특정 ID의 질문을 조회합니다.
     * @param questionId 조회할 질문 ID
     * @return 질문 정보
     */
    suspend fun getQuestion(questionId: String): Result<Question>

    /**
     * 트렌딩(인기) 질문 목록을 조회합니다.
     * @param limit 조회할 질문 수
     * @return 트렌딩 질문 목록
     */
    suspend fun getTrendingQuestions(limit: Int = 5): Flow<List<TrendingQuestion>>

    /**
     * 특정 사용자의 질문 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param limit 조회할 질문 수
     * @return 사용자의 질문 목록
     */
    suspend fun getMyQuestions(userId: String, limit: Int = 5): Flow<List<Question>>

    /**
     * 질문 정보를 업데이트합니다.
     * @param question 업데이트할 질문 정보
     */
    suspend fun updateQuestion(question: Question): Result<Unit>
}