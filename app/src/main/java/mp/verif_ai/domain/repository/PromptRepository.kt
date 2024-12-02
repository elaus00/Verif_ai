package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.prompt.PromptResponse
import mp.verif_ai.domain.model.prompt.PromptTemplate
import mp.verif_ai.domain.model.prompt.UserPrompt

interface PromptRepository {
    /**
     * 프롬프트를 전송하고 응답을 스트리밍으로 받습니다.
     * @param prompt 사용자의 프롬프트 입력
     * @return 응답 스트림
     */
    suspend fun sendPrompt(prompt: UserPrompt): Flow<PromptResponse>

    /**
     * 사용 가능한 프롬프트 템플릿 목록을 가져옵니다.
     * @return 템플릿 목록
     */
    suspend fun getPromptTemplates(): Flow<List<PromptTemplate>>

    /**
     * 특정 ID의 프롬프트 템플릿을 가져옵니다.
     * @param templateId 템플릿 ID
     * @return 템플릿 또는 null
     */
    suspend fun getPromptTemplate(templateId: String): PromptTemplate?

    /**
     * 프롬프트 기록을 가져옵니다.
     * @param limit 가져올 기록 수
     * @return 프롬프트 기록 스트림
     */
    suspend fun getPromptHistory(limit: Int = 20): Flow<List<UserPrompt>>
}