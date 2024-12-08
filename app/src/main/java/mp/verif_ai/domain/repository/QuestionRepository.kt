package mp.verif_ai.domain.repository

import kotlinx.coroutines.flow.Flow
import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.model.question.TrendingQuestion

interface QuestionRepository {
    suspend fun createQuestion(question: Question): Result<String>
    suspend fun getQuestion(questionId: String): Result<Question>
    suspend fun getTrendingQuestions(limit: Int = 5): Flow<List<TrendingQuestion>>
    suspend fun getMyQuestions(userId: String, limit: Int = 5): Flow<List<Question>>
    suspend fun updateQuestion(question: Question): Result<Unit>
}
