package mp.verif_ai.domain.usecase.question

import mp.verif_ai.domain.model.question.Question
import mp.verif_ai.domain.repository.QuestionRepository
import javax.inject.Inject

class CreateQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(question: Question): Result<String> {
        return questionRepository.createQuestion(question)
    }
}

class GetQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(questionId: String): Result<Question> {
        return questionRepository.getQuestion(questionId)
    }
}

class GetTrendingQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(limit: Int = 5) =
        questionRepository.getTrendingQuestions(limit)
}

class GetMyQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(userId: String, limit: Int = 5) =
        questionRepository.getMyQuestions(userId, limit)
}

class UpdateQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(question: Question): Result<Unit> {
        return questionRepository.updateQuestion(question)
    }
}